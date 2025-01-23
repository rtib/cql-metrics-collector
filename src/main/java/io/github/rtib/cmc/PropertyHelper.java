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
package io.github.rtib.cmc;

import java.time.Duration;

/**
 * Access relevant properties.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public enum PropertyHelper {
    /**
     * Java virtual machine distribution name.
     */
    JAVA_VM_NAME ("java.vm.name"),
    
    /**
     * Java version
     */
    JAVA_VERSION ("java.version"),
    
    /**
     * Max time a HTTP request transmission may take
     */
    HTTP_MAX_REQ_TIME ("sun.net.httpserver.maxReqTime"),
    
    /**
     * Max time a HTTP response transmission may take
     */
    HTTP_MAX_RSP_TIME ("sun.net.httpserver.maxRspTime"),
    
    /**
     * Name of the configuration root section
     */
    CONFIG_ROOT_SECTION ("io.github.rtib.cmc.Context.config_root_section", "cql-metrics-collector"),
    ;
    
    private final String key;
    private final String defaultVal;
    
    PropertyHelper(String key) {
        this.key = key;
        this.defaultVal = null;
    }
    
    PropertyHelper(String key, String defaultVal) {
        this.key = key;
        this.defaultVal = defaultVal;
    }
    
    /**
     * Get the property key
     * @return property key string
     */
    public String getKey() {
        return key;
    }
    
    /**
     * Get the property value as String
     * @return property value converted as String
     */
    public String getString() {
        String value = System.getProperty(key);
        return (value == null) ? defaultVal : STRING_CONVERTER.convert(value);
    }
    
    /**
     * Set the property String value.
     * @param value new String value to set
     */
    public void setString(final String value) {
        System.setProperty(key, value);
    }
    
    /**
     * Set the property value as numeric.
     * @param amount numeric value to set
     */
    public void set(final long amount) {
        setString(String.valueOf(amount));
    }
    
    /**
     * Set the property value from Duration as seconds.
     * @param duration Duration to set
     */
    public void setSeconds(final Duration duration) {
        set(duration.getSeconds());
    }
    
    /**
     * Property converter FI
     * @param <T> type to convert to
     */
    public interface PropertyConverter<T> {
        /**
         * Type conversion
         * @param value property value to convert
         * @return the value converted to type T
         */
        T convert(String value);
    }
    
    private static final PropertyConverter<String> STRING_CONVERTER = value -> value;
}
