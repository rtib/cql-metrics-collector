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
package io.github.rtib.cmc.collectors;

import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.metrics.MetricType;
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_views.ThreadPoolName;
import io.github.rtib.cmc.model.system_views.ThreadPools;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of thread pools metrics.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class ThreadPoolsCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolsCollector.class);

    private final Config config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), Config.class);

    private Metric metricGauge;
    private Metric metricCounter;

    public ThreadPoolsCollector() {
        super("thread_pools");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.atWarn().log("ThreadPoolsCollector is not available.");
            return;
        }
        
        try {
            metricGauge = new Metric.Builder()
                    .withName("cassandra_thread_pools")
                    .withHelp("Statistics on the utilisation of Cassandra's thread pools.")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            metricCounter = new Metric.Builder()
                    .withName("cassandra_completed_tasks_counter")
                    .withHelp("Counter of completed tasks of Cassandra's thread pools.")
                    .withType(MetricType.COUNTER)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricGauge);
            Repository.getInstance().add(metricCounter);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initialize collector metrics.", ex);
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
        return context.systemViewsDao.listThreadPools().all();
    }

    private class Collector extends Thread {
        private final ThreadPoolName threadpool;
        private final String counterName = "completed_tasks";
        private final Set<String> gaugeNames = Set.of(
                "active_tasks",
                "active_tasks_limit",
                "blocked_tasks",
                "blocked_tasks_all_time",
                "pending_tasks"
        );
        private final Map<String,List<Label>> metricLabels = new HashMap<>();

        public Collector(MetricsIdentifier id) {
            threadpool = (ThreadPoolName) id;
            for (var gaugeName : gaugeNames) {
                var labels = LabelListBuilder.valueOf(threadpool, gaugeName);
                metricLabels.put(gaugeName, labels);
                metricGauge.addInstance(labels);
            }
            var labels = LabelListBuilder.valueOf(threadpool, counterName);
            metricLabels.put(counterName, labels);
            metricCounter.addInstance(labels);
        }

        @Override
        public void interrupt() {
            LOG.info("Interrupted collector of {}", threadpool);
            super.interrupt(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void run() {
            ThreadPools tpInstance = context.systemViewsDao.threadPool(threadpool.name());
            LOG.debug("Metrics acquired: {}", tpInstance);
            metricGauge.setValue(metricLabels.get("active_tasks"), tpInstance.active_tasks());
            metricGauge.setValue(metricLabels.get("active_tasks_limit"), tpInstance.active_tasks_limit());
            metricGauge.setValue(metricLabels.get("blocked_tasks"), tpInstance.blocked_tasks());
            metricGauge.setValue(metricLabels.get("blocked_tasks_all_time"), tpInstance.blocked_tasks_all_time());
            metricGauge.setValue(metricLabels.get("pending_tasks"), tpInstance.pending_tasks());
            metricCounter.setValue(metricLabels.get("completed_tasks"), tpInstance.completed_tasks());
        }

        @Override
        public void start() {
            LOG.info("Started collector of {}", threadpool);
            super.start(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
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
