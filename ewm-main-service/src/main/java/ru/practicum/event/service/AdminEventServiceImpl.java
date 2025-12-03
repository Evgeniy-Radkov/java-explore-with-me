package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.CategoryRepository;
import ru.practicum.event.Event;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.ParticipationRequest;
import ru.practicum.request.ParticipationRequestRepository;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    private final ParticipationRequestRepository requestRepository;
    private final StatsClient statsClient;

    private static final LocalDateTime STATS_START =
            LocalDateTime.of(2000, 1, 1, 0, 0);

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(List<Long> users,
                                           List<EventState> states,
                                           List<Long> categories,
                                           LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd,
                                           int from,
                                           int size) {

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new ValidationException("Дата окончания не может быть раньше даты начала");
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(2000, 1, 1, 0, 0);
        }

        PageRequest page = PageRequest.of(from / size, size);

        List<Long> usersParam = (users == null || users.isEmpty()) ? null : users;
        List<EventState> statesParam = (states == null || states.isEmpty()) ? null : states;
        List<Long> categoriesParam = (categories == null || categories.isEmpty()) ? null : categories;

        List<Event> events = eventRepository.searchAdminEvents(
                usersParam,
                statesParam,
                categoriesParam,
                rangeStart,
                rangeEnd,
                page
        );

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        List<ParticipationRequest> confirmedRequests =
                requestRepository.findAllByEventIdInAndStatus(eventIds, RequestStatus.CONFIRMED);

        Map<Long, Long> confirmedByEventId = confirmedRequests.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getEvent().getId(),
                        Collectors.counting()
                ));

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

        return events.stream()
                .map(event -> {
                    EventFullDto dto = eventMapper.toEventFullDto(event);

                    long confirmed = confirmedByEventId.getOrDefault(event.getId(), 0L);
                    dto.setConfirmedRequests(confirmed);

                    String uri = "/events/" + event.getId();
                    long views = viewsByUri.getOrDefault(uri, 0L);
                    dto.setViews(views);

                    return dto;
                })
                .toList();
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest dto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория не найдена: " + dto.getCategory()
                    ));
            event.setCategory(category);
        }

        eventMapper.updateEventFromAdminRequest(dto, event);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case PUBLISH_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException(
                                "Событие уже опубликовано и не может быть опубликовано повторно."
                        );
                    }
                    if (event.getState() == EventState.CANCELED) {
                        throw new ConflictException(
                                "Нельзя опубликовать отменённое событие."
                        );
                    }
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "Публиковать можно только события в статусе PENDING."
                        );
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getState() == EventState.PUBLISHED) {
                        throw new ConflictException(
                                "Нельзя отклонить уже опубликованное событие."
                        );
                    }
                    if (event.getState() != EventState.PENDING) {
                        throw new ConflictException(
                                "Отклонить можно только события в статусе PENDING."
                        );
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        return eventMapper.toEventFullDto(event);
    }
}
