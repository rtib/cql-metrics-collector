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
package io.github.rtib.cmc.queryTasks;

import io.github.rtib.cmc.Context;
import io.github.rtib.cmc.model.MetricsIdentifier;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class implementing common functions of collector classes.
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public abstract class AbstractCollector implements ICollector {
    protected final Context context = Context.getInstance();
    protected final Map<MetricsIdentifier,ScheduledFuture<?>> collectors = new ConcurrentHashMap<>();
    protected ScheduledFuture<?> updateTask;

    @Override
    public void deactivate() {
        if ((updateTask != null) || !updateTask.isCancelled())
            updateTask.cancel(true);
        clearCollectors();
    }

    @Override
    public boolean isAvailable() {
        return false;
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
}

