# cql-metrics-exporter

## Abstract

This project is meant as a PoC implementing a Prometheus metrics exporter for
Apache Cassandra. Since Cassandra started exposing metrics via virtual tables
available via CQL, a Prometheus exporter leveraging that seemed reasonable. 
Implemented as a sidecar service, cql-metrics-exporter is connecting an Apache
Cassandra node via localhost, queries its metrics via regular CQL and exports
them in Prometheus' text format on http://localhost:9500/metrics endpoint.

## Usage

The service may be started within an unprivileged user context with
`bin/cql-metrics-exporter`.

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
  * `cassandra_thread_pools` (gauge) - labeled with `name` of the threadpool and
`metric` referring to one of active_tasks, active_tasks_limit, blocked_tasks,
blocked_tasks_all_time or pending_tasks
  * `cassandra_completed_tasks_counter` - labeled with `name` as above and `metric`
completed_tasks
