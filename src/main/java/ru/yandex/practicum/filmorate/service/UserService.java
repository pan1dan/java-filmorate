package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public Set<Long> addNewFriendIdToUserFriendList(Long userId,
                                                  Long friendId,
                                                  InMemoryUserStorage inMemoryUserStorage) {
        log.info("Начало работы метода по добавлению нового друга с id = {} " +
                "в лист пользователя c id = {}", friendId, userId);
        log.debug("Получение пользователя из хранилища");
        User user = inMemoryUserStorage.getUserByIdFromStorage(userId);
        log.debug("Получение добавляемого пользователя из хранилища");
        User friendUser = inMemoryUserStorage.getUserByIdFromStorage(friendId);
        log.debug("Получение листа c id друзей пользователя");
        Set<Long> newUserFriendsList = user.getFriendsList();
        log.debug("Получение листа c id друзей добавляемого пользователя");
        Set<Long> newUserFriendFriendsList = friendUser.getFriendsList();
        log.debug("Добавление id нового друга в лист c id друзей пользователя");
        newUserFriendsList.add(friendId);
        log.debug("Добавление id пользователя в лист c id друзей добавляемого пользователя");
        newUserFriendFriendsList.add(userId);
        log.debug("Сохранение обновленного листа c id друзей пользователя");
        user.setFriendsList(newUserFriendsList);
        log.debug("Сохранение обновленного листа c id друзей добавляемого пользователя");
        friendUser.setFriendsList(newUserFriendFriendsList);
        log.info("Возвращение обновленного листа с id друзей: {}", user.getFriendsList());
        return user.getFriendsList();
    }

    public Set<Long> deleteFriendIdFromUserFriendList(Long userId,
                                               Long friendId,
                                               InMemoryUserStorage inMemoryUserStorage) {
        log.info("Начало работы метода по удаление друга с id = {}" +
                "из листа пользователя c id = {}", friendId, userId);
        log.debug("Получение пользователя из хранилища");
        User user = inMemoryUserStorage.getUserByIdFromStorage(userId);
        log.debug("Получение удаляемого пользователя из хранилища");
        User friendUser = inMemoryUserStorage.getUserByIdFromStorage(friendId);
        log.debug("Получение листа c id друзей пользователя");
        Set<Long> newUserFriendsList = user.getFriendsList();
        log.debug("Получение листа c id друзей добавляемого пользователя");
        Set<Long> newUserFriendFriendsList = friendUser.getFriendsList();
        log.debug("Удаление id друга из листа c id друзей пользователя");
        newUserFriendsList.remove(friendId);
        log.debug("Удаление id пользователя из листа c id друзей удаляемого пользователя");
        newUserFriendFriendsList.remove(userId);
        log.debug("Сохранение обновленного листа c id друзей пользователя");
        user.setFriendsList(newUserFriendsList);
        log.debug("Сохранение обновленного листа c id друзей удаляемого пользователя");
        friendUser.setFriendsList(newUserFriendFriendsList);
        log.info("Возвращение обновленного листа с id друзей: {}", user.getFriendsList());
        return user.getFriendsList();
    }

    public List<User> getUserListFriends(Long userId, InMemoryUserStorage inMemoryUserStorage) {
        log.info("Возвращение листа друзей пользователя: {}",
                inMemoryUserStorage.getUserByIdFromStorage(userId).getFriendsList());
        return inMemoryUserStorage.getUserFriendsInList(userId);
    }

    public List<User> getCommonFriendListTwoUsers(Long userId,
                                                 Long otherId,
                                                 InMemoryUserStorage inMemoryUserStorage) {
        log.info("Начало работы метода по поиску общих друзей: пользователь с id = {} и другой " +
                "пользователь с id = {}", userId, otherId);
        log.debug("Получение пользователя из хранилища");
        User user = inMemoryUserStorage.getUserByIdFromStorage(userId);
        log.debug("Получение другого пользователя из хранилища");
        User otherUser = inMemoryUserStorage.getUserByIdFromStorage(otherId);
        log.info("Возвращение списка общих друзей пользователей");
        Set<Long> idsCommonFriends = user.getFriendsList()
                .stream()
                .filter(id -> otherUser.getFriendsList().contains(id))
                .collect(Collectors.toSet());
        return inMemoryUserStorage.getUserListByListIds(idsCommonFriends);

    }
}
