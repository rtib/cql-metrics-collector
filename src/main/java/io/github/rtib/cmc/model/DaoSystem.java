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
import com.datastax.oss.driver.api.mapper.annotations.Query;
import io.github.rtib.cmc.model.system.SystemInfo;

/**
 * Interface of a data access object allowing to access information within system.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Dao
public interface DaoSystem {

    /**
     * Access some selected fields of system.local.
     * @return an entity instance containing the values
     */
    @Query("select release_version, cluster_name, data_center, rack, listen_address, listen_port from system.local")
    SystemInfo getLocalInfo();
}
