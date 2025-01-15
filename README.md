# cql-metrics-collector

## Abstract

This project is meant as a PoC implementing a Prometheus metrics exporter for
Apache Cassandra. Since Cassandra started exposing metrics via virtual tables
available via CQL, a Prometheus exporter leveraging that seemed reasonable. 
Implemented as a sidecar service, cql-metrics-exporter is connecting an Apache
Cassandra node via localhost, queries its metrics via regular CQL and exports
them in Prometheus' text format on http://localhost:9500/metrics endpoint.

## Usage

The service may be started within an unprivileged user context with
`bin/cql-metrics-collector`.

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

* `disk_usage`
  * `cassandra_disk_usage` (gauge) - labeled with `keyspace` and `table`
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
* `tombstones_per_read`
  * `cassandra_tombstones_per_read` - labeled with `keyspace`, `table` and `metric` referring to one of max, p50th and p99th
  * `cassandra_tombstones_per_read_count` - labeled with `keyspace`, `table` and `metric` referring to reads
* `batch_metrics`
  * `cassandra_batch_metrics` - labeled with `statement` and `metric="max"`
  * `cassandra_batch_metrics_summary` - labeled with `statement` and `quantile`
* `max_partition_size`
  * `cassandra_max_partition_size` - labeled with `keyspace` and `table`
