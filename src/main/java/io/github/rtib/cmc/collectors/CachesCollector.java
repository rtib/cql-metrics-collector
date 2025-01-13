/*
 * Copyright 2025 Tibor Répási <rtib@users.noreply.github.com>.
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
package io.github.rtib.cmc.collectors;

import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.metrics.MetricType;
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_views.CacheName;
import io.github.rtib.cmc.model.system_views.Caches;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of system cache metrics.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class CachesCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(CachesCollector.class);
    // private final Config config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), Config.class);
    
    private Metric metricGauge;
    private Metric metricCounter;

    public CachesCollector() {
        super("caches");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.warn("CachesCollector is not available.");
            return;
        }
        
        try {
            metricGauge = new Metric.Builder()
                    .withName("cassandra_system_caches")
                    .withHelp("Metrics on Cassandra's system cache utilisation.")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            metricCounter = new Metric.Builder()
                    .withName("cassandra_system_cache_counter")
                    .withHelp("Counter on Cassandra's system caches.")
                    .withType(MetricType.COUNTER)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricGauge);
            Repository.getInstance().add(metricCounter);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initalize collector metrics.", ex);
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metricGauge);
        Repository.getInstance().remove(metricCounter);
    }

    @Override
    public boolean isAvailable() {
        return context.systemVirtualSchemaDao.tables(KEYSPACE, TABLE) != null;
    }
    
    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id);
    }

    @Override
    protected List<? extends MetricsIdentifier> getInstances() {
        return context.systemViewsDao.listCaches().all();
    }
    
    private class Collector extends Thread {
        private final CacheName cacheName;
        private final Set<String> counterNames = Set.of(
                "entry_count",
                "hit_count",
                "request_count"
        );
        private final Set<String> gaugeNames = Set.of(
                "capacity_bytes",
                "hit_ratio",
                "recent_hit_rate_per_second",
                "recent_request_rate_per_second",
                "size_bytes"
        );
        private final Map<String,List<Label>> metricLabels = new HashMap<>();
        
        public Collector(MetricsIdentifier id) {
            cacheName = (CacheName) id;
            for (var gaugeName : gaugeNames) {
                var labels = LabelListBuilder.valueOf(cacheName, gaugeName);
                metricLabels.put(gaugeName, labels);
                metricGauge.addInstance(labels);
            }
            for (var counterName : counterNames) {
                var labels = LabelListBuilder.valueOf(cacheName, counterName);
                metricLabels.put(counterName, labels);
                metricCounter.addInstance(labels);
            }
        }

        @Override
        public void run() {
            Caches caches = context.systemViewsDao.caches(cacheName.name());
            LOG.debug("Metrics acquired: {}", caches);
            metricGauge.setValue(metricLabels.get("capacity_bytes"), caches.capacity_bytes());
            metricCounter.setValue(metricLabels.get("entry_count"), caches.entry_count());
            metricCounter.setValue(metricLabels.get("hit_count"), caches.hit_count());
            metricGauge.setValue(metricLabels.get("hit_ratio"), caches.hit_ratio());
            metricGauge.setValue(metricLabels.get("recent_hit_rate_per_second"), caches.recent_hit_rate_per_second());
            metricGauge.setValue(metricLabels.get("recent_request_rate_per_second"), caches.recent_request_rate_per_second());
            metricCounter.setValue(metricLabels.get("request_count"), caches.request_count());
            metricGauge.setValue(metricLabels.get("size_bytes"), caches.size_bytes());
        }
    }
    
    public static final class Config {
        private Duration updateInterval = Duration.ofMinutes(1);
        private Duration metricsCollectionInterval = Duration.ofSeconds(10);

        public void setUpdateInterval(Duration updateInterval) {
            this.updateInterval = updateInterval;
        }

        public void setMetricsCollectionInterval(Duration metricsCollectionInterval) {
            this.metricsCollectionInterval = metricsCollectionInterval;
        }

        public Duration getUpdateInterval() {
            return updateInterval;
        }

        public Duration getMetricsCollectionInterval() {
            return metricsCollectionInterval;
        }
    }
}
