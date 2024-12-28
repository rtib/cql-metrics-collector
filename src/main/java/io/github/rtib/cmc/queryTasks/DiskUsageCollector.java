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
package io.github.rtib.cmc.queryTasks;

import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.metrics.MetricType;
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.system_schema.TableName;
import io.github.rtib.cmc.model.system_views.DiskUsage;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of disk usage of every table.
 * 
 * @author T. Repasi <rtib@users.noreply.github.com>
 */
public class DiskUsageCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(DiskUsageCollector.class);
    
    private final String KEYSPACE = "system_views";
    private final String TABLE = "disk_usage";
    
    private final Config config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), Config.class);
    private Metric metric;

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.info("Metrics class not supported.");
            return;
        }
        try {
            metric = new Metric.Builder()
                    .withName("cassandra_disk_usage")
                    .withHelp("Disk usage by tables acquired from system_views.disk_usage table.")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metric);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initialize collector metrics.", ex);
        }
        
        Duration updateInterval = config.getUpdateInterval();
        LOG.info("Starting table collector update task: {}", updateInterval);
        updateTask = context.queryExecutor.scheduleAtFixedRate(
                new Thread(() -> update()),
                0,
                updateInterval.getSeconds(), TimeUnit.SECONDS);
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metric);
    }
    
    @Override
    public boolean isAvailable() {
        return context.systemVirtualSchemaDao.tables(KEYSPACE, TABLE) != null;
    }

    private List<TableName> listTables() {
        if (config.isIncludeSystemTables())
            return context.systemSchemaDao.listAllTables().all();
        else {
            List<TableName> list = new ArrayList<>();
            for (KeyspaceMetadata keyspace : context.cqlSession.getMetadata().getKeyspaces().values()) {
                for (TableMetadata table : keyspace.getTables().values()) {
                    list.add(new TableName(keyspace.getName().asCql(true), table.getName().asCql(true)));
                }
            }
            return List.copyOf(list);
        }
    }
    
    private void update() {
        LOG.info("Updating.");
        List<TableName> tableList = listTables();
        LOG.debug("Found tables: {}", tableList);
        retainAllCollectors(tableList);
        for (var table : tableList) {
            LOG.debug("Checking {}", table);
            if (collectors.containsKey(table))
                continue;
            
            LOG.debug("Creating Collector for {}", table);
            addCollector(
                    table,
                    new Collector(table),
                    config.getMetricsCollectionInterval()
            );
        }
        LOG.info("Engaged {} collector: {}", collectors.size(), collectors.keySet());
    }

    private class Collector extends Thread {
        private final Logger LOG = LoggerFactory.getLogger(Collector.class);

        private final TableName table;
        private final List<Label> labels;

        public Collector(TableName table) {
            this.table = table;
            this.labels = LabelListBuilder.valueOf(table);
            metric.addInstance(labels);
        }

        @Override
        public void interrupt() {
            LOG.info("Interrupted collector of {}", table);
            super.interrupt(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }

        @Override
        public void run() {
            DiskUsage diskUsageFor = context.systemViewsDao.diskUsageFor(table.keyspace_name(), table.table_name());
            LOG.debug("Metrics acquired: {}", diskUsageFor);
            metric.setValue(labels, diskUsageFor.mebibytes());
        }

        @Override
        public void start() {
            LOG.info("Started collector of {}", table);
            super.start(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        }
    }
    
    public static final class Config {
        private boolean includeSystemTables = false;
        private Duration updateInterval = Duration.ofMinutes(1);
        private Duration metricsCollectionInterval = Duration.ofSeconds(10);

        public Duration getUpdateInterval() {
            return updateInterval;
        }

        public Duration getMetricsCollectionInterval() {
            return metricsCollectionInterval;
        }

        public boolean isIncludeSystemTables() {
            return includeSystemTables;
        }

        public void setIncludeSystemTables(boolean includeSystemTables) {
            this.includeSystemTables = includeSystemTables;
        }

        public void setUpdateInterval(Duration updateInterval) {
            this.updateInterval = updateInterval;
        }

        public void setMetricsCollectionInterval(Duration metricsCollectionInterval) {
            this.metricsCollectionInterval = metricsCollectionInterval;
        }

    }
}
