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

/**
 * Collector interface to be implemented by all metrics collector classes.
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public interface ICollector {

    /**
     * Activate the collector instance by setting up its metrics and update
     * task.
     * @throws CollectorException in case of any failure preventing activation.
     */
    void activate() throws CollectorException;

    /**
     * Deactivate the collector instance by shutting down all scheduled
     * update and collector tasks and remove all metrics from metrics
     * repository.
     */
    void deactivate();

    /**
     * Check if the source for the collector is available in the underlying
     * Cassandra version.
     * @return true if the collector is available, false otherwise
     */
    boolean isAvailable();
    
}
