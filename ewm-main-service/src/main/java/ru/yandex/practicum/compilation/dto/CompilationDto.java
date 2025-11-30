package ru.yandex.practicum.compilation.dto;

import lombok.Data;
import ru.yandex.practicum.event.dto.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {

    private Long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
