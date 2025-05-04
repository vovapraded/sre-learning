package backend.academy.nodeservice.dto;

import jakarta.validation.constraints.NotBlank;

public record DataRequest(
        @NotBlank String key,
        @NotBlank String value
) {}
