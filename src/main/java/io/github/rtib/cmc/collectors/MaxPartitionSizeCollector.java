/*
 * Copyright 2025 T. Repasi <rtib@users.noreply.github.com>.
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
import io.github.rtib.cmc.model.system_views.MaxPartitionSize;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of max partition size for every table.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class MaxPartitionSizeCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(MaxPartitionSizeCollector.class);

    private Metric metric;

    public MaxPartitionSizeCollector() {
        super("max_partition_size");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class not supported.");
            return;
        }
        try {
            metric = new Metric.Builder()
                    .withName("cassandra_max_partition_size")
                    .withHelp("Max partition size by tables acquired from system_views.max_partition_size table.")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metric);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initialize collector metrics.", ex);
        }
        super.activate();
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metric);
    }

    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id);
    }

    private class Collector extends Thread {
        private final TableName table;
        private final List<Label> labels;

        public Collector(MetricsIdentifier id) {
            this.table = (TableName) id;
            this.labels = LabelListBuilder.valueOf(this.table);
            metric.addInstance(labels);
        }

        @Override
        public void run() {
            MaxPartitionSize maxPartitionSize = context.systemViewsDao.MaxPartitionSize(table.keyspace_name(), table.table_name());
            LOG.debug("Metrics acquired: {}", maxPartitionSize);
            metric.setValue(labels, maxPartitionSize.mebibytes());
        }
    }
}
