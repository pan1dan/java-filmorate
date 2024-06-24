package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserEvent;
import ru.yandex.practicum.filmorate.service.interfaces.UserService;
import ru.yandex.practicum.filmorate.storage.model.UserEventStorage;
import ru.yandex.practicum.filmorate.storage.model.UserFriendsStorage;
import ru.yandex.practicum.filmorate.storage.model.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final UserFriendsStorage userFriendsStorage;
    private final UserEventStorage userEventDbStorage;

    public void deleteUserById(Long userId) {
        log.info("Начало работы метода по удалению пользователя с id = {}", userId);
        userStorage.deleteUserByIdFromStorage(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Начало работы метода по получению всех пользователей");
        return userStorage.getAllUsers();
    }

    @Override
    public User addNewUser(User user) {
        log.info("Начало работы метода по добавлению пользователя: {}", user);
        return userStorage.create(user);
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Начало работы метода по обновлению пользователя: {}", newUser);
        return userStorage.update(newUser);
    }

    @Override
    public User getUserById(Long userId) {
        log.info("Начало работы метода по получению пользователя по id = {}", userId);
        return userStorage.getUserById(userId);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Начало работы метода по добавлению в список друзей пользователя с id = {} другого " +
                "пользователя с id = {}", userId, friendId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userFriendsStorage.addFriend(userId, friendId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        log.info("Начало работы метода по удалению из списка друзей пользователя с id = {} другого " +
                "пользователя с id = {}", userId, friendId);
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userFriendsStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        log.info("Возвращение друзей пользователя с id = {}",
                userStorage.getUserById(userId).getUserFriends().getFriendsIds());
        return userStorage.getUserById(userId).getUserFriends().getFriendsIds()
                .stream()
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Начало работы метода по поиску общих друзей: пользователь с id = {} и другой " +
                "пользователь с id = {}", userId, otherId);
        log.debug("Получение пользователя из хранилища");
        User user = userStorage.getUserById(userId);
        log.debug("Получение другого пользователя из хранилища");
        User otherUser = userStorage.getUserById(otherId);
        log.info("Возвращение списка общих друзей пользователей");
        Set<Long> idsCommonFriends = user.getUserFriends().getFriendsIds()
                .stream()
                .filter(id -> otherUser.getUserFriends().getFriendsIds().contains(id))
                .collect(Collectors.toSet());
        return idsCommonFriends
                .stream()
                .map(userStorage::getUserById)
                .toList();
    }

    @Override
    public List<UserEvent> getEvents(long userId) {
        // проверяем что такой пользователь есть в БД
        userStorage.getUserById(userId);
        // возвращаем все его события
        return userEventDbStorage.getUserEvents(userId);
    }
}
