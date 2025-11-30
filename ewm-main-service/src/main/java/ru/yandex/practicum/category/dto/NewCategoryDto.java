package ru.yandex.practicum.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotBlank
    private String name;
}
