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

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Metric label.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public final class Label {

    // Regex of label names allowed in Prometheus
    private final Pattern ALLOWED_NAME = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private final Pattern RESERVED_NAME = Pattern.compile("^__.*");
    
    private final String name;
    private final String value;
    
    /**
     * Create a Label consisting of a name and value pair. Label names are validated
     * in order to comply Prometheus rules.
     * 
     * @param name label name
     * @param value label value
     * @throws MetricException if the label name is not valid
     * @see https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels
     */
    public Label(String name, String value) throws MetricException {
        if (!ALLOWED_NAME.matcher(name).matches())
            throw new MetricException("Label name must comply with Prometheus requirements. See https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels");
        if (RESERVED_NAME.matcher(name).matches())
            throw new MetricException("Label name reseved for internal use only. See https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels");
        
        this.name = name;
        this.value = value;
    }
    
    /**
     * Get the label name.
     * @return name
     */
    public String name() {
        return name;
    }
    
    /**
     * Get the label value.
     * @return value
     */
    public String value() {
        return value;
    }

    /**
     * Output the label as name=value string for use into export format.
     * @return exportable label format
     */
    @Override
    public String toString() {
        return name + "=\"" + value + '"';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Label other = (Label) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }
}
