package ru.practicum.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.event.Location;
import ru.practicum.event.enums.AdminStateAction;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @Future
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;

    private AdminStateAction stateAction;
}
