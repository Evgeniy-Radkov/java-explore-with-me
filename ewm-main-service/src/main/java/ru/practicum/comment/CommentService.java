package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, NewCommentDto dto);

    List<CommentDto> getComments(Long eventId, int from, int size);

    void deleteOwn(Long userId, Long commentId);

    void deleteAdmin(Long commentId);
}
