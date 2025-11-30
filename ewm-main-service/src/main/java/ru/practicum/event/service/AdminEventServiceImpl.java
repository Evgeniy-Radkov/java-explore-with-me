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
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

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

        PageRequest page = PageRequest.of(from / size, size);

        List<Long> usersParam = (users == null || users.isEmpty()) ? null : users;
        List<EventState> statesParam = (states == null || states.isEmpty()) ? null : states;
        List<Long> categoriesParam = (categories == null || categories.isEmpty()) ? null : categories;

        return eventRepository.searchAdminEvents(
                        usersParam,
                        statesParam,
                        categoriesParam,
                        rangeStart,
                        rangeEnd,
                        page
                ).stream()
                .map(eventMapper::toEventFullDto)
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
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new ValidationException(
                                "Публиковать можно только события в статусе PENDING"
                        );
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                }
                case REJECT_EVENT -> {
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        throw new ValidationException(
                                "Нельзя отклонить уже опубликованное событие"
                        );
                    }
                    event.setState(EventState.CANCELED);
                }
            }
        }

        return eventMapper.toEventFullDto(event);
    }
}
