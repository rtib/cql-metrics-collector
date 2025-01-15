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
package io.github.rtib.cmc.collectors;

import io.github.rtib.cmc.metrics.Label;
import io.github.rtib.cmc.metrics.LabelListBuilder;
import io.github.rtib.cmc.metrics.Metric;
import io.github.rtib.cmc.metrics.MetricException;
import io.github.rtib.cmc.metrics.MetricType;
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_views.BatchMetrics;
import io.github.rtib.cmc.model.system_views.BatchMetricsName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of batch statement metrics.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class BatchMetricsCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(BatchMetricsCollector.class);
    
    private Metric metricGauge;
    private Metric metricSummary;

    public BatchMetricsCollector() {
        super("batch_metrics");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.warn("BatchMetricsCollector is not available.");
            return;
        }
        
        try{
            metricGauge = new Metric.Builder()
                    .withName("cassandra_batch_metrics")
                    .withHelp("Metrics specific to batch statements")
                    .withType(MetricType.GAUGE)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricGauge);
            metricSummary = new Metric.Builder()
                    .withName("cassandra_batch_metrics_bucket")
                    .withHelp("Metrics specific to batch statements")
                    .withType(MetricType.SUMMARY)
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metricSummary);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initalize collector metrics.", ex);
        }
        super.activate(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }

    @Override
    public void deactivate() {
        super.deactivate(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        Repository.getInstance().remove(metricGauge);
        Repository.getInstance().remove(metricSummary);
    }

    @Override
    protected List<? extends MetricsIdentifier> getInstances() {
        return context.systemViewsDao.listBatchStatements().all();
    }

    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id);
    }
    
    private class Collector extends Thread {
        private final BatchMetricsName batchStatement;
        private final Map<String,List<Label>> metricLabels;
        
        public Collector(MetricsIdentifier id) throws MetricException {
            batchStatement = (BatchMetricsName) id;
            Map<String,List<Label>> labelmap = new HashMap<>();
            labelmap.put("max", new LabelListBuilder()
                    .addLabel("statement", batchStatement.name())
                    .addLabel("metric", "max")
                    .build()
            );
            labelmap.put("p50th", new LabelListBuilder()
                    .addLabel("statement", batchStatement.name())
                    .addLabel("quantile", "0.5")
                    .build()
            );
            labelmap.put("p999th", new LabelListBuilder()
                    .addLabel("statement", batchStatement.name())
                    .addLabel("quantile", "0.999")
                    .build()
            );
            labelmap.put("p99th", new LabelListBuilder()
                    .addLabel("statement", batchStatement.name())
                    .addLabel("quantile", "0.99")
                    .build()
            );
            metricLabels = Map.copyOf(labelmap);
            metricGauge.addInstance(labelmap.get("max"));
            metricSummary.addInstance(labelmap.get("p50th"));
            metricSummary.addInstance(labelmap.get("p999th"));
            metricSummary.addInstance(labelmap.get("p99th"));
        }

        @Override
        public void run() {
            BatchMetrics metrics = context.systemViewsDao.BatchMetrics(batchStatement.name());
            LOG.debug("Metrics acquired: {}", metrics);
            metricGauge.setValue(metricLabels.get("max"), metrics.max());
            metricSummary.setValue(metricLabels.get("p50th"), metrics.p50th());
            metricSummary.setValue(metricLabels.get("p999th"), metrics.p999th());
            metricSummary.setValue(metricLabels.get("p99th"), metrics.p99th());
        }
    }
}
