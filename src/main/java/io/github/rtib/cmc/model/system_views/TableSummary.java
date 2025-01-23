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

import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;

/**
 * Abstract entity class for table based summaries, e.g.
 * <pre>
 * VIRTUAL TABLE system_views.tombstones_per_read (
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
public abstract class TableSummary {
    
    @PartitionKey(1) private final String keyspace_name;
    @PartitionKey(2) private final String table_name;
    private final long count;
    private final double max;
    private final double p50th;
    private final double p99th;

    /**
     * Create entity instance
     * @param keyspace_name initial value
     * @param table_name initial value
     * @param count initial value
     * @param max initial value
     * @param p50th initial value
     * @param p99th initial value
     */
    public TableSummary(String keyspace_name, String table_name, long count, double max, double p50th, double p99th) {
        this.keyspace_name = keyspace_name;
        this.table_name = table_name;
        this.count = count;
        this.max = max;
        this.p50th = p50th;
        this.p99th = p99th;
    }

    /**
     * Get the value of keyspace_name
     *
     * @return the value of keyspace_name
     */
    public String keyspace_name() {
        return keyspace_name;
    }

    /**
     * Get the value of table_name
     *
     * @return the value of table_name
     */
    public String table_name() {
        return table_name;
    }

    /**
     * Get the value of count
     *
     * @return the value of count
     */
    public long count() {
        return count;
    }

    /**
     * Get the value of max
     *
     * @return the value of max
     */
    public double max() {
        return max;
    }

    /**
     * Get the value of p50th
     *
     * @return the value of p50th
     */
    public double p50th() {
        return p50th;
    }

    /**
     * Get the value of p99th
     *
     * @return the value of p99th
     */
    public double p99th() {
        return p99th;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{keyspace_name=" + keyspace_name + ", table_name=" + table_name + ", count=" + count + ", max=" + max + ", p50th=" + p50th + ", p99th=" + p99th + '}';
    }

    /**
     * Get the value of keyspace_name
     *
     * @return the value of keyspace_name
     * @deprecated Use {@link keyspace_name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getKeyspace_name() {
        return keyspace_name;
    }

    /**
     * Get the value of table_name
     *
     * @return the value of table_name
     * @deprecated Use {@link table_name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getTable_name() {
        return table_name;
    }

    /**
     * Get the value of count
     *
     * @return the value of count
     * @deprecated Use {@link count()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getCount() {
        return count;
    }

    /**
     * Get the value of max
     *
     * @return the value of max
     * @deprecated Use {@link max()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getMax() {
        return max;
    }

    /**
     * Get the value of p50th
     *
     * @return the value of p50th
     * @deprecated Use {@link p50th()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP50th() {
        return p50th;
    }

    /**
     * Get the value of p99th
     *
     * @return the value of p99th
     * @deprecated Use {@link p99th()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP99th() {
        return p99th;
    }
    
}
