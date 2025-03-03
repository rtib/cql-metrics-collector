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
package io.github.rtib.cmc.metrics;

/**
 * Metric types to be exported.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public enum MetricType {
    /**
     * Counter type
     */
    COUNTER ("counter"),
    
    /**
     * Gauge type
     */
    GAUGE ("gauge"),
    
    /**
     * Histogram type
     */
    HISTOGRAM ("histogram"),
    
    /**
     * Summary type
     */
    SUMMARY ("summary"),
    
    /**
     * No type specified
     */
    UNTYPED ("untyped")
    ;
    
    private String name;
    
    /**
     * Initialize the enum instance
     * @param key instance key
     */
    MetricType(String key) {
        this.name = key;
    }

    @Override
    public String toString() {
        return name;
    }
    
}
