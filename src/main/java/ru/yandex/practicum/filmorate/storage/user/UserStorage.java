package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    public List<User> getAllUsersFromStorage();

    public User addNewUserInStorage(User user);

    public User updateUserInStorage(User newUser);

    public User getUserByIdFromStorage(Long userId);
}
