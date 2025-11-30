package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.UpdateEventAdminRequest;
import ru.yandex.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {

    List<EventFullDto> searchEvents(List<Long> users,
                                    List<EventState> states,
                                    List<Long> categories,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    int from,
                                    int size);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto);
}
