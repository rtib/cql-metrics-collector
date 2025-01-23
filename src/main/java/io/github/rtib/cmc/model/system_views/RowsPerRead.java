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
package io.github.rtib.cmc.model.system_views;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

/**
 * Entity class for system_views.rows_per_read virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.rows_per_read (
 *     keyspace_name text,
 *     table_name text,
 *     count bigint,
 *     max double,
 *     p50th double,
 *     p99th double,
 *     PRIMARY KEY ((keyspace_name, table_name))
 * ) WITH comment = '';
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 *
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@PropertyStrategy(mutable = false)
public final class RowsPerRead extends TableSummary {

    /**
     * Create entity instance.
     * @param keyspace_name initial value
     * @param table_name initial value
     * @param count initial value
     * @param max initial value
     * @param p50th initial value
     * @param p99th initial value
     */
    public RowsPerRead(String keyspace_name, String table_name, long count, double max, double p50th, double p99th) {
        super(keyspace_name, table_name, count, max, p50th, p99th);
    }
    
}
