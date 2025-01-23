/*
 * Copyright 2024-2025 Tibor Répási <rtib@users.noreply.github.com>.
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

import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.Context;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.model.MetricsIdentifier;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class implementing common functions of collectors.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class AbstractCollector implements ICollector {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCollector.class);

    /**
     * Application context reference.
     */
    protected final Context context = Context.getInstance();
    
    /**
     * Generic collector configuration bean instance.
     */
    protected final CollectorConfig config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), CollectorConfig.class);
    
    /**
     * Map of metric identifiers (things collecting metrics about) mapping the collector tasks.
     */
    protected final Map<MetricsIdentifier,ScheduledFuture<?>> collectors = new ConcurrentHashMap<>();
    
    /**
     * Keyspace of the source table's keyspace.
     */
    protected final String KEYSPACE = "system_views";
    
    /**
     * Source table.
     */
    protected final String TABLE;

    /**
     * Scheduled task for updating collector task instances.
     */
    protected ScheduledFuture<?> updateTask;

    /**
     * Create the collector instance.
     * @param source_table source table name
     */
    public AbstractCollector(String source_table) {
        TABLE = source_table;
    }

    @Override
    public boolean isEnabled() {
        return context.getConfigFor(this.getClass()).getBoolean("enabled");
    }

    @Override
    public void activate() throws CollectorException {
        Duration updateInterval = config.getUpdateInterval();
        LOG.info("Starting {} update task with interval {}", this.getClass().getSimpleName(), updateInterval);
        updateTask = context.queryExecutor.scheduleAtFixedRate(
                new Thread(() -> update()),
                ThreadLocalRandom.current().nextLong(config.getUpdateInitialDelay().toSeconds()),
                updateInterval.getSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void deactivate() {
        LOG.info("Shutting down {}", this.getClass().getSimpleName());
        if ((updateTask != null) || !updateTask.isCancelled())
            updateTask.cancel(true);
        clearCollectors();
    }

    @Override
    public boolean isAvailable() {
        return context.systemVirtualSchemaDao.tables(KEYSPACE, TABLE) != null;
    }
    
    /**
     * Add a collector task. A collector task is a Thread instance collecting metrics.
     * Once added, the task will be scheduled for execution at a fixed rate on
     * the query executor pool of the context.
     * @param id the MetricsIdentifier to identify the collector
     * @param task the Thread instance implementing the collector
     * @param interval interval the task is scheduled at
     * @return true in case of success or false if the task is already registered
     */
    boolean addCollector(MetricsIdentifier id, Thread task, Duration interval) {
        if (collectors.containsKey(id))
            return false;
        
        collectors.put(
                id, 
                context.queryExecutor.scheduleAtFixedRate(
                        task,
                        ThreadLocalRandom.current().nextLong(interval.toSeconds()),
                        interval.toSeconds(),
                        TimeUnit.SECONDS)
        );
        LOG.info("Engaged {} task for: {}", this.getClass().getSimpleName(), id);
        return true;
    }
    
    /**
     * Remove a collector task. The task is canceled and removed from the scheduler.
     * @param id MetricsIdentifier to identify the task to be removed
     */
    void removeCollector(MetricsIdentifier id) {
        ScheduledFuture<?> task = collectors.get(id);
        if (task == null)
            return;
        
        LOG.info("Ceasing {} task for: {}", this.getClass().getSimpleName(), id);
        task.cancel(false);
        collectors.remove(id);
    }
    
    /**
     * Retain collectors with the given identifiers. All other collectors are
     * shut down and removed.
     * @param ids collection of identifiers to be retained
     */
    void retainAllCollectors(Collection<? extends MetricsIdentifier> ids) {
        for (var collector : collectors.keySet()) {
            if (ids.contains(collector))
                continue;
            
            removeCollector(collector);
        }
    }
    
    /**
     * Cancel and remove all collector tasks.
     */
    void clearCollectors() {
        collectors.values().forEach((ScheduledFuture<?> t) -> t.cancel(true));
        collectors.clear();
    }

    /**
     * This is to create a thread instance implementing the collector task for
     * a given instance.
     * @param id identifier which metrics are to be collected.
     * @return the collector thread.
     * @throws MetricException wrapping exceptions from creating labels
     */
    protected abstract Thread createCollectorTask(MetricsIdentifier id) throws MetricException;

    /**
     * Get the list of instances to be collected by this collector.
     * @return List of instance identifiers.
     */
    protected abstract List<? extends MetricsIdentifier> getInstances();

    /**
     * Updating the collector threads run by this class. It is enumerating all
     * tables and checking for collectors for each table. Creates collector
     * tasks for recently created tables and removes collector tasks of dropped
     * tables.
     */
    protected void update() {
        LOG.debug("Updating collector tasks of {}", this.getClass().getSimpleName());
        List<? extends MetricsIdentifier> instanceList = getInstances();
        LOG.debug("Found tables: {}", instanceList);
        retainAllCollectors(instanceList);
        int numKept = collectors.size();
        int numNew = 0;
        for (MetricsIdentifier instance : instanceList) {
            LOG.debug("Checking {}", instance);
            if (collectors.containsKey(instance)) {
                continue;
            }
            try {
                addCollector(instance, createCollectorTask(instance), config.getMetricsCollectionInterval());
                numNew++;
            } catch (MetricException ex) {
                LOG.error("Couldn't create {} task for {}.", this.getClass().getSimpleName(), instance, ex);
                throw new RuntimeException(ex);
            }
        }
        LOG.info("{} tasks updated: {} kept, {} created, {} overall engaged.", this.getClass().getSimpleName(), numKept, numNew, collectors.size());
    }
    
    /**
     * Configuration bean for all kinds of collectors.
     */
    protected static class CollectorConfig {
        private Duration metricsCollectionInterval;
        private Duration updateInterval;
        private Duration updateInitialDelay;

        /**
         * Get the initial delay of starting the update task.
         * @return initial delay
         */
        public Duration getUpdateInitialDelay() {
            return updateInitialDelay;
        }

        /**
         * Set the initial delay of starting the update task.
         * @param updateInitialDelay Duration of the initial delay
         */
        public void setUpdateInitialDelay(Duration updateInitialDelay) {
            this.updateInitialDelay = updateInitialDelay;
        }

        /**
         * Get the interval of collecting metric values.
         * @return Duration of the interval
         */
        public Duration getMetricsCollectionInterval() {
            return metricsCollectionInterval;
        }

        /**
         * Get the interval of updating metric instances.
         * @return Duration of the update interval
         */
        public Duration getUpdateInterval() {
            return updateInterval;
        }

        /**
         * Set the interval of collecting metric values.
         * @param metricsCollectionInterval new Duration of collection interval
         */
        public void setMetricsCollectionInterval(Duration metricsCollectionInterval) {
            this.metricsCollectionInterval = metricsCollectionInterval;
        }

        /**
         * Set the interval of updating metric instances.
         * @param updateInterval new Duration of update interval
         */
        public void setUpdateInterval(Duration updateInterval) {
            this.updateInterval = updateInterval;
        }

        /**
         * Default constructor.
         */
        public CollectorConfig() {
        }        
    }
}

