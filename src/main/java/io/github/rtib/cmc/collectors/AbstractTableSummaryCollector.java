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
import io.github.rtib.cmc.model.system_views.TableSummary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract collector of table access summary metrics.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class AbstractTableSummaryCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTableSummaryCollector.class);

    private final String BASENAME = "cassandra_" + TABLE;
    private Metric metricCount;
    private Metric metricGauge;

    /**
     * Create collector instance.
     * @param source_table source table name promoted upstream
     */
    public AbstractTableSummaryCollector(String source_table) {
        super(source_table);
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.warn("Metrics collector class {} not supported.", this.getClass().getSimpleName());
            return;
        }
        try {
            metricGauge = new Metric.Builder()
                    .withName(BASENAME)
                    .withHelp("Summary gauge metrics as acquired from " + TABLE)
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricGauge);
            metricCount = new Metric.Builder()
                    .withName(BASENAME + "_count")
                    .withHelp("Summary counter metrics as acquired from " + TABLE)
                    .withType(MetricType.COUNTER)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricCount);
        } catch (MetricException ex) {
            throw new CollectorException(String.format("{}: failed to initialize collector metrics.", this.getClass().getSimpleName()), ex);
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metricGauge);
        Repository.getInstance().remove(metricCount);
    }
    
    /**
     * Collector tasks for collecting the metrics of a single table.
     */
    protected abstract class Collector extends Thread {
        /**
         * Table this collector task is collecting metrics for.
         */
        protected final TableName table;
        
        private final Map<String,List<Label>> metricLabels = new HashMap<>();
        private final String counterName = "reads";
        private final Set<String> gaugeNames = Set.of(
                "max",
                "p50th",
                "p99th"
        );

        Collector(MetricsIdentifier id) {
            this.table = (TableName) id;
            for (String gaugeName : gaugeNames) {
                List<Label> labels = LabelListBuilder.valueOf(table, gaugeName);
                metricLabels.put(gaugeName, labels);
                metricGauge.addInstance(labels);
            }
            List<Label> labels = LabelListBuilder.valueOf(table, counterName);
            metricLabels.put(counterName, labels);
            metricCount.addInstance(labels);
        }

        @Override
        public void run() {
            TableSummary summary = getSummary();
            LOG.debug("Metrics acquired: {}", summary);
            metricCount.setValue(metricLabels.get("reads"), summary.count());
            metricGauge.setValue(metricLabels.get("max"), summary.max());
            metricGauge.setValue(metricLabels.get("p50th"), summary.p50th());
            metricGauge.setValue(metricLabels.get("p99th"), summary.p99th());
        }

        /**
         * Get the summary entity for this task.
         * @return metrics entity object
         */
        protected abstract TableSummary getSummary();
    }
}
