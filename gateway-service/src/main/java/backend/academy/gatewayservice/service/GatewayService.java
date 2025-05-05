package backend.academy.gatewayservice.service;

import backend.academy.gatewayservice.client.NodeClient;
import backend.academy.gatewayservice.dto.DataDto;
import backend.academy.gatewayservice.dto.DataRequest;
import backend.academy.gatewayservice.dto.MetricName;
import backend.academy.gatewayservice.registry.NodeClientRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final NodeClientRegistry nodeClientRegistry;
    private final MetricsService metrics;

    public void save(DataRequest data) {
        NodeClient selected = chooseNode();
        metrics.increment(MetricName.REQUEST_RECEIVED);
        try {
            selected.save(data);
        } catch (Exception e) {
            metrics.increment(MetricName.ERRORS);
            throw new RuntimeException("Failed to save data", e);
        }
    }

    public Optional<DataDto> get(String key) {
        metrics.increment(MetricName.GET_REQUESTS_TOTAL);

        try {
            return metrics.recordLatency(MetricName.GET_LATENCY, () -> {
                List<CompletableFuture<Optional<DataDto>>> futures = nodeClientRegistry.getClients().stream()
                        .<CompletableFuture<Optional<DataDto>>>map(node -> CompletableFuture.supplyAsync(() -> {
                            try {
                                return Optional.ofNullable(node.get(key));
                            } catch (Exception e) {
                                return Optional.empty();
                            }
                        }))
                        .toList();

                // Приводим массив точно к CompletableFuture<?>[]
                CompletableFuture<?>[] futureArray = futures.toArray(new CompletableFuture[0]);

                // anyOf вернёт CompletableFuture<Object>
                CompletableFuture<Optional<DataDto>> first = CompletableFuture.anyOf(futureArray)
                        .thenApply(obj -> (Optional<DataDto>) obj);

                return first.get();
            });
        } catch (Exception e) {
            metrics.increment(MetricName.GET_ERRORS_TOTAL);
            return Optional.empty();
        }
    }



    private NodeClient chooseNode() {
        // simplest: random
        List<NodeClient> nodes = nodeClientRegistry.getClients();
        return nodes.get(ThreadLocalRandom.current().nextInt(nodes.size()));
    }
}
