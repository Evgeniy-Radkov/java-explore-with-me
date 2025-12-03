package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.event.enums.UserStateAction;

@Data
public class UpdateEventUserRequest extends BaseUpdateEventRequest {

    private UserStateAction stateAction;
}
