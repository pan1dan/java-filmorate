package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.model.UserFriendsStorage;
import ru.yandex.practicum.filmorate.storage.model.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final UserFriendsStorage userFriendsStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("userFriendsDbStorage") UserFriendsStorage userFriendsStorage) {
        this.userStorage = userStorage;
        this.userFriendsStorage = userFriendsStorage;
    }

    public void deleteUserById(Long userId) {
        log.info("Начало работы метода по удалению пользователя с id = {}", userId);
        userStorage.deleteUserByIdFromStorage(userId);
    }

    public List<User> getAllUsers() {
        log.info("Начало работы метода по получению всех пользователей");
        return userStorage.getAllUsersFromStorage();
    }

    public User addNewUser(User user) {
        log.info("Начало работы метода по добавлению пользователя: {}", user);
        return userStorage.addNewUserInStorage(user);
    }

    public User updateUser(User newUser) {
        log.info("Начало работы метода по обновлению пользователя: {}", newUser);
        return userStorage.updateUserInStorage(newUser);
    }

    public User getUserById(Long userId) {
        log.info("Начало работы метода по получению пользователя по id = {}", userId);
        return userStorage.getUserByIdFromStorage(userId);
    }

    public void addNewFriendIdToUserFriendList(Long userId,
                                               Long friendId) {
        log.info("Начало работы метода по добавлению в список друзей пользователя с id = {} другого " +
                "пользователя с id = {}", userId, friendId);
        userStorage.getUserByIdFromStorage(userId);
        userStorage.getUserByIdFromStorage(friendId);
        userFriendsStorage.addUserFriend(userId, friendId);
    }

    public void deleteFriendIdFromUserFriendList(Long userId,
                                                 Long friendId) {
        log.info("Начало работы метода по удалению из списка друзей пользователя с id = {} другого " +
                "пользователя с id = {}", userId, friendId);
        userStorage.getUserByIdFromStorage(userId);
        userStorage.getUserByIdFromStorage(friendId);
        userFriendsStorage.deleteUserFriends(userId, friendId);
    }

    public List<User> getUserListFriends(Long userId) {
        log.info("Возвращение листа друзей пользователя: {}",
                userStorage.getUserByIdFromStorage(userId).getUserFriends().getFriendsIds());
        return userStorage.getUserByIdFromStorage(userId).getUserFriends().getFriendsIds()
                .stream()
                .map(userStorage::getUserByIdFromStorage)
                .toList();
    }

    public List<User> getCommonFriendListTwoUsers(Long userId,
                                                  Long otherId) {
        log.info("Начало работы метода по поиску общих друзей: пользователь с id = {} и другой " +
                "пользователь с id = {}", userId, otherId);
        log.debug("Получение пользователя из хранилища");
        User user = userStorage.getUserByIdFromStorage(userId);
        log.debug("Получение другого пользователя из хранилища");
        User otherUser = userStorage.getUserByIdFromStorage(otherId);
        log.info("Возвращение списка общих друзей пользователей");
        Set<Long> idsCommonFriends = user.getUserFriends().getFriendsIds()
                .stream()
                .filter(id -> otherUser.getUserFriends().getFriendsIds().contains(id))
                .collect(Collectors.toSet());
        return idsCommonFriends
                .stream()
                .map(userStorage::getUserByIdFromStorage)
                .toList();
    }
}
