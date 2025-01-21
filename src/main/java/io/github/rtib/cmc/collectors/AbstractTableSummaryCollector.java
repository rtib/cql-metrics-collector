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
 *
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public abstract class AbstractTableSummaryCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTableSummaryCollector.class);

    protected final String BASENAME = "cassandra_" + TABLE;
    protected Metric metricCount;
    protected Metric metricGauge;

    public AbstractTableSummaryCollector(String source_table) {
        super(source_table);
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class not supported.");
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
            throw new CollectorException("Failed to initialize collector metrics.", ex);
        }
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metricGauge);
        Repository.getInstance().remove(metricCount);
    }
    
    protected abstract class Collector extends Thread {
        protected final TableName table;
        private final Map<String,List<Label>> metricLabels = new HashMap<>();
        private final String counterName = "reads";
        private final Set<String> gaugeNames = Set.of(
                "max",
                "p50th",
                "p99th"
        );

        public Collector(MetricsIdentifier id) {
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
         * @return 
         */
        protected abstract TableSummary getSummary();
    }
}
