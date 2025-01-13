/*
 * Copyright 2024-2025 T. Repasi <rtib@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rtib.cmc;

import com.datastax.oss.driver.api.core.CqlSession;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import static io.github.rtib.cmc.PropertyHelper.CONFIG_ROOT_SECTION;
import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.DaoSystem;
import io.github.rtib.cmc.model.DaoSystemSchema;
import io.github.rtib.cmc.model.DaoSystemViews;
import io.github.rtib.cmc.model.DaoSystemVirtualSchema;
import io.github.rtib.cmc.model.MapperSystem;
import io.github.rtib.cmc.model.MapperSystemSchema;
import io.github.rtib.cmc.model.MapperSystemViews;
import io.github.rtib.cmc.model.MapperSystemVirtualSchema;
import io.github.rtib.cmc.model.system.SystemInfo;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application context.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public final class Context {
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);
    private static final Context instance = new Context();

    private static final String CONFIG_RELOAD_INTERVAL = "config-reload-interval";
    
    private final ScheduledExecutorService adminExecutor;
    private ScheduledFuture<?> configReloadScheduler;

    /**
     * Get the singleton instance of this class.
     * @return the Context instance
     */
    public static Context getInstance() {
        return instance;
    }
    
    public Config rootConfig;
    public CqlSession cqlSession;
    public SystemInfo systemInfo;
    public DaoSystem systemDao;
    public DaoSystemVirtualSchema systemVirtualSchemaDao;
    public DaoSystemViews systemViewsDao;
    public DaoSystemSchema systemSchemaDao;
    public ScheduledExecutorService queryExecutor;
    public final Properties projectProperties;
    public List<Label> commonLabels = Collections.emptyList();
    private Duration configReloadInterval = Duration.ZERO;

    /**
     * Context startup. This is creating the CQL session and setting up the
     * thread pool executing CQL queries.
     * @throws ContextException 
     */
    void startup() throws ContextException {
        loadConfig();
        cqlConnect();
        queryExecutor = new ScheduledThreadPoolExecutor(getConfigFor("queryExecutor").getInt("corePoolSize"));
        
        InetSocketAddress node = new InetSocketAddress(
                systemInfo.listen_address(),
                systemInfo.listen_port()
        );
        node.getHostName(); // just resolve, no need to store the result here
        try {
            commonLabels = new LabelListBuilder()
                    .addLabel("cluster", systemInfo.cluster_name())
                    .addLabel("dc", systemInfo.data_center())
                    .addLabel("rack", systemInfo.rack())
                    .addLabel("node", node.toString())
                    .build();
        } catch (MetricException ex) {
            LOG.atError().log("Failed to build common labels.", ex);
            throw new ContextException("Context startup failed.", ex);
        }
    }

    /**
     * Shut the context down. This is shutting down admin task thread pool,
     * query executor thread pool and closing the CQL session.
     */
    void shutdown() {
        if ((adminExecutor != null) && !adminExecutor.isShutdown())
            adminExecutor.shutdown();
        
        if ((queryExecutor != null) && !queryExecutor.isShutdown())
            queryExecutor.shutdown();
        
        if ((cqlSession != null) && !cqlSession.isClosed())
            cqlSession.close();
        
        commonLabels = Collections.emptyList();
    }

    /**
     * Get access to a configuration section.
     * @param section to be accessed
     * @return the Config object of the requested section
     */
    public Config getConfigFor(String section) {
        LOG.debug("Getting config for section name {}", section);
        return rootConfig.getConfig(section);
    }
    
    /**
     * Get access to the configuration of a specified class.
     * @param clazz class which configuration is to be accessed
     * @return the Config object of the requested class
     */
    public Config getConfigFor(Class<?> clazz) {
        LOG.debug("Getting config for class {}", clazz.getName());
        return rootConfig.getConfig(clazz.getName());
    }

    private Context() {
        projectProperties = new Properties();
        try {
            projectProperties.load(CqlMetricsCollectorDaemon.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException ex) {
            LOG.atError().setCause(ex).log("Failed to load project properties");
        }
        this.adminExecutor = new ScheduledThreadPoolExecutor(1);
    }

    private void cqlConnect() throws ContextException {
        if (cqlSession == null || cqlSession.isClosed()) {
            InetSocketAddress contactPoint;
            try {
                contactPoint = new InetSocketAddress(Inet4Address.getLocalHost(), 9042);
            } catch (UnknownHostException ex) {
                LOG.error("Failed to get local hostname.", ex);
                throw new ContextException("Failed to get local hostname.", ex);
            }

            if (rootConfig.hasPath("node")) {
                URI parsedCP;
                try {
                    parsedCP = new URI(null, rootConfig.getString("node"), null, null, null);
                } catch (URISyntaxException ex) {
                    throw new ContextException("Failed startup, parsing cql-metrics-collector.node configuration value.", ex);
                }
                contactPoint = new InetSocketAddress(parsedCP.getHost(), parsedCP.getPort());                
            }

            cqlSession = CqlSession.builder()
                    .addContactPoint(contactPoint)
                    .withNodeDistanceEvaluator(new NodeDiscrimiator(contactPoint))
                    .withApplicationName(projectProperties.getProperty("application-name"))
                    .withApplicationVersion(projectProperties.getProperty("application-version"))
                    .build();
            
            this.systemDao = MapperSystem
                    .builder(cqlSession)
                    .build()
                    .systemDao();
            this.systemVirtualSchemaDao = MapperSystemVirtualSchema
                    .builder(cqlSession)
                    .build()
                    .systemVirtualSchemaDao();
            this.systemViewsDao = MapperSystemViews
                    .builder(cqlSession)
                    .build()
                    .systemViewsDao();
            this.systemSchemaDao = MapperSystemSchema
                    .builder(cqlSession)
                    .build()
                    .systemSchemaDao();
            
            this.systemInfo = systemDao.getLocalInfo();
        }
    }

    private void loadConfig() {
        LOG.info("Loading root configuration section: {}", CONFIG_ROOT_SECTION.getString());
        ConfigFactory.invalidateCaches();
        rootConfig = ConfigFactory.load().getConfig(CONFIG_ROOT_SECTION.getString());
        
        if (configReloadInterval != rootConfig.getDuration(CONFIG_RELOAD_INTERVAL)) {
            configReloadInterval = rootConfig.getDuration(CONFIG_RELOAD_INTERVAL);

            // Cancel if running
            if (configReloadScheduler != null)
                configReloadScheduler.cancel(false);

            // Create if configured
            if (configReloadInterval.toSeconds() > 0) {
                LOG.info("Setting up scheduled config reload for {}", configReloadInterval);
                this.configReloadScheduler = adminExecutor.scheduleWithFixedDelay(
                    new Thread(() -> loadConfig()),
                    configReloadInterval.toSeconds(),
                    configReloadInterval.toSeconds(),
                    TimeUnit.SECONDS);
            }
        }
    }
}
