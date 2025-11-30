package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Закрытый API
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    // Админский поиск событий
    @Query(
            "SELECT e FROM Event e " +
                    "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
                    "AND (:states IS NULL OR e.state IN :states) " +
                    "AND (:categories IS NULL OR e.category.id IN :categories) " +
                    "AND (:rangeStart IS NULL OR e.eventDate >= :rangeStart) " +
                    "AND (:rangeEnd IS NULL OR e.eventDate <= :rangeEnd)"
    )
    List<Event> searchAdminEvents(List<Long> users,
                                  List<EventState> states,
                                  List<Long> categories,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Pageable pageable);

    // Публичный поиск событий
    @Query(
            "SELECT e FROM Event e " +
                    "WHERE e.state = ru.practicum.event.enums.EventState.PUBLISHED " +
                    "AND (:text IS NULL OR " +
                    "     (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                    "      OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))) " +
                    "AND (:categories IS NULL OR e.category.id IN :categories) " +
                    "AND (:paid IS NULL OR e.paid = :paid) " +
                    "AND e.eventDate >= :rangeStart " +
                    "AND e.eventDate <= :rangeEnd"
    )
    List<Event> searchPublicEvents(String text,
                                   List<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Pageable pageable);
}
