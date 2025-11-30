package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.Event;
import ru.practicum.event.EventRepository;
import ru.practicum.event.enums.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.enums.RequestUpdateStatus;
import ru.practicum.user.User;
import ru.practicum.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(
                        "Пользователь не найден: " + userId
                ));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Нельзя отправить запрос на участие в своём событии");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя участвовать в неопубликованном событии");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Заявка на участие уже создана");
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != null
                && event.getParticipantLimit() > 0
                && confirmedRequests >= event.getParticipantLimit()) {
            throw new ValidationException("Лимит заявок на участие исчерпан");
        }

        ParticipationRequest request = new ParticipationRequest();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        if (Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        requestRepository.save(request);

        return requestMapper.toDto(request);
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {

        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(
                        "Заявка не найдена: " + requestId
                ));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Заявка не принадлежит данному пользователю");
        }

        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит данному пользователю");
        }

        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequests(Long userId,
                                                              Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        "Событие не найдено: " + eventId
                ));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не принадлежит данному пользователю");
        }

        if (updateRequest.getRequestIds() == null || updateRequest.getRequestIds().isEmpty()) {
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(List.of());
            result.setRejectedRequests(List.of());
            return result;
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

        if (requests.size() != updateRequest.getRequestIds().size()) {
            throw new NotFoundException("Некоторые заявки не найдены");
        }

        boolean hasWrongEvent = requests.stream()
                .anyMatch(r -> !r.getEvent().getId().equals(eventId));
        if (hasWrongEvent) {
            throw new ValidationException("Нельзя изменять заявки, относящиеся к другим событиям");
        }

        boolean hasNonPending = requests.stream()
                .anyMatch(r -> r.getStatus() != RequestStatus.PENDING);
        if (hasNonPending) {
            throw new ConflictException("Можно изменять только заявки в статусе PENDING");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        List<ParticipationRequestDto> confirmedDtos = new ArrayList<>();
        List<ParticipationRequestDto> rejectedDtos = new ArrayList<>();

        RequestUpdateStatus targetStatus = updateRequest.getStatus();

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit() == null ? 0 : event.getParticipantLimit();

        if (targetStatus == RequestUpdateStatus.CONFIRMED) {

            if (limit > 0 && confirmedCount >= limit) {
                throw new ConflictException("Лимит заявок на участие в событии достигнут");
            }

            for (ParticipationRequest r : requests) {
                if (limit > 0 && confirmedCount >= limit) {
                    r.setStatus(RequestStatus.REJECTED);
                    rejectedDtos.add(requestMapper.toDto(r));
                } else {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmedCount++;
                    confirmedDtos.add(requestMapper.toDto(r));
                }
            }

        } else if (targetStatus == RequestUpdateStatus.REJECTED) {

            for (ParticipationRequest r : requests) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedDtos.add(requestMapper.toDto(r));
            }
        }

        requestRepository.saveAll(requests);

        result.setConfirmedRequests(confirmedDtos);
        result.setRejectedRequests(rejectedDtos);

        return result;
    }
}
