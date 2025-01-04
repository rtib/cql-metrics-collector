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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.mapper.MapperBuilder;
import com.datastax.oss.driver.api.mapper.annotations.DaoFactory;
import com.datastax.oss.driver.api.mapper.annotations.Mapper;

/**
 * Interface of an object to map DAO to the system keyspace.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
@Mapper
public interface MapperSystem {
    
    /**
     * Get the DaoSystem object.
     * @return a DAO
     */
    @DaoFactory
    DaoSystem systemDao();
    
    /**
     * Build an instance of for MapperSystem.
     * @param session the CQL session on which the mapper should be created.
     * @return an object implementing this interface
     */
    static MapperBuilder<MapperSystem> builder(CqlSession session) {
        return new MapperSystemBuilder(session).withDefaultKeyspace("system");
    }
}
