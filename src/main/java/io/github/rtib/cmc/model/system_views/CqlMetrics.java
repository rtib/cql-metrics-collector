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
 * Entity class for system_views.cql_metrics virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.cql_metrics (
 *     name text PRIMARY KEY,
 *     value double
 * ) WITH comment = 'Metrics specific to CQL prepared statement caching';
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@PropertyStrategy(mutable = false)
public class CqlMetrics {
    @PartitionKey private final String name;
    private final double value;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String name() {
        return name;
    }

    /**
     * Get the value
     *
     * @return the value
     */
    public double value() {
        return value;
    }

    /**
     * Create entity instance.
     * @param name initial value
     * @param value initial value
     */
    public CqlMetrics(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "CqlMetrics{" + "name=" + name + ", value=" + value + '}';
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
     * Get the value
     *
     * @return the value
     * @deprecated Use {@link value()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getValue() {
        return value;
    }
}
