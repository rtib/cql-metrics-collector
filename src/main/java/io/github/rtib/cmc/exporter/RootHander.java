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
import io.github.rtib.cmc.Context;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler of root context.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class RootHander implements HttpHandler {
    
    private static final Context context = Context.getInstance();
    private static final Logger LOG = LoggerFactory.getLogger(RootHander.class);
    private final String contentType = "text/html; charset=utf-8";
    private final String responseBody = ""
            + "<html>"
            + "<head><title>test</title></head>"
            + "<body>"
            + "<h1>" + context.projectProperties.getProperty("application-name")
            + " v" + context.projectProperties.getProperty("application-version")
            + "</h1>"
            + "<li><a href=\"/metrics\">/metrics</a> - Prometheus metrics endpoint</li>"
            + "</body>"
            + "</html>";

    /**
     * Default constructor.
     */
    public RootHander() {
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        try {
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Content-Length", Integer.toString(responseBytes.length));
            exchange.sendResponseHeaders(200, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } finally {
            exchange.close();
        }
        LOG.atInfo().log("{} {} {} {} {} {}", 
                exchange.getRemoteAddress(),
                exchange.getProtocol(),
                exchange.getRequestMethod(),
                exchange.getRequestURI(),
                exchange.getResponseCode(),
                responseBytes.length
        );
    }
}
