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
package io.github.rtib.cmc.model.system_views;

import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import com.datastax.oss.driver.api.mapper.annotations.PropertyStrategy;

/**
 * Entity class for system_views.caches virtual table.
 * <pre>
 * VIRTUAL TABLE system_views.caches (
 *     name text PRIMARY KEY,
 *     capacity_bytes bigint,
 *     entry_count int,
 *     hit_count bigint,
 *     hit_ratio double,
 *     recent_hit_rate_per_second bigint,
 *     recent_request_rate_per_second bigint,
 *     request_count bigint,
 *     size_bytes bigint
 * ) WITH comment = 'system caches';
 * </pre>
 * This read-only entity class provides property getter methods of the legacy bean pattern as well, as
 * record style getter methods for forward compatibility with Java-14 records. Legacy getter methods are
 * annotated deprecated.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
@Entity
@PropertyStrategy(mutable = false)
public final class Caches {

    @PartitionKey private final String name;
    private final long capacity_bytes;
    private final int entry_count;
    private final long hit_count;
    private final double hit_ratio;
    private final long recent_hit_rate_per_second;
    private final long recent_request_rate_per_second;
    private final long request_count;
    private final long size_bytes;

    /**
     * Get cache name.
     * @return cache name as string
     */
    public String name() {
        return name;
    }

    /**
     * Cache capacity.
     * @return capacity in bytes
     */
    public long capacity_bytes() {
        return capacity_bytes;
    }

    /**
     * Cache entry count.
     * @return number of entries
     */
    public int entry_count() {
        return entry_count;
    }

    /**
     * Cache hit count.
     * @return hit count
     */
    public long hit_count() {
        return hit_count;
    }

    /**
     * Cache hit ratio.
     * @return hit ratio
     */
    public double hit_ratio() {
        return hit_ratio;
    }

    /**
     * Recent cache hit rate.
     * @return hit rate per second
     */
    public long recent_hit_rate_per_second() {
        return recent_hit_rate_per_second;
    }

    /**
     * Recent request rate.
     * @return request rate per second
     */
    public long recent_request_rate_per_second() {
        return recent_request_rate_per_second;
    }

    /**
     * Request count.
     * @return number of requests
     */
    public long request_count() {
        return request_count;
    }

    /**
     * Cache size.
     * @return cache size in bytes
     */
    public long size_bytes() {
        return size_bytes;
    }
    
    /**
     * Create entity instance.
     * @param name initial value
     * @param capacity_bytes initial value
     * @param entry_count initial value
     * @param hit_count initial value
     * @param hit_ratio initial value
     * @param recent_hit_rate_per_second initial value
     * @param recent_request_rate_per_second initial value
     * @param request_count initial value
     * @param size_bytes initial value
     */
    Caches(
            String name,
            long capacity_bytes,
            int entry_count,
            long hit_count,
            double hit_ratio,
            long recent_hit_rate_per_second,
            long recent_request_rate_per_second,
            long request_count,
            long size_bytes
    ) {
        this.name = name;
        this.capacity_bytes = capacity_bytes;
        this.entry_count = entry_count;
        this.hit_count = hit_count;
        this.hit_ratio = hit_ratio;
        this.recent_hit_rate_per_second = recent_hit_rate_per_second;
        this.recent_request_rate_per_second = recent_request_rate_per_second;
        this.request_count = request_count;
        this.size_bytes = size_bytes;
    }

    /**
     * Get cache name.
     * @return String
     * @deprecated Use {@link name()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public String getName() {
        return name;
    }

    /**
     * Cache capacity.
     * @return long
     * @deprecated Use {@link capacity_bytes()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getCapacity_bytes() {
        return capacity_bytes;
    }

    /**
     * Cache entry count.
     * @return int
     * @deprecated Use {@link entry_count()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public int getEntry_count() {
        return entry_count;
    }

    /**
     * Cache hit count.
     * @return long
     * @deprecated Use {@link hit_count()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getHit_count() {
        return hit_count;
    }

    /**
     * Cache hit ratio.
     * @return double
     * @deprecated Use {@link hit_ratio()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public double getHit_ratio() {
        return hit_ratio;
    }

    /**
     * Recent cache hit rate.
     * @return long
     * @deprecated Use {@link recent_hit_rate_per_second()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getRecent_hit_rate_per_second() {
        return recent_hit_rate_per_second;
    }

    /**
     * Recent request rate.
     * @return long
     * @deprecated Use {@link recent_request_rate_per_second()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getRecent_request_rate_per_second() {
        return recent_request_rate_per_second;
    }

    /**
     * Request count.
     * @return long
     * @deprecated Use {@link request_count()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getRequest_count() {
        return request_count;
    }

    /**
     * Cache size.
     * @return long
     * @deprecated Use {@link size_bytes()} instead. Will be discontinued with Java-14.
     */
    @Deprecated
    public long getSize_bytes() {
        return size_bytes;
    }
}
