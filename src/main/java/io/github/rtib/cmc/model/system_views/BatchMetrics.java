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
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

/**
 * Entity class for system_views.batch_metrics virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.batch_metrics (
 *     name text PRIMARY KEY,
 *     max bigint,
 *     p50th double,
 *     p999th double,
 *     p99th double
 * ) WITH comment = 'Metrics specific to batch statements';
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@PropertyStrategy(mutable = false)
public final class BatchMetrics {

    @PartitionKey private final String name;
    private final long max;
    private final double p50th;
    private final double p999th;
    private final double p99th;

    /**
     * Create entity instance.
     * @param name initial value
     * @param max initial value
     * @param p50th initial value
     * @param p999th initial value
     * @param p99th initial value
     */
    public BatchMetrics(String name, long max, double p50th, double p999th, double p99th) {
        this.name = name;
        this.max = max;
        this.p50th = p50th;
        this.p999th = p999th;
        this.p99th = p99th;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String name() {
        return name;
    }

    /**
     * Get the value of max
     *
     * @return the value of max
     */
    public long max() {
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

    /**
     * Get the value of p999th
     *
     * @return the value of p999th
     */
    public double p999th() {
        return p999th;
    }

    @Override
    public String toString() {
        return "BatchMetrics{" + "name=" + name + ", max=" + max + ", p50th=" + p50th + ", p999th=" + p999th + ", p99th=" + p99th + '}';
    }
    
    /**
     * Get the value of name
     *
     * @return the value of name
     * @deprecated Use {@link name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     * Get the value of max
     *
     * @return the value of max
     * @deprecated Use {@link max()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getMax() {
        return max;
    }

    /**
     * Get the value of p50th
     *
     * @return the value of p50th
     * @deprecated Use {@link p50th} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP50th() {
        return p50th;
    }

    /**
     * Get the value of p99th
     *
     * @return the value of p99th
     * @deprecated Use {@link p99th} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP99th() {
        return p99th;
    }

    /**
     * Get the value of p999th
     *
     * @return the value of p999th
     * @deprecated Use {@link p999th} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getP999th() {
        return p999th;
    }
    
}
