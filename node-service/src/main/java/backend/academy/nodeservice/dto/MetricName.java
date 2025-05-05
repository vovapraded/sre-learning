package backend.academy.nodeservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricName {
    GET_REQUESTS_TOTAL("get.by.key.requests.total", "Total GET requests"),
    GET_ERRORS_TOTAL("get.by.key.errors.total", "GET errors"),
    GET_LATENCY("get.by.key.latency", "Latency of GET"),

    SAVE_REQUESTS_TOTAL("save.requests.total", "Total SAVE requests"),
    SAVE_LATENCY("save.latency", "Latency of SAVE"),

    REPLICATION_SUCCESS_TOTAL("replication.success.total", "Successful replications"),
    REPLICATION_FAILURE_TOTAL("replication.failure.total", "Failed replications"),
    REPLICATION_LATENCY("replication.latency", "Latency of replication");

    private final String metricName;
    private final String description;
}
