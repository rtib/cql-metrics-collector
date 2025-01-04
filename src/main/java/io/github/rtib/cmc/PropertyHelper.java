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
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public enum PropertyHelper {
    JAVA_VM_NAME ("java.vm.name"),
    JAVA_VERSION ("java.version"),
    
    HTTP_MAX_REQ_TIME ("sun.net.httpserver.maxReqTime"),
    HTTP_MAX_RSP_TIME ("sun.net.httpserver.maxRspTime"),
    
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
    
    public String getKey() {
        return key;
    }
    
    public String getString() {
        String value = System.getProperty(key);
        return (value == null) ? defaultVal : STRING_CONVERTER.convert(value);
    }
    
    public void setString(final String value) {
        System.setProperty(key, value);
    }
    
    public void set(final long amount) {
        setString(String.valueOf(amount));
    }
    
    public void setSeconds(final Duration duration) {
        set(duration.getSeconds());
    }
    
    public interface PropertyConverter<T> {
        T convert(String value);
    }
    
    private static final PropertyConverter<String> STRING_CONVERTER = value -> value;
}
