package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserStorage {
    void deleteUserByIdFromStorage(Long userId);

    public List<User> getAllUsersFromStorage();

    public User addNewUserInStorage(User user);

    public User updateUserInStorage(User newUser);

    public User getUserByIdFromStorage(Long userId);
}
