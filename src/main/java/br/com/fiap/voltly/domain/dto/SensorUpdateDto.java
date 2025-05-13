package br.com.fiap.voltly.domain.dto;

import jakarta.validation.constraints.NotBlank;

public record SensorUpdateDto(
        @NotBlank(message = "Serial number is required")
        String serialNumber,

        @NotBlank(message = "Type is required")
        String type

) { }
