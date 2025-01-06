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
package io.github.rtib.cmc.queryTasks;

import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.system_schema.TableName;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class collecting metrics on a per table base.
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public abstract class AbstractTableCollector extends AbstractCollector {

    protected final TableCollectorConfig config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), TableCollectorConfig.class);

    /**
     * Initializing a collector for a given source table.
     * @param source_table 
     */
    public AbstractTableCollector(String source_table) {
        super(source_table);
    }

    /**
     * List tables for which metrics might be collected.
     * @param includeSystemTables whether system tables should be included
     * @return 
     */
    protected List<TableName> listTables(boolean includeSystemTables) {
        if (includeSystemTables) {
            return context.systemSchemaDao.listAllTables().all();
        } else {
            List<TableName> list = new ArrayList<>();
            for (KeyspaceMetadata keyspace : context.cqlSession.getMetadata().getKeyspaces().values()) {
                for (TableMetadata table : keyspace.getTables().values()) {
                    list.add(new TableName(keyspace.getName().asCql(true), table.getName().asCql(true)));
                }
            }
            return List.copyOf(list);
        }
    }

    /**
     * Updating the collector threads run by this class. It is enumerating all
     * tables and checking for collectors for each table. Creates collector
     * tasks for recently created tables and removes collector tasks of dropped
     * tables.
     */
    protected void update() {
        LOG.info("Updating.");
        List<TableName> tableList = listTables(context.getConfigFor(this.getClass()).getBoolean("includeSystemTables"));
        LOG.debug("Found tables: {}", tableList);
        retainAllCollectors(tableList);
        for (TableName table : tableList) {
            LOG.debug("Checking {}", table);
            if (collectors.containsKey(table)) {
                continue;
            }

        LOG.debug("Creating collector for {}", table);
        try {
            addCollector(
                    table,
                    getCollector(table),
                    config.getMetricsCollectionInterval()
            );
        } catch (MetricException ex) {
            LOG.error("Couldn't create Collector for {}.", table, ex);
            throw new RuntimeException(ex);
        }
        }
        LOG.info("Engaged {} collector: {}", collectors.size(), collectors.keySet());
    }

    /**
     * This is to create a thread instance implementing the collector task for
     * a given table.
     * @param table Table which metrics are to be collected.
     * @return the collector thread.
     * @throws MetricException 
     */
    protected abstract Thread getCollector(TableName table) throws MetricException;

    /**
     * Configuration bean.
     */
    protected static class TableCollectorConfig extends CollectorConfig {
        private boolean includeSystemTables = false;

        public TableCollectorConfig() {
            super();
        }

        public boolean isIncludeSystemTables() {
            return includeSystemTables;
        }

        public void setIncludeSystemTables(boolean includeSystemTables) {
            this.includeSystemTables = includeSystemTables;
        }
    }
}
