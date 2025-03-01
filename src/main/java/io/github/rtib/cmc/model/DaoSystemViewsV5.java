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
package io.github.rtib.cmc.model;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import io.github.rtib.cmc.model.system_views.MaxSstableSize;

/**
 * Interface of data access to system_views.
 * 
 * @since Cassandra v5.0
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Dao
public interface DaoSystemViewsV5 {    
    /**
     * Get max sstable size of a table.
     * @param keyspace_name selector value
     * @param table_name selector value
     * @return metrics entity
     */
    @Select
    MaxSstableSize MaxSstableSize(String keyspace_name, String table_name);
}
