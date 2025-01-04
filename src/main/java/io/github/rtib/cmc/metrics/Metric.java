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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metric.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class Metric {

    private static final Logger LOG = LoggerFactory.getLogger(Metric.class);
    private final Charset encoding = StandardCharsets.UTF_8;
    
    private final String name;
    private final String help;
    private final MetricType type;
    private final List<Label> commonLabels;
    private final Set<List<Label>> instances = new HashSet<>();
    private final Map<List<Label>, MetricValue> values = new HashMap<>();
    
    /**
     * Private constructor of metric.
     * 
     * @param name metric name which needs to comply with Prometheus rules
     * @param help string describing the metric
     * @param type one of MetricTypes items
     * @param commonLabels list of labels all metric instances are applied to
     */
    private Metric(
                String name,
                String help,
                MetricType type,
                List<Label> commonLabels
    ) {
        this.name = name;
        this.help = help;
        this.type = type;
        this.commonLabels = commonLabels;
    }
    
    /**
     * Register an instance of the metric.
     * Each instance has a distinct list of labels.
     * 
     * @param labels distinguished list of labels applied to this instance
     */
    public synchronized void addInstance(List<Label> labels) {
        this.instances.add(labels);
    }

    /**
     * Cease the registration of a particular metric instance. This will remove
     * the metric instance from the export.
     * 
     * @param labels distinguished labels of the instance
     */
    public synchronized void removeInstance(List<Label> labels) {
        this.values.remove(labels);
        this.instances.remove(labels);
    }

    /**
     * Set the value of a particular metric instance.
     * 
     * @param labels list of labels to identify the instance
     * @param value actual metric value
     */
    public synchronized void setValue(List<Label> labels, double value) {
        if (instances.contains(labels))
            this.values.put(labels, new MetricValue(value));
    }
    
    /**
     * Generate the help string of the export.
     * 
     * @return help string
     */
    public String getHelp() {
        return new StringBuilder()
                .append("# HELP ")
                .append(name)
                .append(' ')
                .append(help)
                .append('\n')
                .toString();
    }
    
    public void writeHelp(OutputStream out) {
        try {
            out.write(getHelp().getBytes(encoding));
        } catch (IOException ex) {
            LOG.atTrace().log(null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Generate the type string of the export.
     * 
     * @return type string
     */
    public String getType() {
        return new StringBuilder()
                .append("# TYPE ")
                .append(name)
                .append(' ')
                .append(type)
                .append('\n')
                .toString();
    }
    
    public void writeType(OutputStream out) {
        try {
            out.write(getType().getBytes(encoding));
        } catch (IOException ex) {
            LOG.atTrace().log(null, ex);
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Generate the export of all instances.
     * 
     * @return string containing the instance exports
     */
    public String getInstances() {
        OutputStream out = new ByteArrayOutputStream();
        writeInstances(out);
        return out.toString();
    }
    
    public void writeInstances(OutputStream out) {
        StringBuilder sb;
        for (var value : values.entrySet()) {
            sb = new StringBuilder();
            sb.append(name)
                    .append('{');
            List<Label> allLabels = new ArrayList<>();
            allLabels.addAll(commonLabels);
            allLabels.addAll(value.getKey());
            sb.append(allLabels
                    .stream()
                    .map(label -> label.toString())
                    .collect(Collectors.joining(",")))
                .append('}')
                .append(' ')
                .append(value.getValue())
                .append('\n');
            try {
                out.write(sb.toString().getBytes(encoding));
            } catch (IOException ex) {
                LOG.atTrace().log("Failed to write {}", value, ex);
                throw new RuntimeException(ex);
            }
        }
    }

    public void write(OutputStream out) {
        writeHelp(out);
        writeType(out);
        writeInstances(out);
    }
    
    @Override
    public String toString() {
        OutputStream out = new ByteArrayOutputStream();
        write(out);
        return out.toString();
    }

    public String getName() {
        return this.name;
    }

    /**
     * Builder to construct a Metric.
     */
    public static class Builder {

        private final Pattern ALLOWED_NAME = Pattern.compile("^[a-zA-Z_:][a-zA-Z0-9_:]*$");
        
        private String name;
        private String help;
        private MetricType type = MetricType.UNTYPED;
        private final List<Label> commonLabels = new ArrayList<>();

        /**
         * Create a pristine Builder instance.
         */
        public Builder() {
        }
        
        /**
         * Instantiate the Metric.
         * 
         * @return the built metric
         * @throws MetricException if construction of the Metric is not possible
         */
        public Metric build() throws MetricException {
            if (name.isBlank())
                throw new MetricException("Metric name cannot be empty.");
            return new Metric(name, help, type, List.copyOf(commonLabels));
        }
        
        /**
         * Set metric name. A valid name must comply Prometheus naming rules.
         * 
         * @param name name of the metric to be built
         * @return this builder instance
         * @throws MetricException if the name doesn't comply with Prometheus rules
         */
        public Builder withName(final String name) throws MetricException {
            if (ALLOWED_NAME.matcher(name).matches())
                this.name = name;
            else
                throw new MetricException("Metric name must comply Prometheus requirements. See https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels");
            return this;
        }
        
        /**
         * Help string of the metric to be built. The help string will be printed
         * to the export as comment on top of the instances of this metric.
         * 
         * @param help the help string
         * @return this builder instance
         */
        public Builder withHelp(final String help) {
            this.help = help;
            return this;
        }
        
        /**
         * Set the type of this metric. This is optional, default type will be
         * MetricType.UNTYPED.
         * 
         * @param type an item of MetricType
         * @return this builder instance
         */
        public Builder withType(final MetricType type) {
            this.type = type;
            return this;
        }
        
        /**
         * Add a Label to the list of common labels. This instantiates a Label.
         * 
         * @param name label name
         * @param value label value
         * @return this builder instance
         * @throws MetricException if the label name doesn't comply with Prometheus rules
         */
        public Builder withCommonLabel(String name, String value) throws MetricException {
            this.commonLabels.add(new Label(name, value));
            return this;
        }
        
        /**
         * Add a list of labels to the common labels list. The list of labels
         * to be added needs to be prepared in advance.
         * 
         * @param labels list of labels to be added
         * @return this builder instance
         */
        public Builder withCommonLabels(List<Label> labels) {
            this.commonLabels.addAll(labels);
            return this;
        }
    }
}
