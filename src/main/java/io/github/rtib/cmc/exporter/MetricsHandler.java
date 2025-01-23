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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.Repository;
import java.io.IOException;
import java.io.OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prometheus metrics endpoint handler.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class MetricsHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MetricsHandler.class);
    private final static String contentType = "text/plain; charset=utf-8";
    private final static Repository repo = Repository.getInstance();

    /**
     * Default constructor.
     */
    public MetricsHandler() {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, 0);
            try (OutputStream responseBody = exchange.getResponseBody()) {
                for (Metric metric : repo.listMetrics()) {
                    metric.write(responseBody);
                    responseBody.flush();
                }
                responseBody.close();
            }
        } finally {
            exchange.close();
        }
        LOG.atInfo().log("{} {} {} {} {}", 
                exchange.getRemoteAddress(),
                exchange.getProtocol(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getResponseCode()
                
        );
    }
    
}
