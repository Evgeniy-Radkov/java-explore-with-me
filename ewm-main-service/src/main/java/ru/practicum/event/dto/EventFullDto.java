package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.Location;
import ru.practicum.user.dto.UserShortDto;

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
