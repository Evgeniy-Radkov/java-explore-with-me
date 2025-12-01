package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    private static final LocalDateTime STATS_START =
            LocalDateTime.of(2000, 1, 1, 0, 0);

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         int from,
                                         int size) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = rangeStart.plusYears(1000);
        }

        Sort springSort = Sort.by("eventDate").ascending();
        PageRequest page = PageRequest.of(from / size, size, springSort);

        List<Long> categoriesParam = (categories == null || categories.isEmpty()) ? null : categories;

        List<Event> events = eventRepository.searchPublicEvents(
                categoriesParam,
                paid,
                rangeStart,
                rangeEnd,
                page
        );

        if (events.isEmpty()) {
            return List.of();
        }

        if (text != null && !text.isBlank()) {
            String lowerText = text.toLowerCase();
            events = events.stream()
                    .filter(e ->
                            (e.getAnnotation() != null &&
                                    e.getAnnotation().toLowerCase().contains(lowerText)) ||
                                    (e.getDescription() != null &&
                                            e.getDescription().toLowerCase().contains(lowerText))
                    )
                    .toList();

            if (events.isEmpty()) {
                return List.of();
            }
        }

        List<String> uris = events.stream()
                .map(e -> "/events/" + e.getId())
                .toList();

        LocalDateTime statsEnd = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(STATS_START, statsEnd, uris, true);

        Map<String, Long> viewsByUri = (stats == null)
                ? Collections.emptyMap()
                : stats.stream()
                .collect(Collectors.toMap(
                        ViewStatsDto::getUri,
                        ViewStatsDto::getHits
                ));

        List<EventShortDto> result = events.stream()
                .map(event -> {
                    long confirmed = requestRepository
                            .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

                    if (Boolean.TRUE.equals(onlyAvailable)) {
                        Integer limit = event.getParticipantLimit();
                        int limitValue = (limit == null ? 0 : limit);

                        if (limitValue > 0 && confirmed >= limitValue) {
                            return null;
                        }
                    }

                    EventShortDto dto = eventMapper.toEventShortDto(event);
                    dto.setConfirmedRequests(confirmed);

                    String uri = "/events/" + event.getId();
                    Long views = viewsByUri.getOrDefault(uri, 0L);
                    dto.setViews(views);

                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if ("VIEWS".equalsIgnoreCase(sort)) {
            result.sort(Comparator.comparing(
                    EventShortDto::getViews,
                    Comparator.nullsFirst(Long::compareTo)
            ).reversed());
        }

        return result;
    }

    @Override
    public EventFullDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: " + eventId));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие не найдено: " + eventId);
        }

        EventFullDto dto = eventMapper.toEventFullDto(event);

        long confirmed = requestRepository
                .countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        dto.setConfirmedRequests(confirmed);

        String uri = "/events/" + eventId;
        LocalDateTime statsEnd = LocalDateTime.now();

        List<ViewStatsDto> stats = statsClient.getStats(STATS_START, statsEnd, List.of(uri), true);

        long views = 0L;
        if (stats != null && !stats.isEmpty()) {
            views = stats.get(0).getHits();
        }
        dto.setViews(views);

        return dto;
    }
}
