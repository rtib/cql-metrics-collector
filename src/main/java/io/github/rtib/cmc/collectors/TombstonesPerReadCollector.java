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
package io.github.rtib.cmc.collectors;

import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.metrics.MetricType;
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_schema.TableName;
import io.github.rtib.cmc.model.system_views.TombstonesPerRead;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of tombstones per read metrics for every table.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class TombstonesPerReadCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(TombstonesPerReadCollector.class);

    private Metric metricGauge;
    private Metric metricCount;

    public TombstonesPerReadCollector() {
        super("tombstones_per_read");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class not supported.");
            return;
        }
        try {
            metricGauge = new Metric.Builder()
                    .withName("cassandra_tombstones_per_read")
                    .withHelp("Tombstones per read metrics as acquired from system_views.tombstones_per_read table.")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricGauge);
            metricCount = new Metric.Builder()
                    .withName("cassandra_tombstones_per_read_count")
                    .withHelp("Tombstones per read metrics as acquired from system_views.tombstones_per_read table.")
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
    
    @Override
    public boolean isAvailable() {
        return context.systemVirtualSchemaDao.tables(KEYSPACE, TABLE) != null;
    }

    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id);
    }

    private class Collector extends Thread {
        private final TableName table;
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
            TombstonesPerRead tombstonesPerRead = context.systemViewsDao.TombstonesPerRead(table.keyspace_name(), table.table_name());
            LOG.debug("Metrics acquired: {}", tombstonesPerRead);
            metricCount.setValue(metricLabels.get("reads"), tombstonesPerRead.count());
            metricGauge.setValue(metricLabels.get("max"), tombstonesPerRead.max());
            metricGauge.setValue(metricLabels.get("p50th"), tombstonesPerRead.p50th());
            metricGauge.setValue(metricLabels.get("p99th"), tombstonesPerRead.p99th());
        }
    }
}
