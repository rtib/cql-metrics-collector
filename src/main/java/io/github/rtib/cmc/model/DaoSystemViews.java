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

import com.datastax.oss.driver.api.core.PagingIterable;
import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Query;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import io.github.rtib.cmc.model.system_views.BatchMetrics;
import io.github.rtib.cmc.model.system_views.BatchMetricsName;
import io.github.rtib.cmc.model.system_views.CacheName;
import io.github.rtib.cmc.model.system_views.Caches;
import io.github.rtib.cmc.model.system_views.CoordinatorReadLatency;
import io.github.rtib.cmc.model.system_views.CoordinatorScanLatency;
import io.github.rtib.cmc.model.system_views.CoordinatorWriteLatency;
import io.github.rtib.cmc.model.system_views.CqlMetrics;
import io.github.rtib.cmc.model.system_views.CqlMetricsName;
import io.github.rtib.cmc.model.system_views.DiskUsage;
import io.github.rtib.cmc.model.system_views.LocalReadLatency;
import io.github.rtib.cmc.model.system_views.LocalScanLatency;
import io.github.rtib.cmc.model.system_views.LocalWriteLatency;
import io.github.rtib.cmc.model.system_views.MaxPartitionSize;
import io.github.rtib.cmc.model.system_views.RowsPerRead;
import io.github.rtib.cmc.model.system_views.ThreadPoolName;
import io.github.rtib.cmc.model.system_views.ThreadPools;
import io.github.rtib.cmc.model.system_views.TombstonesPerRead;

/**
 * Interface of a data access object allowing to access information within system_views.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Dao
public interface DaoSystemViews {
    // ToDo: refactor method names to common schema
    
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
    
    /**
     * List cache names.
     * @return 
     */
    @Query("SELECT name FROM system_views.caches")
    PagingIterable<CacheName> listCaches();
    
    /**
     * Get metrics of the specified system cache.
     * @param name system cache name
     * @return 
     */
    @Select
    Caches caches(String name);
    
    /**
     * Get coordinator read latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    CoordinatorReadLatency CoordinatorReadLatency(String keyspace_name, String table_name);
    
    /**
     * Get coordinator write latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    CoordinatorWriteLatency CoordinatorWriteLatency(String keyspace_name, String table_name);
    
    /**
     * Get coordinator scan latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    CoordinatorScanLatency CoordinatorScanLatency(String keyspace_name, String table_name);
    
    /**
     * Get local read latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    LocalReadLatency LocalReadLatency(String keyspace_name, String table_name);
    
    /**
     * Get local write latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    LocalWriteLatency LocalWriteLatency(String keyspace_name, String table_name);
    
    /**
     * Get local scan latency metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    LocalScanLatency LocalScanLatency(String keyspace_name, String table_name);
    
    /**
     * Get tombstones per read metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    TombstonesPerRead TombstonesPerRead(String keyspace_name, String table_name);
    
    /**
     * Get rows per read metrics for a given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    RowsPerRead RowsPerRead(String keyspace_name, String table_name);
    
    /**
     * List the names of batch statements.
     * @return 
     */
    @Query("SELECT name FROM system_views.batch_metrics")
    PagingIterable<BatchMetricsName> listBatchStatements();
    
    /**
     * Get metrics specific to batch a given statement.
     * @param name
     * @return 
     */
    @Select
    BatchMetrics BatchMetrics(String name);
    
    /**
     * Get the max partition size of the given table.
     * @param keyspace_name
     * @param table_name
     * @return 
     */
    @Select
    MaxPartitionSize MaxPartitionSize(String keyspace_name, String table_name);
    
    /**
     * List the names of available CQL metrics.
     * @return 
     */
    @Query("SELECT name FROM system_views.cql_metrics")
    PagingIterable<CqlMetricsName> listCqlMetrics();
    
    /**
     * Get the CqlMetrics for a name.
     * @param name
     * @return 
     */
    @Select
    CqlMetrics CqlMetrics(String name);
}
