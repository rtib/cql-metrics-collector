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
import io.github.rtib.cmc.model.system_schema.TableName;

/**
 * Interface of a data access object allowing to access information within system_schema.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Dao
public interface DaoSystemSchema {

    /**
     * List all tables with fully qualified names.
     * @return iterable list of fully qualified table names
     */
    @Query("SELECT keyspace_name, table_name FROM system_schema.tables")
    PagingIterable<TableName> listAllTables();
}
