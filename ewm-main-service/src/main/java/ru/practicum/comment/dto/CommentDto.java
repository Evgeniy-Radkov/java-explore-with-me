package ru.practicum.comment.dto;

import lombok.Data;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private UserShortDto author;
    private LocalDateTime createdOn;
}
