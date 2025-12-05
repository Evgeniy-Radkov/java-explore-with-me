package ru.practicum.event.dto;

import lombok.Data;
import ru.practicum.event.enums.AdminStateAction;

@Data
public class UpdateEventAdminRequest extends BaseUpdateEventRequest {

    private AdminStateAction stateAction;
}
