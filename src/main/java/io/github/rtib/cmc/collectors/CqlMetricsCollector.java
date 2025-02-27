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
import io.github.rtib.cmc.metrics.Repository;
import io.github.rtib.cmc.model.DaoSystemViewsV4;
import io.github.rtib.cmc.model.MapperSystemViews;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_views.CqlMetrics;
import io.github.rtib.cmc.model.system_views.CqlMetricsName;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collector of system cache metrics.
 * 
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class CqlMetricsCollector extends AbstractCollector {
    private static final Logger LOG = LoggerFactory.getLogger(CqlMetricsCollector.class);
    // private final Config config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), Config.class);
    
    private Metric metric;
    private DaoSystemViewsV4 dao;

    /**
     * Initialize collector.
     */
    public CqlMetricsCollector() {
        super("cql_metrics");
    }

    @Override
    public void activate() throws CollectorException {
        if (!isAvailable()) {
            LOG.warn("CqlMetricsCollector is not available.");
            return;
        }
        
        try {
            metric = new Metric.Builder()
                    .withName("cassandra_cql_metrics")
                    .withHelp("Metrics on CQL query execution.")
                    .withCommonLabels(context.commonLabels)
                    .build();
            Repository.getInstance().add(metric);
        } catch (MetricException ex) {
            throw new CollectorException("Failed to initalize collector metrics.", ex);
        }
        super.activate();
    }

    @Override
    protected void setup() {
        dao = MapperSystemViews.builder(context.cqlSession).build().systemViewsDaoV4();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        Repository.getInstance().remove(metric);
    }
    
    @Override
    protected Thread createCollectorTask(MetricsIdentifier id) throws MetricException {
        return new Collector(id);
    }

    @Override
    protected List<? extends MetricsIdentifier> getInstances() {
        return dao.listCqlMetrics().all();
    }
    
    private class Collector extends Thread {
        private final CqlMetricsName metricsName;
        private List<Label> metricLabels = Collections.emptyList();
        
        public Collector(MetricsIdentifier id) {
            metricsName = (CqlMetricsName) id;
            try {
                    metricLabels = new LabelListBuilder()
                            .addLabel("metric", metricsName.name())
                            .build();
                    metric.addInstance(metricLabels);
            } catch (MetricException ex) {
                LOG.atError().log("Failed to create Label.", ex);
            }
        }

        @Override
        public void run() {
            CqlMetrics CqlMetrics = dao.CqlMetrics(metricsName.name());
            LOG.debug("Metrics acquired: {}", CqlMetrics);
            metric.setValue(metricLabels, CqlMetrics.value());
        }
    }
}
