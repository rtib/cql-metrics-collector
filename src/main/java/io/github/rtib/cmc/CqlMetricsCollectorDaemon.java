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

import static io.github.rtib.cmc.PropertyHelper.JAVA_VERSION;
import static io.github.rtib.cmc.PropertyHelper.JAVA_VM_NAME;
import io.github.rtib.cmc.collectors.BatchMetricsCollector;
import io.github.rtib.cmc.collectors.CachesCollector;
import io.github.rtib.cmc.collectors.CollectorException;
import io.github.rtib.cmc.collectors.CoordinatorReadLatencyCollector;
import io.github.rtib.cmc.collectors.CoordinatorScanLatencyCollector;
import io.github.rtib.cmc.collectors.CoordinatorWriteLatencyCollector;
import io.github.rtib.cmc.collectors.CqlMetricsCollector;
import io.github.rtib.cmc.collectors.DiskUsageCollector;
import io.github.rtib.cmc.collectors.LocalReadLatencyCollector;
import io.github.rtib.cmc.collectors.LocalScanLatencyCollector;
import io.github.rtib.cmc.collectors.LocalWriteLatencyCollector;
import io.github.rtib.cmc.collectors.MaxPartitionSizeCollector;
import io.github.rtib.cmc.collectors.MaxSstableSizeCollector;
import io.github.rtib.cmc.collectors.RowsPerReadCollector;
import io.github.rtib.cmc.collectors.ThreadPoolsCollector;
import io.github.rtib.cmc.collectors.TombstonesPerReadCollector;
import io.github.rtib.cmc.exporter.HTTPServer;
import io.github.rtib.cmc.exporter.HTTPServerException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class CqlMetricsCollectorDaemon {
    private static final Logger LOG;
    private static final CqlMetricsCollectorDaemon instance;
    
    private Context context;
    private HTTPServer httpServer;
    
    static {
        LOG = LoggerFactory.getLogger(CqlMetricsCollectorDaemon.class);
        instance = new CqlMetricsCollectorDaemon();
    }

    private CqlMetricsCollectorDaemon() {
    }
    
    /**
     * Main program entry point.
     * @param args command line argument list
     */
    public static void main(String... args) {
        instance.activate();
        
        // ToDo: put collector activation into a recurring task of admin executor
        try{
            new DiskUsageCollector().activate();
            new ThreadPoolsCollector().activate();
            new CachesCollector().activate();
            new CoordinatorReadLatencyCollector().activate();
            new CoordinatorWriteLatencyCollector().activate();
            new CoordinatorScanLatencyCollector().activate();
            new LocalReadLatencyCollector().activate();
            new LocalWriteLatencyCollector().activate();
            new LocalScanLatencyCollector().activate();
            new TombstonesPerReadCollector().activate();
            new RowsPerReadCollector().activate();
            new BatchMetricsCollector().activate();
            new MaxPartitionSizeCollector().activate();
            new CqlMetricsCollector().activate();
            new MaxSstableSizeCollector().activate();
        } catch (CollectorException ex) {
            LOG.debug("Failed to initialize DiskUsageCollector.", ex);
        }
        
        // ToDo: make this a real daemon threadpool
        while(true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(CqlMetricsCollectorDaemon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void activate() {
        context = Context.getInstance();
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> instance.deactivate()));
        LOG.info("Starting {} v{}",
                context.projectProperties.getProperty("application-name"),
                context.projectProperties.getProperty("application-version")
                );
        LOG.info("JVM vendor/version: {}/{}",
                JAVA_VM_NAME.getString(),
                JAVA_VERSION.getString()
        );
        
        try {
            context.startup();
        } catch (ContextException ex) {
            LOG.atError().log("Failed to activate application context. Aborting.", ex);
            deactivate();
            System.exit(-1);
        }
        logClusterInfo();
        try {
            httpServer = new HTTPServer.Builder().build().start();
        } catch (HTTPServerException ex) {
            LOG.atError().log("Failed to start HTTP server.", ex);
        }
    }
    
    private void deactivate() {
        LOG.info("Shutting down.");
        context.shutdown();
        if (httpServer != null)
            httpServer.stop();
    }

    private void logClusterInfo() {
        LOG.info("Connected to Cassandra cluster: {}", context.systemInfo.cluster_name());
        LOG.info("Cassandra version: {}", context.systemInfo.release_version());
        LOG.info("DC/Rack: {}/{}",
                context.systemInfo.data_center(),
                context.systemInfo.rack());
    }
}
