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
package io.github.rtib.cmc.model;

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import io.github.rtib.cmc.model.system_views.DiskUsage;
import io.github.rtib.cmc.model.system_views.ThreadPoolName;
import io.github.rtib.cmc.model.system_views.ThreadPools;

/**
 * Interface of a data access object allowing to access information within system_views.
 * 
 * @author T. Repasi <rtib@users.noreply.github.com>
 */
@Dao
public interface DaoSystemViews {
    
    /**
     * Get an DiskUsage entity to a specific table.
     * @param keyspace_name keyspace containing the table
     * @param table_name table name
     * @return an Entity
     */
    @Select
    DiskUsage diskUsageFor(String keyspace_name, String table_name);
    
    /**
     * List the names of all thread pools.
     * @return iterable list of thread pools
     */
    @Query("SELECT name FROM system_views.thread_pools")
    PagingIterable<ThreadPoolName> listThreadPools();
    
    /**
     * Get the metrics of a thread pool.
     * @param name of the thread pool
     * @return metrics record of thread pool
     */
    @Select
    ThreadPools threadPool(String name);
}
