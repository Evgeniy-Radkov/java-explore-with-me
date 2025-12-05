package ru.practicum.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdOrderByCreatedOnDesc(Long eventId, Pageable pageable);
}
