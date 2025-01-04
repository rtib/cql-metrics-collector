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
package io.github.rtib.cmc.model.system_schema;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;
import io.github.rtib.cmc.model.MetricsIdentifier;
import java.util.Objects;

/**
 * Fully qualified table, consisting of keyspace and table names.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Entity
@PropertyStrategy(mutable = false)
public final class TableName implements MetricsIdentifier {
    
    private final String keyspace_name;
    private final String table_name;

    public TableName(String keyspace_name, String table_name) {
        this.keyspace_name = keyspace_name;
        this.table_name = table_name;
    }

    /**
     * Get the value of keyspace_name
     *
     * @return the value of keyspace_name
     */
    public String keyspace_name() { return keyspace_name; }


    /**
     * Get the value of table_name
     *
     * @return the value of table_name
     */
    public String table_name() { return table_name; }

    /**
     * Get the value of keyspace_name
     *
     * @return the value of keyspace_name
     * @deprecated
     */
    @Deprecated
    public String getKeyspace_name() { return keyspace_name; }


    /**
     * Get the value of table_name
     *
     * @return the value of table_name
     * @deprecated
     */
    @Deprecated
    public String getTable_name() { return table_name; }

    @Override
    public String toString() {
        return keyspace_name + "." + table_name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.keyspace_name);
        hash = 37 * hash + Objects.hashCode(this.table_name);
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
        final TableName other = (TableName) obj;
        if (!Objects.equals(this.keyspace_name, other.keyspace_name)) {
            return false;
        }
        return Objects.equals(this.table_name, other.table_name);
    }
}
