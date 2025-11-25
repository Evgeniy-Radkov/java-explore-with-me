package ru.practicum.stats.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EndpointHitDto {

    @NotBlank
    private String app;

    @NotBlank
    private String uri;

    @NotBlank
    private String ip;

    @NotBlank
    private String timestamp;
}
