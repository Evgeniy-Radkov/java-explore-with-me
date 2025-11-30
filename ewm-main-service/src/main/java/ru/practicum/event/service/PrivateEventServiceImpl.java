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
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);

        return eventMapper.toEventShortDtoList(
                eventRepository.findAllByInitiatorId(userId, page)
        );
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto dto) {

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь не найден: " + userId
                ));

        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(
                        "Категория не найдена: " + dto.getCategory()
                ));

        Event event = eventMapper.toEvent(dto);

        event.setInitiator(initiator);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        eventRepository.save(event);

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getUserEvent(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException(
                    "Событие не принадлежит данному пользователю"
            );
        }

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException(
                    "Событие не принадлежит данному пользователю"
            );
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(
                    "Опубликованное событие нельзя изменить"
            );
        }

        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(
                            "Категория не найдена: " + dto.getCategory()
                    ));
            event.setCategory(category);
        }

        eventMapper.updateEventFromUserRequest(dto, event);

        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
            }
        }

        return eventMapper.toEventFullDto(event);
    }
}
