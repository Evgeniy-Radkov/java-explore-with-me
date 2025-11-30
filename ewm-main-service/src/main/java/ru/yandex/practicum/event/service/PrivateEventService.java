package ru.yandex.practicum.event.service;

import ru.yandex.practicum.event.dto.EventFullDto;
import ru.yandex.practicum.event.dto.EventShortDto;
import ru.yandex.practicum.event.dto.NewEventDto;
import ru.yandex.practicum.event.dto.UpdateEventUserRequest;

import java.util.List;

public interface PrivateEventService {

    List<EventShortDto> getUserEvents(Long userId, int from, int size);

    EventFullDto createEvent(Long userId, NewEventDto dto);

    EventFullDto getUserEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto);
}
