/*
 * Copyright 2024 T. Repasi <rtib@users.noreply.github.com>.
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
 * Entity class for system_views.disk_usage virtual table.
 * 
 * VIRTUAL TABLE system_views.disk_usage (
 *   keyspace_name text,
 *   table_name text,
 *   mebibytes bigint,
 * PRIMARY KEY ((keyspace_name, table_name))
 * ) WITH comment = '';
 * 
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 *
 * @author T. Repasi <rtib@users.noreply.github.com>
 */
@Entity
@PropertyStrategy(mutable = false)
public final class DiskUsage {
    
    @PartitionKey(1) private final String keyspace_name;
    @PartitionKey(2) private final String table_name;
    private final long mebibytes;
    
    /**
     * Get keyspace_name.
     * @return String
     */
    public String keyspace_name() { return keyspace_name; }

    /**
     * Get table_name.
     * @return String
     */
    public String table_name() { return table_name; }
    
    /**
     * Get mebibytes.
     * @return long
     */
    public long mebibytes() { return mebibytes; }
    
    public DiskUsage(String keyspace_name, String table_name, long mebibytes) {
        this.keyspace_name = keyspace_name;
        this.table_name = table_name;
        this.mebibytes = mebibytes;
    }

    @Override
    public String toString() {
        return "DiskUsage{" + "keyspace_name=" + keyspace_name + ", table_name=" + table_name + ", mebibytes=" + mebibytes + '}';
    }

    /**
     * Get mebibytes.
     * @return long
     * @deprecated
     */
    @Deprecated
    public long getMebibytes() { return mebibytes; }
    
    /**
     * Get table_name.
     * @return String
     * @deprecated
     */
    @Deprecated
    public String getTable_name() { return table_name; }
    
    /**
     * Get keyspace_name.
     * @return String
     * @deprecated
     */
    @Deprecated
    public String getKeyspace_name() { return keyspace_name; }
}
