package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.CommentService;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return commentService.getComments(eventId, from, size);
    }
}
