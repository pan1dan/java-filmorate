package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User create(User user);

    User update(User newUser);

    User getUserById(Long userId);
}
