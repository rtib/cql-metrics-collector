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

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import io.github.rtib.cmc.model.MetricsIdentifier;
import java.util.Objects;

/**
 * Entity class for CQL query:
 * 
 * SELECT name FROM system_views.cql_metrics;
 * 
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Entity
@CqlName("cql_metrics")
@PropertyStrategy(mutable = false)
public class CqlMetricsName implements MetricsIdentifier {
    @PartitionKey private final String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String name() {
        return name;
    }

    public CqlMetricsName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CqlMetricsName other = (CqlMetricsName) obj;
        return Objects.equals(this.name, other.name);
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     * @deprecated 
     */
    @Deprecated
    public String getName() {
        return name;
    }
}
