package ru.yandex.practicum.event.dto;

import lombok.Data;
import ru.yandex.practicum.category.dto.CategoryDto;
import ru.yandex.practicum.event.Location;
import ru.yandex.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private String state;

    private String title;

    private Long views;
}
