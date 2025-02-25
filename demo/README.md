# Dashboard demo

In this folder a demonstration appliance is available to scrape metrics from Cassandra nodes and visualise its metrics on Grafana dashboards.

## Usage

Set up the list of Cassandra nodes equipped with cql-metrics-collector in `demo/cassandra_targets.json`. Start the demo with

``` % docker-compose up```

This will bring up two containers, one running [VictoriaMetrics](https://docs.victoriametrics.com/) configured to scrape the cql-metrics-collector targets listed in `cassandra_targets.json`. A second container will run a [Grafana](https://grafana.com/oss/grafana/) with provisioned datasource and the pre-built dashboards available.

Go to http://localhost:3000 where the demo will be available.
