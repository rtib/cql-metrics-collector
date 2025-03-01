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
import io.github.rtib.cmc.model.system_views.CqlMetrics;
import io.github.rtib.cmc.model.system_views.CqlMetricsName;

/**
 * Interface of data access to system_views.
 * 
 * @since Cassandra v4.1
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Dao
public interface DaoSystemViewsV41 {
    
    /**
     * List the names of batch statements.
     * @return list of batch metric names
     */
    @Query("SELECT name FROM system_views.batch_metrics")
    PagingIterable<BatchMetricsName> listBatchStatements();
    
    /**
     * Get metrics specific to batch a given statement.
     * @param name selector value
     * @return metrics entity
     */
    @Select
    BatchMetrics BatchMetrics(String name);

    /**
     * List the names of available CQL metrics.
     * @return list of cql metrics
     */
    @Query("SELECT name FROM system_views.cql_metrics")
    PagingIterable<CqlMetricsName> listCqlMetrics();
    
    /**
     * Get the CqlMetrics for a name.
     * @param name selector value
     * @return metrics entity
     */
    @Select
    CqlMetrics CqlMetrics(String name);
}
