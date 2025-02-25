# cql-metrics-collector

## Abstract

This project is meant as a PoC implementing a Prometheus metrics exporter for
Apache Cassandra. Since Cassandra started exposing metrics via virtual tables
available via CQL, a Prometheus exporter leveraging that seemed reasonable. 
Implemented as a sidecar service, cql-metrics-exporter is connecting an Apache
Cassandra node via localhost, queries its metrics via regular CQL and exports
them in Prometheus' text format on http://localhost:9500/metrics endpoint.

## Installation

Unback any of the bundles or install the Debian package provided in the release.

When installing the Debian package, a system user for the service will also be created and a SystemD service unit is installed, allowing to launch and control the application instance.

## Configuration

The project used [Typesafe config](https://lightbend.github.io/config/) for configuration. The main configuration file is placed at [/etc/application.conf](conf/application.conf) containing commented basic configuration.

If the Cassandra node is requiring user authentication, a tool user might be created in Cassandra. When using Cassandra `PasswordAuthenticator` and `CassandraAuthorizer`, follow the example below to set up a tool user"

```cql
cassandra@cqlsh> CREATE ROLE monitor WITH PASSWORD = 'secret' AND LOGIN = true AND SUPERUSER = false;
cassandra@cqlsh> GRANT SELECT PERMISSION ON KEYSPACE system_virtual_schema TO monitor;
cassandra@cqlsh> GRANT SELECT PERMISSION ON KEYSPACE system_views TO monitor;
```

Then follow the example in [/etc/application.conf](conf/application.conf) to set up the application authentication:

```
datastax-java-driver.advanced.auth-provider {
  class = PlainTextAuthProvider
  username = monitor
  password = secret
}
```

For more detailed configuration parameters refer to [reference.conf](src/main/resources/reference.conf).

## Usage

The service may be started within an unprivileged user context with
`bin/cql-metrics-collector`.

Or on Debian deployments just start the `cql-metrics-collector.service` unit with systemctl.

Metrics can be collected from HTTP `/metrics` endpoint available by default on port 9500.

## Dashboards

Metrics collected by a TSDB, e.g. [VictoriaMetrics](https://docs.victoriametrics.com/) can be visualized with e.g. [Grafana](https://grafana.com/oss/grafana/). While you are free to create metrics based visualizations, a few pre-defined dashboards are available in the [dashboards](dashboards) folder. These are part of the release, packaged in `dashboards.tar.gz` and can be imported to any Grafana instance.

## Metrics labels

All exported metrics get a few labels. These sets of labels are merged from
a common set of label and a set of individual lables.

The common set of labels contains:

* `cluster` - cluster name as configured for Cassandra
* `dc` - datacenter of the current node as repoterd by Cassandra's snitch
* `rack` - rack of the current node as reported by Cassandra's snitch
* `node` - resolved host name, IP address and port the Cassandra node is listening

## Supported metrics

Currently, only a few metrics are supported. The following virtual tables are
accessed and exported as listed:

* `disk_usage`, `max_partition_size`, `max_sstable_size`
  * `cassandra_<basename>` (gauge) - labeled with `keyspace` and `table`
* `thread_pools`
  * `cassandra_thread_pools` (gauge) - labeled with `name` of the threadpool and `metric` referring to one of active_tasks, active_tasks_limit, blocked_tasks, blocked_tasks_all_time or pending_tasks
  * `cassandra_completed_tasks_counter` - labeled with `name` as above and `metric` completed_tasks
* `caches`
  * `cassandra_system_caches` (gauge) - labeled with `name` of the system cache and `metric` referring to one of capacity_bytes, hit_ratio, recent_hit_rate_per_second, recent_request_rate_per_second or size_bytes
  * `cassandra_system_cache_counter` -  labeled with `name` as above and `metric` referring to one of entry_count, hit_count or request_count
* `coordinator_read_latency`, `coordinator_scan_latency`, `coordinator_write_latency`, `local_read_latency`, `local_scan_latency`, `local_write_latency`
  * all above latency metrics tables are exoprted using four metric names:
    * `cassandra_<basename>_count` - exporting the count field of the base table
    * `cassandra_<basename>_max` - exporting the max latency in milliseconds
    * `cassandra_<basename>_buckets` - exporting p50th and p99th buckets in milliseconds
    * `cassandra_<basename>_rate` - exporting the request rate per seconds
  * all metrics are labeled with `keyspace` and `table` referring to the subject of the metrics
  * the buckets are additionally labeled with `quantile`
* `rows_per_read`, `tombstones_per_read`
  * `cassandra_<basename>` - labeled with `keyspace`, `table` and `metric` referring to one of max, p50th and p99th
  * `cassandra_<basename>_count` - labeled with `keyspace`, `table` and `metric` referring to reads
* `batch_metrics`
  * `cassandra_batch_metrics` - labeled with `statement` and `metric="max"`
  * `cassandra_batch_metrics_summary` - labeled with `statement` and `quantile`
* `cql_metrics`
  * `cassandra_cql_metrics` - labeled with `metric` for the actual metric name
