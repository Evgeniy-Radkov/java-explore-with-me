package ru.practicum.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    // Закрытый API
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    // Админский поиск событий
    @Query("""
        SELECT e FROM Event e
        WHERE (:users IS NULL OR e.initiator.id IN :users)
          AND (:states IS NULL OR e.state IN :states)
          AND (:categories IS NULL OR e.category.id IN :categories)
          AND e.eventDate >= :rangeStart
          AND e.eventDate <= :rangeEnd
        """)
    List<Event> searchAdminEvents(
            @Param("users") List<Long> users,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    // Публичный поиск событий
    @Query("""
            SELECT e FROM Event e
            WHERE e.state = ru.practicum.event.enums.EventState.PUBLISHED
              AND (:categories IS NULL OR e.category.id IN :categories)
              AND (:paid IS NULL OR e.paid = :paid)
              AND e.eventDate >= :rangeStart
              AND e.eventDate <= :rangeEnd
            """)
    List<Event> searchPublicEvents(
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable
    );

    boolean existsByCategory_Id(Long categoryId);
}
