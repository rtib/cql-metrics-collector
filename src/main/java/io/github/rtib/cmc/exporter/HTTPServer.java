/*
 * Copyright 2024-2025 Tibor Répási <rtib@users.noreply.github.com>.
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
package io.github.rtib.cmc.exporter;

import com.sun.net.httpserver.HttpServer;
import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.Context;
import static io.github.rtib.cmc.PropertyHelper.HTTP_MAX_REQ_TIME;
import static io.github.rtib.cmc.PropertyHelper.HTTP_MAX_RSP_TIME;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP server exporting the metrics.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class HTTPServer implements Closeable {

    private static final Config config = ConfigBeanFactory.create(Context.getInstance().getConfigFor(HTTPServer.class), Config.class);
    private static final Logger LOG = LoggerFactory.getLogger(HTTPServer.class);

    static {
        HTTP_MAX_REQ_TIME.setSeconds(config.maxReqTime());
        HTTP_MAX_RSP_TIME.setSeconds(config.maxRspTime());
    }
    
    private final HttpServer server;
    private final ExecutorService threadpool;
    
    private HTTPServer (
            HttpServer server,
            ExecutorService threadpool
    ) {
        this.server = server;
        this.threadpool = threadpool;
        this.server.createContext("/", new RootHander());
        this.server.createContext("/metrics", new MetricsHandler());
    }

    /**
     * Start the service instance.
     * @return the server instance
     */
    public HTTPServer start() {
        threadpool.submit(this.server::start);
        LOG.atDebug().log("HTTPServer started.");
        return this;
    }
    
    /**
     * Shut down the server instance.
     */
    public void stop() {
        close();
    }
    
    @Override
    public void close() {
        LOG.atDebug().log("HTTPServer shutting down.");
        server.stop(0);
        threadpool.shutdown();
    }
    
    /**
     * Configuration bean.
     */
    public static final class Config {
        private final int port = 9500;
        private final int minThreads = 1;
        private final int maxThreads = 10;
        private final Duration keepalive = Duration.ofSeconds(120);
        private final Duration maxReqTime = Duration.ofSeconds(60);
        private final Duration maxRspTime = Duration.ofSeconds(600);

        /**
         * Default constructor.
         */
        public Config() {
        }
        
        /**
         * Port the HTTP server should listen on.
         * @return port number
         */
        public int port() {
            return this.port;
        }
        
        /**
         * Lower bound of server threads hold available.
         * @return number of threads
         */
        public int minThreads() {
            return this.minThreads;
        }
        
        /**
         * Upper limit of server threads to be created.
         * @return number of threads
         */
        public int maxThreads() {
            return this.maxThreads;
        }
        
        /**
         * Duration to keep alive idle thread before throw away.
         * @return keep alive duration
         */
        public Duration keepalive() {
            return this.keepalive;
        }
        
        /**
         * Max time allowed for a request transmission.
         * @return Duration of a HTTP request transmission
         */
        public Duration maxReqTime() {
            return this.maxReqTime;
        }
        
        /**
         * Max time allowed for a response transmission.
         * @return Duration of a HTTP response transmission
         */
        public Duration maxRspTime() {
            return this.maxRspTime;
        }
    }
    
    /**
     * Builder class
     */
    public static class Builder {
        
        /**
         * Create the builder.
         */
        public Builder() {
        }
        
        /**
         * Build the HTTPServer instance.
         * @return the created instance
         * @throws HTTPServerException wrapping IOException of socket creation
         */
        public HTTPServer build() throws HTTPServerException {
            InetSocketAddress listen = new InetSocketAddress(config.port());
            LOG.info("Building HTTP server listening on {}", listen);
            try {
                return new HTTPServer(
                        HttpServer.create(listen, 0),
                        makeThreadPool()
                );
            } catch (IOException ex) {
                throw new HTTPServerException("Failed to create HttpServer.", ex);
            }
        }

        private ExecutorService makeThreadPool() {
            return new ThreadPoolExecutor(
                    config.minThreads(),
                    config.maxThreads(),
                    config.keepalive().get(ChronoUnit.SECONDS),
                    TimeUnit.SECONDS,
                    new SynchronousQueue<>(true),
                    Executors.defaultThreadFactory(),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }
}
