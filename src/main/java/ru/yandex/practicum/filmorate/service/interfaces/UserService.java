package ru.yandex.practicum.filmorate.service.interfaces;

import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserEvent;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();

    User addNewUser(User user);

    User updateUser(User newUser);

    User getUserById(Long userId);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    void deleteUserById(long userId);

    List<User> getCommonFriends(Long userId, Long otherId);

    List<UserEvent> getEvents(long userId);

}
