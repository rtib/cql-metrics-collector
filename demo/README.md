# Dashboard demo

In this folder a demonstration appliance is available to scrape metrics from Cassandra nodes and visualise its metrics on Grafana dashboards.

## Usage

Set up the list of Cassandra nodes equipped with cql-metrics-collector in `demo/prometheus/cassandra_targets.json` and start the demo with

``` % docker-compose up```

Then go to http://localhost:3000 where a Grafana with provisioned dashboards should be available.

## Dashboards

Predefined dashboards are availble at `../dashboards` folder. Dashboards found there are provisioned into the demo environment and can also be imported to a Grafana having a Prometheus datasource configured as default providing the metrics collected from cql-metrics-collector instances.

Currently available dashboards are

* Cluster Overview - providing an overview of the whole Cluster
* Schema details - insights into the metrics of a selected keyspace/table
* Node details - per node metrics on the operation and service a selected node
