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
 * Abstract entity class for table bound latency metrics.
 * 
 * <pre>
 * VIRTUAL TABLE system_views.[coordinator|local]_[read|scan|write]_latency (
 *     keyspace_name text,
 *     table_name text,
 *     count bigint,
 *     max_ms double,
 *     p50th_ms double,
 *     p99th_ms double,
 *     per_second double,
 *     PRIMARY KEY ((keyspace_name, table_name))
 * ) WITH comment = '';
 * </pre>
 * 
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class Latency {

    @PartitionKey(1) private final String keyspace_name;
    @PartitionKey(2) private final String table_name;
    private final long count;
    private final double max_ms;
    private final double p50th_ms;
    private final double p99th_ms;
    private final double per_second;

    /**
     * Get keyspace name.
     * @return keyspace_name
     */
    public String keyspace_name() {
        return keyspace_name;
    }

    /**
     * Get table name.
     * @return table_name
     */
    public String table_name() {
        return table_name;
    }

    /**
     * Get request count.
     * @return request count
     */
    public long count() {
        return count;
    }

    /**
     * Get max request duration in milliseconds.
     * @return max latency in milliseconds
     */
    public double max_ms() {
        return max_ms;
    }

    /**
     * Get p50th percentile of request durations in milliseconds.
     * @return p50th latency in milliseconds
     */
    public double p50th_ms() {
        return p50th_ms;
    }

    /**
     * Get p99th percentile of request durations in milliseconds.
     * @return p99th latency in milliseconds
     */
    public double p99th_ms() {
        return p99th_ms;
    }

    /**
     * Get request rate.
     * @return request rate per second
     */
    public double per_second() {
        return per_second;
    }
    
    /**
     * Create the entity with initial values.
     * @param keyspace_name initial keyspace_name
     * @param table_name initial table_name
     * @param count initial request count
     * @param max_ms initial max latency ms
     * @param p50th_ms initial p50th latency ms
     * @param p99th_ms initial p99th latency ms
     * @param per_second initial request rate per second
     */
    Latency(
            String keyspace_name,
            String table_name,
            long count,
            double max_ms,
            double p50th_ms,
            double p99th_ms,
            double per_second
    ) {
        this.keyspace_name = keyspace_name;
        this.table_name = table_name;
        this.count = count;
        this.max_ms = max_ms;
        this.p50th_ms = p50th_ms;
        this.p99th_ms = p99th_ms;
        this.per_second = per_second;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "keyspace_name=" + keyspace_name + ", table_name=" + table_name + ", count=" + count + ", max_ms=" + max_ms + ", p50th_ms=" + p50th_ms + ", p99th_ms=" + p99th_ms + ", per_second=" + per_second + '}';
    }

    /**
     * Get keyspace name.
     * @return keyspace_name
     * @deprecated Use {@code keyspace_name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getKeyspace_name() {
        return keyspace_name;
    }

    /**
     * Get table name.
     * @return table_name
     * @deprecated Use {@code table_name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getTable_name() {
        return table_name;
    }

    /**
     * Get request count.
     * @return request count
     * @deprecated Use {@code count()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getCount() {
        return count;
    }

    /**
     * Get max request duration in milliseconds.
     * @return max latency in milliseconds
     * @deprecated Use {@code max_ms()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getMax_ms() {
        return max_ms;
    }

    /**
     * Get p50th percentile of request durations in milliseconds.
     * @return p50th latency in milliseconds.
     * @deprecated Use {@code p50th_ms()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP50th_ms() {
        return p50th_ms;
    }

    /**
     * Get p99th percentile of request durations in milliseconds.
     * @return p99th latency in milliseconds
     * @deprecated Use {@code p99ths()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP99th_ms() {
        return p99th_ms;
    }

    /**
     * Get request rate.
     * @return request rate per seconds
     * @deprecated Use {@code rate()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getPer_second() {
        return per_second;
    }
}
