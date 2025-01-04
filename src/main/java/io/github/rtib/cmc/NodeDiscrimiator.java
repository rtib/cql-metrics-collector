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

import com.datastax.oss.driver.api.core.loadbalancing.NodeDistance;
import com.datastax.oss.driver.api.core.loadbalancing.NodeDistanceEvaluator;
import com.datastax.oss.driver.api.core.metadata.Node;
import java.net.InetSocketAddress;

/**
 * Simple node discriminator excluding all nodes except the monitored one.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class NodeDiscrimiator implements NodeDistanceEvaluator {

    private final InetSocketAddress preferedNode;
    
    public NodeDiscrimiator(InetSocketAddress preferedNode) {
        this.preferedNode = preferedNode;
    }

    @Override
    public NodeDistance evaluateDistance(Node node, String string) {
        if (preferedNode.equals(node.getEndPoint().resolve()))
            return NodeDistance.LOCAL;
        else
            return NodeDistance.IGNORED;
    }
    
}
