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
 * Entity class for system_views.disk_usage virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.max_partition_size (
 *     keyspace_name text,
 *     table_name text,
 *     mebibytes bigint,
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
public final class MaxPartitionSize extends TableSize {

    /**
     * Create entity instance.
     * @param keyspace_name initial value
     * @param table_name initial value
     * @param mebibytes initial value
     */
    public MaxPartitionSize(String keyspace_name, String table_name, long mebibytes) {
        super(keyspace_name, table_name, mebibytes);
    }
}
