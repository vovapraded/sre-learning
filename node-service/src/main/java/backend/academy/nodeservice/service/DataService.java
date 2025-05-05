package backend.academy.nodeservice.service;

import backend.academy.nodeservice.dto.DataDto;
import backend.academy.nodeservice.dto.MetricName;
import backend.academy.nodeservice.entity.DataEntity;
import backend.academy.nodeservice.exception.KeyNotFoundException;
import backend.academy.nodeservice.repository.DataRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {

    private final DataRepository repository;
    private final DiscoveryClient discoveryClient;
    private final MetricsService metrics;
    private final RestTemplate restTemplate = new RestTemplate();

    private String getSelfHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not resolve local host address: {}", e.getMessage());
            return null;
        }
    }

    public DataDto getValueByKey(String key) {
        metrics.increment(MetricName.GET_REQUESTS_TOTAL);
        try {
            return metrics.recordLatency(MetricName.GET_LATENCY, () ->
                    repository.findByKey(key)
                            .map(dataEntity -> new DataDto(dataEntity.key(), dataEntity.value()))
                            .orElseThrow(() -> {
                                metrics.increment(MetricName.GET_ERRORS_TOTAL);
                                return new KeyNotFoundException("Key not found: " + key);
                            })
            );
        } catch (Exception e) {
            // логика проброса необязательна, если метрики уже собраны
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void saveAndReplicate(DataDto request) {
        metrics.increment(MetricName.SAVE_REQUESTS_TOTAL);

        metrics.recordLatency(MetricName.SAVE_LATENCY, () -> {
            if (repository.existsByKey(request.key())) {
                log.info("Key {} already exists. Skipping save.", request.key());
                return null;
            }

            repository.save(DataEntity.builder()
                    .key(request.key())
                    .value(request.value())
                    .build());
            log.info("Saved key '{}' locally", request.key());

            String selfInstanceId = InetAddress.getLocalHost().getHostName();
            List<ServiceInstance> instances = discoveryClient.getInstances("node-service");

            for (ServiceInstance instance : instances) {
                if (instance.getInstanceId() != null && instance.getInstanceId().contains(selfInstanceId)) {
                    continue;
                }

                String url = instance.getUri() + "/data/internal/replicate";
                try {
                    long start = System.nanoTime();
                    restTemplate.postForEntity(url, request, Void.class);
                    metrics.increment(MetricName.REPLICATION_SUCCESS_TOTAL);
                    metrics.recordLatency(MetricName.REPLICATION_LATENCY, () -> {
                        TimeUnit.NANOSECONDS.sleep(System.nanoTime() - start);
                        return null;
                    });
                    log.info("Replicated to {}", url);
                } catch (Exception e) {
                    metrics.increment(MetricName.REPLICATION_FAILURE_TOTAL);
                    log.warn("Replication to {} failed: {}", url, e.getMessage());
                }
            }
            return null;
        });
    }

    public void replicateOnly(DataDto request) {
        if (repository.existsByKey(request.key())) {
            log.info("Key '{}' already exists. Skipping internal replication.", request.key());
            return;
        }

        repository.save(DataEntity.builder()
                .key(request.key())
                .value(request.value())
                .build());
        metrics.increment(MetricName.REPLICATION_SUCCESS_TOTAL);
        log.info("Internally replicated key '{}'", request.key());
    }
}
