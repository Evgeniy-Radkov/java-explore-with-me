package ru.yandex.practicum.request.dto;

import lombok.Data;
import ru.yandex.practicum.request.enums.RequestUpdateStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private RequestUpdateStatus status;
}
