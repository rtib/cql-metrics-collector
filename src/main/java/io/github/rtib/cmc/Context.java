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
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeStateListener;
import com.datastax.oss.driver.api.core.metadata.SafeInitNodeStateListener;
import com.datastax.oss.driver.api.core.session.Session;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import static io.github.rtib.cmc.PropertyHelper.CONFIG_ROOT_SECTION;
import io.github.rtib.cmc.collectors.CollectorException;
import io.github.rtib.cmc.collectors.ICollector;
import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.DaoSystem;
import io.github.rtib.cmc.model.DaoSystemSchema;
import io.github.rtib.cmc.model.DaoSystemVirtualSchema;
import io.github.rtib.cmc.model.MapperSystem;
import io.github.rtib.cmc.model.MapperSystemSchema;
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
import java.util.ServiceLoader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application context.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public final class Context implements NodeStateListener {

    /**
     * Get the singleton instance of this class.
     * @return the Context instance
     */
    public static Context getInstance() {
        return instance;
    }
    
    /**
     * Configuration object to access the root section of application config.
     */
    public Config rootConfig;
    
    /**
     * CQL session object connected to Apache Cassandra.
     */
    public CqlSession cqlSession;
    
    /**
     * System information entity.
     */
    public SystemInfo systemInfo;
    
    /**
     * DAO to access system keyspace.
     */
    public DaoSystem systemDao;
    
    /**
     * DAO to access system_virtual_schema keyspace.
     */
    public DaoSystemVirtualSchema systemVirtualSchemaDao;
    
    /**
     * DAO to access system_schema keyspace.
     */
    public DaoSystemSchema systemSchemaDao;
    
    /**
     * Thread pool executor service for scheduled CQL queries.
     */
    public ScheduledExecutorService queryExecutor;
    
    /**
     * Project properties `application-name` and `application-version`
     */
    public final Properties projectProperties;
    
    /**
     * List of labels which will be applied to all metric instances.
     */
    public List<Label> commonLabels = Collections.emptyList();

    /**
     * Context startup. This is creating the CQL session and setting up the
     * thread pool executing CQL queries.
     * @throws ContextException 
     */
    public void startup() throws ContextException {
        loadConfig();
        cqlConnect();
    }

    /**
     * Shut the context down. This is shutting down admin task thread pool,
     * query executor thread pool and closing the CQL session.
     */
    public void shutdown() {
        if ((adminScheduledTaskExecutor != null) && !adminScheduledTaskExecutor.isShutdown())
            adminScheduledTaskExecutor.shutdown();

        if ((adminTaskExecutor != null) && !adminTaskExecutor.isShutdown())
            adminTaskExecutor.shutdown();
        
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
    
    private static final Logger LOG = LoggerFactory.getLogger(Context.class);
    private static final Context instance = new Context();
    private static final String CONFIG_RELOAD_INTERVAL = "config-reload-interval";
    private final ScheduledExecutorService adminScheduledTaskExecutor;
    private final ExecutorService adminTaskExecutor;
    
    private InetSocketAddress contactPoint;
    private ScheduledFuture<?> configReloadScheduler;
    private Duration configReloadInterval = Duration.ZERO;

    private Context() {
        projectProperties = new Properties();
        try {
            projectProperties.load(CqlMetricsCollectorDaemon.class.getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException ex) {
            LOG.atError().setCause(ex).log("Failed to load project properties");
        }
        adminScheduledTaskExecutor = new ScheduledThreadPoolExecutor(1);
        adminTaskExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue(5, true));
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
                configReloadScheduler = adminScheduledTaskExecutor.scheduleWithFixedDelay(
                    new Thread(() -> loadConfig()),
                    configReloadInterval.toSeconds(),
                    configReloadInterval.toSeconds(),
                    TimeUnit.SECONDS);
            }
        }
    }
    
    private void cqlConnect() throws ContextException {
        if (cqlSession == null || cqlSession.isClosed()) {
            if (rootConfig.hasPath("node")) {
                URI parsedCP;
                try {
                    parsedCP = new URI(null, rootConfig.getString("node"), null, null, null);
                } catch (URISyntaxException ex) {
                    throw new ContextException("Failed startup, parsing cql-metrics-collector.node configuration value.", ex);
                }
                contactPoint = new InetSocketAddress(parsedCP.getHost(), parsedCP.getPort());                
            } else {
                try {
                    contactPoint = new InetSocketAddress(Inet4Address.getLocalHost().getCanonicalHostName(), 9042);
                } catch (UnknownHostException ex) {
                    LOG.error("Failed to get local hostname.", ex);
                    throw new ContextException("Failed to get local hostname.", ex);
                }    
            }
            
            LOG.info("Connecting Cassandra node at {}", contactPoint);

            CqlSession.builder()
                .addContactPoint(contactPoint)
                .addNodeStateListener(new SafeInitNodeStateListener(this, false))
                .withNodeDistanceEvaluator(new NodeDiscrimiator(contactPoint))
                .withApplicationName(projectProperties.getProperty("application-name"))
                .withApplicationVersion(projectProperties.getProperty("application-version"))
                .build();
        }
    }
     
    protected final class CollectorActivator implements Runnable {

        @Override
        public void run() {
            // ToDo: put collector activation into a recurring task of admin executor
            for (ICollector collector : ServiceLoader.load(ICollector.class)) {
                LOG.debug("Collector {} is enabled: {}", collector.getClass().getSimpleName(), collector.isEnabled());
                if (!collector.isEnabled())
                    continue;
                
                try {
                    LOG.info("Activating collector: {}, enabled: {}", collector.getClass().getSimpleName(), collector.isEnabled());
                    collector.activate();
                } catch (CollectorException ex) {
                    LOG.atWarn().setCause(ex).log("Failed to activate {}", collector.getClass().getSimpleName());
                }
            }
        }        
    }
        
    protected final class SessionSetup implements Runnable {

        @Override
        public void run() {
            systemDao = MapperSystem
                    .builder(cqlSession)
                    .build()
                    .systemDao();
            systemVirtualSchemaDao = MapperSystemVirtualSchema
                    .builder(cqlSession)
                    .build()
                    .systemVirtualSchemaDao();
            systemSchemaDao = MapperSystemSchema
                    .builder(cqlSession)
                    .build()
                    .systemSchemaDao();

            systemInfo = systemDao.getLocalInfo();

            LOG.info("Connected to Cassandra cluster: {}", systemInfo.cluster_name());
            LOG.info("Cassandra version: {}", systemInfo.release_version());
            LOG.info("DC/Rack: {}/{}",
                systemInfo.data_center(),
                systemInfo.rack());

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
            }
            queryExecutor = new ScheduledThreadPoolExecutor(getConfigFor("queryExecutor").getInt("corePoolSize"));
            adminTaskExecutor.execute(new CollectorActivator());
        }   
    }

    @Override
    public void onAdd(Node node) {
        // that's simply not of interest here
    }

    @Override
    public void onUp(Node node) {
        // that's simply not of interest here
    }

    @Override
    public void onDown(Node node) {
        // ToDo: add handler here
    }

    @Override
    public void onRemove(Node node) {
        // ToDo: add handler here
    }

    @Override
    public void onSessionReady(Session session) {
        cqlSession = (CqlSession) session;
        adminTaskExecutor.execute(new SessionSetup());
    }

    @Override
    public void close() throws Exception {
        // that's simply not of interest here
    }
}
