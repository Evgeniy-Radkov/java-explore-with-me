package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.event.Location;

import java.time.LocalDateTime;

@Data
public abstract class BaseUpdateEventRequest {

    @Size(min = 20, max = 2000)
    protected String annotation;

    protected Long category;

    @Size(min = 20, max = 7000)
    protected String description;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime eventDate;

    protected Location location;

    protected Boolean paid;

    @Min(0)
    protected Integer participantLimit;

    protected Boolean requestModeration;

    @Size(min = 3, max = 120)
    protected String title;
}
