package backend.academy.gatewayservice.controller;

import backend.academy.gatewayservice.dto.DataDto;
import backend.academy.gatewayservice.dto.DataRequest;
import backend.academy.gatewayservice.service.GatewayService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final GatewayService gatewayService;

    @PostMapping("/data")
    public ResponseEntity<Void> post(@RequestBody DataRequest data) {
        gatewayService.save(data);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/data/{key}")
    public ResponseEntity<DataDto> get(@PathVariable String key) {
        return ResponseEntity.of(gatewayService.get(key));
    }
}
