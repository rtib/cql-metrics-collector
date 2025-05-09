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
import io.github.rtib.cmc.model.system_schema.TableName;
import io.github.rtib.cmc.model.system_views.Latency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract collector of latencies for all tables. To be implemented for any
 * latency metric.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class AbstractLatencyCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLatencyCollector.class);
    private final String BASENAME = "cassandra_" + TABLE;
    private Metric metricBuckets;
    private Metric metricCount;
    private Metric metricMax;
    private Metric metricRate;
    
    /**
     * Create collector instance.
     * @param source_table source table name promoted upstream
     */
    public AbstractLatencyCollector(String source_table) {
        super(source_table);
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class {} not supported.", this.getClass().getSimpleName());
            return;
        }
        try {
            metricCount = new Metric.Builder()
                    .withName(BASENAME + "_count")
                    .withHelp("exporting the count field for " + TABLE)
                    .withType(MetricType.COUNTER)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricCount);
            metricMax = new Metric.Builder()
                    .withName(BASENAME + "_max")
                    .withHelp("exporting max latency in milliseconds for " + TABLE)
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricMax);
            metricBuckets = new Metric.Builder()
                    .withName(BASENAME + "_bucket")
                    .withHelp("exporting percentile buckets in milliseconds for " + TABLE)
                    .withType(MetricType.SUMMARY)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricBuckets);
            metricRate = new Metric.Builder()
                    .withName(BASENAME + "_rate")
                    .withHelp("exporting request rate per second for " + TABLE)
                    .withType(MetricType.SUMMARY)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricRate);
        } catch (MetricException ex) {
            throw new CollectorException(String.format("{}: failed to initialize collector metrics.", this.getClass().getSimpleName()), ex);
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metricCount);
        Repository.getInstance().remove(metricMax);
        Repository.getInstance().remove(metricBuckets);
        Repository.getInstance().remove(metricRate);
    }

    /**
     * A generic latency collector task.
     */
    protected abstract class Collector extends Thread {
        /**
         * Table this collector task is collecting metrics for.
         */
        protected final TableName table;
        private final Map<String,List<Label>> metricLabels;
        
        Collector(MetricsIdentifier id) throws MetricException {
            table = (TableName) id;
            List<Label> tabLabel = LabelListBuilder.valueOf(table);
            Map<String,List<Label>> labelmap = new HashMap<>();
            labelmap.put("count", tabLabel);
            labelmap.put("max_ms", tabLabel);
            labelmap.put("p50th_ms", new LabelListBuilder()
                    .addLabels(tabLabel)
                    .addLabel("quantile", "0.5")
                    .build()
            );
            labelmap.put("p99th_ms", new LabelListBuilder()
                    .addLabels(tabLabel)
                    .addLabel("quantile", "0.99")
                    .build()
            );
            labelmap.put("per_second", tabLabel);
            metricLabels = Map.copyOf(labelmap);
            
            metricCount.addInstance(metricLabels.get("count"));
            metricMax.addInstance(metricLabels.get("max_ms"));
            metricBuckets.addInstance(metricLabels.get("p50th_ms"));
            metricBuckets.addInstance(metricLabels.get("p99th_ms"));
            metricRate.addInstance(metricLabels.get("per_second"));
        }

        @Override
        public void run() {
            Latency latency = getLatency();
            LOG.debug("Metrics acquired: {}", latency);
            metricCount.setValue(metricLabels.get("count"), latency.count());
            metricMax.setValue(metricLabels.get("max_ms"), latency.max_ms());
            metricBuckets.setValue(metricLabels.get("p50th_ms"), latency.p50th_ms());
            metricBuckets.setValue(metricLabels.get("p99th_ms"), latency.p99th_ms());
            metricRate.setValue(metricLabels.get("per_second"), latency.per_second());
        }

        /**
         * Get the latency entity for this task.
         * @return metrics entity object
         */
        protected abstract Latency getLatency();
    }

}
