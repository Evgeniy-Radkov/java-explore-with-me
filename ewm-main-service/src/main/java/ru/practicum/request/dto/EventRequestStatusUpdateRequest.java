package ru.practicum.request.dto;

import lombok.Data;
import ru.practicum.request.enums.RequestUpdateStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private RequestUpdateStatus status;
}
