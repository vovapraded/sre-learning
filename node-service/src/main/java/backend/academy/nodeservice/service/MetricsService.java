package backend.academy.nodeservice.service;

import backend.academy.nodeservice.dto.MetricName;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry registry;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final String podName = System.getenv().getOrDefault("HOSTNAME", "unknown");

    public void increment(MetricName metric) {
        log.info("Incrementing metric: {}", metric.metricName());
        counters.computeIfAbsent(metric.metricName(), name ->
                Counter.builder(name)
                        .description(metric.description())
                        .tag("pod_name", podName)
                        .register(registry)
        ).increment();
    }

    public <T> T recordLatency(MetricName metric, SupplierWithException<T> supplier) throws Exception {
        Timer timer = timers.computeIfAbsent(metric.metricName(), name ->
                Timer.builder(name)
                        .description(metric.description())
                        .tag("pod_name", podName)
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .register(registry)
        );

        long start = System.nanoTime();
        try {
            return supplier.get();
        } finally {
            long end = System.nanoTime();
            timer.record(end - start, TimeUnit.NANOSECONDS);
        }
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws Exception;
    }
}
