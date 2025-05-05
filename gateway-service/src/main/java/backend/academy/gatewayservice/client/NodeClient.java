package backend.academy.gatewayservice.client;

import backend.academy.gatewayservice.dto.DataDto;
import backend.academy.gatewayservice.dto.DataRequest;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class NodeClient {

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public void save(DataRequest data) {
        try {
            restTemplate.postForEntity(baseUrl + "/data", data, Void.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to replicate to " + baseUrl, e);
        }
    }

    public DataDto get(String key) {
        try {
            ResponseEntity<DataDto> response = restTemplate.getForEntity(baseUrl + "/data/" + key, DataDto.class);
            return response.getBody();
        } catch (RestClientException e) {
            throw new RuntimeException("Failed to fetch key from " + baseUrl, e);
        }
    }

    @Override
    public String toString() {
        return "NodeClient{" + "baseUrl='" + baseUrl + '\'' + '}';
    }
}
