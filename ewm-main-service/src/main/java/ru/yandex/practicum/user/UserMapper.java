package ru.yandex.practicum.user;

import org.mapstruct.Mapper;
import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(NewUserRequest dto);
}
