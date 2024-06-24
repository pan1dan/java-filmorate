package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    void deleteUserByIdFromStorage(long userId);

    User create(User user);
    void deleteUserByIdFromStorage(Long userId);
    User update(User newUser);

    User getUserById(Long userId);

}
