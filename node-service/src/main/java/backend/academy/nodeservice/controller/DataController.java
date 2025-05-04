package backend.academy.nodeservice.controller;

import backend.academy.nodeservice.dto.DataRequest;
import backend.academy.nodeservice.service.DataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
@Tag(name = "Data API", description = "Управление хранением и репликацией ключ-значение")
public class DataController {

    private final DataService dataService;

    @Operation(summary = "Создать запись и реплицировать её на другие ноды")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Запись принята и реплицируется"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса")
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody
            @Parameter(description = "Ключ и значение для сохранения") DataRequest request) {
        dataService.saveAndReplicate(request);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Реплицировать запись только локально (без дальнейшей пересылки)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Репликация выполнена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат запроса")
    })
    @PostMapping("/internal/replicate")
    public ResponseEntity<Void> replicate(
            @Valid @RequestBody
            @Parameter(description = "Ключ и значение, полученные от других нод") DataRequest request) {
        dataService.replicateOnly(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить значение по ключу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Значение успешно получено"),
            @ApiResponse(responseCode = "404", description = "Ключ не найден")
    })
    @GetMapping("/{key}")
    public ResponseEntity<String> get(
            @PathVariable
            @Parameter(description = "Ключ для получения значения") String key) {
        return ResponseEntity.ok(dataService.getValueByKey(key));
    }
}
