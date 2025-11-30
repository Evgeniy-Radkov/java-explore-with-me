package ru.yandex.practicum.user;

import ru.yandex.practicum.user.dto.NewUserRequest;
import ru.yandex.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(NewUserRequest dto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void delete(Long userId);
}
