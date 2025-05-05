package backend.academy.gatewayservice.registry;

import backend.academy.gatewayservice.client.NodeClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeClientRegistry {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate = new RestTemplate();

    private final AtomicReference<List<NodeClient>> clientsRef = new AtomicReference<>(List.of());
    @PostConstruct
    public void init(){
        refreshClients();
    }
    public List<NodeClient> getClients() {
        return clientsRef.get();
    }

    @Scheduled(fixedDelay = 5000)
    public void refreshClients() {
        List<ServiceInstance> instances = discoveryClient.getInstances("my-app-node-service");

        List<NodeClient> newClients = instances.stream()
                .map(instance -> new NodeClient(instance.getUri().toString(), restTemplate))
                .collect(Collectors.toList());

        clientsRef.set(newClients);
        log.debug("Refreshed node clients: {}", newClients);
    }
}
