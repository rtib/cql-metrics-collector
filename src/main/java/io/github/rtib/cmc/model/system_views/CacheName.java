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
 * Entity class to holt query result of
 * <pre>
 *   SELECT name FROM system_views.caches
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@CqlName("caches")
@PropertyStrategy(mutable = false)
public final class CacheName implements MetricsIdentifier {

    @PartitionKey private final String name;

    /**
     * Get cache name.
     * @return cache name as string
     */
    public String name() {
        return name;
    }
    
    /**
     * Create entity instance.
     * @param name initial value
     */
    CacheName(String name) {
        this.name = name;
    } 

    /**
     * Get cache name.
     * @return String
     * @deprecated Use {@link name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + Objects.hashCode(this.name);
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
        final CacheName other = (CacheName) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
