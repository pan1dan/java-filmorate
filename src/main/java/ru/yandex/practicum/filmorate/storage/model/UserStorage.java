package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User create(User user);

    User update(User newUser);

    User getUserById(Long userId);
}
