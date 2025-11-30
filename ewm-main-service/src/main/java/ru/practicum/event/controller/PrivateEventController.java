package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.PrivateEventService;
import ru.practicum.request.RequestService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final PrivateEventService privateEventService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getUserEvents(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        return privateEventService.getUserEvents(userId, from, size);
    }

    @PostMapping
    public EventFullDto createEvent(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto dto
    ) {
        return privateEventService.createEvent(userId, dto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return privateEventService.getUserEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest dto
    ) {
        return privateEventService.updateEvent(userId, eventId, dto);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest
    ) {
        return requestService.updateEventRequests(userId, eventId, updateRequest);
    }

}
