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

import io.github.rtib.cmc.model.system_schema.TableName;
import io.github.rtib.cmc.model.system_views.CacheName;
import io.github.rtib.cmc.model.system_views.ThreadPoolName;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Build a label list programmatically. 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class LabelListBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(LabelListBuilder.class);
    
    /**
     * Convert a TableName entity, consisting of keyspace name and table name,
     * into a pair of labels.
     * @param table an instance of a TableName entity
     * @return List containing Labels of keyspace and table
     */
    public static List<Label> valueOf(TableName table) {
        List<Label> labels = null;
        try {
            labels = new LabelListBuilder()
                    .addLabel("keyspace", table.keyspace_name())
                    .addLabel("table", table.table_name())
                    .build();
        } catch (MetricException ex) {
            LOG.atError().log("Failed to create List<Label> of TableName {}.", table, ex);
        }
        return labels;
    }
    
    /**
     * Convert a threadpool and a metric name to a list of label.
     * @param tp ThreadPoolName instance
     * @param metricName metric component name
     * @return list of labels
     */
    public static List<Label> valueOf(ThreadPoolName tp, String metricName) {
        List<Label> labels = null;
        try {
            labels = new LabelListBuilder()
                    .addLabel("threadpool", tp.name())
                    .addLabel("metric", metricName)
                    .build();
        } catch (MetricException ex) {
            LOG.atError().log("Failed to create List<Label> of threadpool/metric {}/{}.", tp, metricName, ex);
        }
        return labels;
    }
    
    /**
     * Convert a cache and metric name to a list of label.
     * @param cache CacheName instance
     * @param metricName metric component name
     * @return list of labels
     */
    public static List<Label> valueOf(CacheName cache, String metricName) {
        List<Label> labels = null;
        try {
            labels = new LabelListBuilder()
                    .addLabel("cache", cache.name())
                    .addLabel("metric", metricName)
                    .build();
        } catch (MetricException ex) {
            LOG.atError().log("Failed to create List<Label> of cache/metric {}/{}.", cache, metricName, ex);
        }
        return labels;
    }
    
    /**
     * Create a list of labels for a table with a metric component.
     * @param table TableName entity instance
     * @param metricName name of the metric component
     * @return list of labels
     */
    public static List<Label> valueOf(TableName table, String metricName) {
        List<Label> labels = null;
        try {
            labels = new LabelListBuilder()
                    .addLabels(LabelListBuilder.valueOf(table))
                    .addLabel("metric", metricName)
                    .build();
        } catch (MetricException ex) {
            LOG.atError().log("Failed to create List<Label> of table/metric {}/{}.", table, metricName, ex);
        }
        return labels;
    }
    
    private final List<Label> list = new LinkedList<>();
    
    /**
     * Create a LabelListBuilder.
     */
    public LabelListBuilder() {
    }

    /**
     * Create and add a new label.
     * @param name label name
     * @param value label value
     * @return this builder instance
     * @throws MetricException if the label name doesn't comply rules
     */
    public LabelListBuilder addLabel(String name, String value) throws MetricException {
        list.add(new Label(name, value));
        return this;
    }

    /**
     * Add a new label.
     * @param label Label instance to be add
     * @return this builder instance
     */
    public LabelListBuilder addLabel(Label label) {
        list.add(label);
        return this;
    }
    
    /**
     * Add a list of labels.
     * @param labels List of labels to be added
     * @return this builder instance
     */
    public LabelListBuilder addLabels(List<Label> labels) {
        list.addAll(labels);
        return this;
    }
    
    /**
     * Build the list of labels.
     * @return list of labels
     */
    public List<Label> build() {
        return List.copyOf(list);
    }
}
