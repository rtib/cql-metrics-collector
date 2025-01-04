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
package io.github.rtib.cmc.model.system_virtual_schema;

import com.datastax.oss.driver.api.mapper.annotations.ClusteringColumn;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

/**
 * Entity class for system_virtual_schema.tables virtual table.
 * 
 * VIRTUAL TABLE system_virtual_schema.tables (
 *  keyspace_name text,
 *  table_name text,
 *  comment text,
 *  PRIMARY KEY (keyspace_name, table_name)
 *  ) WITH CLUSTERING ORDER BY (table_name ASC)
 *  AND comment = 'virtual table definitions';
 * 
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Entity
@PropertyStrategy(mutable = false)
public final class Tables {

    public Tables(String keyspace_name, String table_name, String comment) {
        this.keyspace_name = keyspace_name;
        this.table_name = table_name;
        this.comment = comment;
    }
    
    @PartitionKey private final String keyspace_name;
    @ClusteringColumn private final String table_name;
    private final String comment;

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
    public String table_name() { return table_name;}

    /**
     * Get the value of comment
     *
     * @return the value of comment
     */
    public String comment() { return comment; }

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
    public String getTable_name() { return table_name;}

    /**
     * Get the value of comment
     *
     * @return the value of comment
     * @deprecated
     */
    @Deprecated
    public String getComment() { return comment; }
}
