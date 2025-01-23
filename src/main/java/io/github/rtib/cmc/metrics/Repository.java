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
package io.github.rtib.cmc.metrics;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repository of active metrics. The repository acts as a List of active metrics
 * which is always returned in the same order, however, the order may change if
 * its elements are changed. Each Metric can only be listed once, while metrics
 * are distinguished by their name. A single Metric may carry multiple values.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public final class Repository {
    
    private static final Repository instance = new Repository();
    private static final Logger LOG = LoggerFactory.getLogger(Repository.class);

    private final List<Metric> metrics = new CopyOnWriteArrayList<>();
    private final Set<String> names = ConcurrentHashMap.newKeySet();
    
    private Repository() {
    }
    
    /**
     * Get the singleton instance of this class.
     * @return the Repository instance
     */
    public static final Repository getInstance() {
        return instance;
    }
    
    /**
     * Add a Metric to the repository.
     * @param metric Metric to be added
     */
    public void add(final Metric metric) {
        if (!names.add(metric.getName())) {
            var ex = new IllegalStateException("Metric " + metric.getName() + " already active.");
            LOG.atError().log(null, ex);
            throw ex;
        }
        
        this.metrics.add(metric);
        LOG.atInfo().log("Metric {} registered.", metric.getName());
    }
    
    /**
     * Remote a Metric from the repository.
     * @param metric the Metric to be removed.
     */
    public void remove(final Metric metric) {
        LOG.atInfo().log("Removing metric {}", metric.getName());
        metrics.remove(metric);
        names.remove(metric.getName());
    }
    
    /**
     * Remove all metrics from repository.
     */
    public void clear() {
        LOG.atInfo().log("Flushing.");
        metrics.clear();
        names.clear();
    }
    
    /**
     * Get a list of all metrics 
     * @return list of metrics
     */
    public List<Metric> listMetrics() {
        return List.copyOf(metrics);
    }
}
