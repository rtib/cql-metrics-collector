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
import io.github.rtib.cmc.model.system_views.TableSize;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract collector of size metrics about tables.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class AbstractTableSizeCollector extends AbstractTableCollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTableSizeCollector.class);
    private Metric metric;
    
    /**
     * Create collector instance.
     * @param source_table source table name promoted upstream
     */
    public AbstractTableSizeCollector(String source_table) {
        super(source_table);
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class not supported.");
            return;
        }
        try {
            metric = new Metric.Builder()
                    .withName("cassandra_" + TABLE)
                    .withHelp("Disk usage by tables acquired from " + TABLE)
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

    /**
     * Collector task to collect metrics of a single table.
     */
    protected abstract class Collector extends Thread {

        /**
         * Table this collector task is collecting metrics for.
         */
        protected final TableName table;
        private final List<Label> labels;

        Collector(MetricsIdentifier id) {
            super();
            this.table = (TableName) id;
            this.labels = LabelListBuilder.valueOf(this.table);
            metric.addInstance(labels);
        }

        @Override
        public void run() {
            TableSize size = getTableSize();
            LOG.debug("Metrics acquired: {}", size);
            metric.setValue(labels, size.mebibytes());
        }
        
        /**
         * Get the table size metrics.
         * @return entity instance
         */
        protected abstract TableSize getTableSize();
    }
    
}
