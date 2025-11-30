package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  String sort,
                                  int from,
                                  int size);

    EventFullDto getEvent(Long eventId);
}
