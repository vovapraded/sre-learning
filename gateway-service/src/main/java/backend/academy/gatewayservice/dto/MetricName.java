package backend.academy.gatewayservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricName {

    // Gateway / GET
    GET_REQUESTS_TOTAL("get.by.key.requests.total", "Total GET requests by key"),
    GET_ERRORS_TOTAL("get.by.key.errors.total", "GET request errors"),
    GET_LATENCY("get.by.key.latency", "Latency of GET requests by key"),

    // Gateway / POST
    REQUEST_RECEIVED("save.request.total", "Save requests received"),
    ERRORS("save.errors.total", "Save request errors"),
    SAVE_LATENCY("save.latency", "Latency of save operation"),

    // Replication
    REPLICATION_SUCCESS_TOTAL("replication.success.total", "Successful replications"),
    REPLICATION_FAILURE_TOTAL("replication.failure.total", "Failed replications"),
    REPLICATION_LATENCY("replication.latency", "Latency of replication");

    private final String metricName;
    private final String description;
}
