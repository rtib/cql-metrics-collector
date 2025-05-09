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

import com.typesafe.config.ConfigBeanFactory;
import io.github.rtib.cmc.model.MetricsIdentifier;
import io.github.rtib.cmc.model.system_schema.TableName;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Abstract class collecting metrics on a per table base.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public abstract class AbstractTableCollector extends AbstractCollector {
    // private static final Logger LOG = LoggerFactory.getLogger(AbstractTableCollector.class);
    
    /**
     * Configuration bean generic to all subordinate collectors.
     */
    protected final TableCollectorConfig config = ConfigBeanFactory.create(context.getConfigFor(this.getClass()), TableCollectorConfig.class);
    
    private static final Predicate<TableName> isUserKeyspace = new Predicate<>() {
        private final List<Pattern> USUAL_SUSPECTS = List.of(
            Pattern.compile("^system$"),
            Pattern.compile("^system_.*"),
            Pattern.compile("^dse_.*"),
            Pattern.compile("solr_admin"),
            Pattern.compile("OpsCenter")
        );
        
        @Override
        public boolean test(TableName t) {
            return USUAL_SUSPECTS.stream().allMatch(p -> !p.matcher(t.keyspace_name()).matches());
        }
    };
    
    /**
     * Initializing a collector for a given source instance.
     * @param source_table source table name promoted upstream
     */
    public AbstractTableCollector(String source_table) {
        super(source_table);
    }

    @Override
    protected List<? extends MetricsIdentifier> getInstances() {
        List<TableName> fulllist = context.systemSchemaDao.listAllTables().all();
        if (config.isIncludeSystemTables())
            return fulllist;
        
        return fulllist
                .stream()
                .filter(AbstractTableCollector.isUserKeyspace)
                .collect(Collectors.toList());
    }

    /**
     * Configuration bean.
     */
    protected static class TableCollectorConfig extends CollectorConfig {
        private boolean includeSystemTables;

        /**
         * Create the configuration bean instance.
         */
        public TableCollectorConfig() {
            super();
        }

        /**
         * Get whether or not metrics on system tables should be included.
         * @return true if system tables should be included
         */
        public boolean isIncludeSystemTables() {
            return includeSystemTables;
        }

        /**
         * Set whether or not metrics on system tables should be included.
         * @param includeSystemTables true to include system tables
         */
        public void setIncludeSystemTables(boolean includeSystemTables) {
            this.includeSystemTables = includeSystemTables;
        }
    }
}
