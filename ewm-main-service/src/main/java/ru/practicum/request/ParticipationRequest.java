package ru.practicum.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.Event;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.user.User;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_request_event_requester",
                        columnNames = {"event_id", "requester_id"})
        }
)
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
}
