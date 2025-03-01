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

import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.MapperSystemViews;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_views.TableSummary;
import io.github.rtib.cmc.model.DaoSystemViewsV40;

/**
 * Collector of tombstones per read metrics for every table.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class TombstonesPerReadCollector extends AbstractTableSummaryCollector {

    private DaoSystemViewsV40 dao;

    /**
     * Create collector instance.
     */
    public TombstonesPerReadCollector() {
        super("tombstones_per_read");
    }
    
    @Override
    protected void setup() {
        dao = MapperSystemViews.builder(context.cqlSession).build().systemViewsDaoV40();
    }

    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id) {
            @Override
            protected TableSummary getSummary() {
                return dao.TombstonesPerRead(table.keyspace_name(), table.table_name());
            }
        };
    }

}
