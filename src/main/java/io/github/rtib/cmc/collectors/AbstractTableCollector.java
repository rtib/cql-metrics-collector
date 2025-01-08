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
package io.github.rtib.cmc.collectors;

import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_schema.TableName;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class collecting metrics on a per instance base.
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public abstract class AbstractTableCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractTableCollector.class);

    protected final TableCollectorConfig config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), TableCollectorConfig.class);

    /**
     * Initializing a collector for a given source instance.
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
        LOG.debug("Updating collector tasks of {}", this.getClass().getSimpleName());
        List<? extends MetricsIdentifier> tableList = getInstances();
        LOG.debug("Found tables: {}", tableList);
        
        retainAllCollectors(tableList);
        int numKept = collectors.size();
        int numNew = 0;
        for (MetricsIdentifier instance : tableList) {
            LOG.debug("Checking {}", instance);
            if (collectors.containsKey(instance)) {
                continue;
            }

            try {
                addCollector(instance,
                        createCollectorTask(instance),
                        config.getMetricsCollectionInterval()
                );
                numNew++;
            } catch (MetricException ex) {
                LOG.error("Couldn't create {} task for {}.", this.getClass().getSimpleName(), instance, ex);
                throw new RuntimeException(ex);
            }
        }
        LOG.info("{} tasks updated: {} kept, {} created, {} overall engaged.",
                this.getClass().getSimpleName(), numKept, numNew, collectors.size());
    }

    /**
     * Get the list of instances to be collected by this collector.
     * @return 
     */
    protected List<? extends MetricsIdentifier> getInstances() {
        return listTables(context.getConfigFor(this.getClass()).getBoolean("includeSystemTables"));
    }
    
    /**
     * This is to create a thread instance implementing the collector task for
     * a given instance.
     * @param id identifier which metrics are to be collected.
     * @return the collector thread.
     * @throws MetricException 
     */
    protected abstract Thread createCollectorTask(MetricsIdentifier id) throws MetricException;

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
