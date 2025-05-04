package backend.academy.nodeservice.service;

import backend.academy.nodeservice.dto.DataRequest;
import backend.academy.nodeservice.exception.KeyNotFoundException;
import backend.academy.nodeservice.model.DataEntity;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {

    private final DataRepository repository;
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    private String getSelfHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("Could not resolve local host address: {}", e.getMessage());
            return null;
        }
    }
    public String getValueByKey(String key) {
        return repository.findByKey(key)
                .map(DataEntity::value)
                .orElseThrow(() -> new KeyNotFoundException("Key not found: " + key));
    }

    @SneakyThrows
    public void saveAndReplicate(DataRequest request)  {
        if (repository.existsByKey(request.key())) {
            log.info("Key {} already exists. Skipping save.", request.key());
            return;
        }

        repository.save(DataEntity.builder()
                .key(request.key())
                .value(request.value())
                .build());
        log.info("Saved key '{}' locally", request.key());

        String selfHost = getSelfHost();
        List<ServiceInstance> instances = discoveryClient.getInstances("node-service");

        String selfInstanceId = InetAddress.getLocalHost().getHostName(); // имя пода

        for (ServiceInstance instance : instances) {
            if (instance.getInstanceId() != null && instance.getInstanceId().contains(selfInstanceId)) {
                continue; // это мы сами
            }


            String url = instance.getUri() + "/data/internal/replicate";
            try {
                restTemplate.postForEntity(url, request, Void.class);
                log.info("Replicated to {}", url);
            } catch (Exception e) {
                log.warn("Replication to {} failed: {}", url, e.getMessage());
            }
        }
    }

    public void replicateOnly(DataRequest request) {
        if (repository.existsByKey(request.key())) {
            log.info("Key '{}' already exists. Skipping internal replication.", request.key());
            return;
        }

        repository.save(DataEntity.builder()
                .key(request.key())
                .value(request.value())
                .build());
        log.info("Internally replicated key '{}'", request.key());
    }
}
