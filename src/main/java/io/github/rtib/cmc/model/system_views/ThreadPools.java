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
package io.github.rtib.cmc.model.system_views;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

/**
 * Entity class for system_views.thread_pools virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.thread_pools (
 *   name text PRIMARY KEY,
 *   active_tasks int,
 *   active_tasks_limit int,
 *   blocked_tasks bigint,
 *   blocked_tasks_all_time bigint,
 *   completed_tasks bigint,
 *   pending_tasks int
 * ) WITH comment = '';
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 *
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@PropertyStrategy(mutable = false)
public final class ThreadPools {
    
    @PartitionKey private final String name;
    private final int active_tasks;
    private final int active_tasks_limit;
    private final long blocked_tasks;
    private final long blocked_tasks_all_time;
    private final long completed_tasks;
    private final int pending_tasks;
    
    /**
     * Get thread pool name.
     * @return name of the thread pool as string
     */
    public String name() {
        return name;
    }

    /**
     * Get active_tasks.
     * @return number of currently active tasks
     */
    public int active_tasks() {
        return active_tasks;
    }

    /**
     * Get active_tasks_limit.
     * @return limit of active tasks
     */
    public int active_tasks_limit() {
        return active_tasks_limit;
    }

    /**
     * Get blocked_tasks.
     * @return number of currently blocked tasks
     */
    public long blocked_tasks() {
        return blocked_tasks;
    }

    /**
     * Get blocked_tasks_all_time.
     * @return high water mark of blocked tasks
     */
    public long blocked_tasks_all_time() {
        return blocked_tasks_all_time;
    }

    /**
     * Get completed_tasks.
     * @return count of tasks completed
     */
    public long completed_tasks() {
        return completed_tasks;
    }

    /**
     * Get pending_tasks.
     * @return number of tasks pending
     */
    public int pending_tasks() {
        return pending_tasks;
    }
    
    /**
     * Create entity instance.
     * @param name initial value
     * @param active_tasks initial value
     * @param active_tasks_limit initial value
     * @param blocked_tasks initial value
     * @param blocked_tasks_all_time initial value
     * @param completed_tasks initial value
     * @param pending_tasks initial value
     */
    ThreadPools(
            String name,
            int active_tasks,
            int active_tasks_limit,
            long blocked_tasks,
            long blocked_tasks_all_time,
            long completed_tasks,
            int pending_tasks
    ) {
        this.name = name;
        this.active_tasks = active_tasks;
        this.active_tasks_limit = active_tasks_limit;
        this.blocked_tasks = blocked_tasks;
        this.blocked_tasks_all_time = blocked_tasks_all_time;
        this.completed_tasks = completed_tasks;
        this.pending_tasks = pending_tasks;
    }

    @Override
    public String toString() {
        return "ThreadPools{" + "name=" + name + ", active_tasks=" + active_tasks + ", active_tasks_limit=" + active_tasks_limit + ", blocked_tasks=" + blocked_tasks + ", blocked_tasks_all_time=" + blocked_tasks_all_time + ", completed_tasks=" + completed_tasks + ", pending_tasks=" + pending_tasks + '}';
    }
    
    /**
     * Get thread pool name.
     * @return String
     * @deprecated Use {@link name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     * Get active_tasks.
     * @return number
     * @deprecated Use {@link active_tasks()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public int getActive_tasks() {
        return active_tasks;
    }

    /**
     * Get active_tasks_limit.
     * @return number
     * @deprecated Use {@link active_tasks_limit()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public int getActive_tasks_limit() {
        return active_tasks_limit;
    }

    /**
     * Get blocked_tasks.
     * @return number
     * @deprecated Use {@link blocked_tasks()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getBlocked_tasks() {
        return blocked_tasks;
    }

    /**
     * Get blocked_tasks_all_time.
     * @return number
     * @deprecated Use {@link blocked_tasks_all_time()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getBlocked_tasks_all_time() {
        return blocked_tasks_all_time;
    }

    /**
     * Get completed_tasks.
     * @return count
     * @deprecated Use {@link completed_tasks()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getCompleted_tasks() {
        return completed_tasks;
    }

    /**
     * Get pending_tasks.
     * @return number
     * @deprecated Use {@link pending_tasks()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public int getPending_tasks() {
        return pending_tasks;
    }
}
