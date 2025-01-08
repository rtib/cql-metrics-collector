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

import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.system_schema.TableName;
import io.github.rtib.cmc.model.system_views.Latency;

/**
 * Collect local_read_latency metrics for every table.
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public final class LocalWriteLatencyCollector extends AbstractLatencyCollector {

    public LocalWriteLatencyCollector() {
        super("local_write_latency");
    }

    @Override
    protected Thread createCollectorTask(TableName table) throws MetricException {
        return new Collector(table) {
            @Override
            protected Latency getLatency() {
                return context.systemViewsDao.LocalWriteLatency(table.keyspace_name(), table.table_name());
            }
        };
    }
    
}
