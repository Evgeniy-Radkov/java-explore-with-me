package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.dto.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", source = "author")
    CommentDto toDto(Comment comment);
}
