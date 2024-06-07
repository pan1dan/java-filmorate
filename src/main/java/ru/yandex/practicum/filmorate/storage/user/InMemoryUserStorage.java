package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();
    ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public List<User> getAllUsersFromStorage() {
        log.debug("Возврат списка всех пользователей: {}", users.values());
        return users.values().stream().toList();
    }

    @Override
    public User addNewUserInStorage(User user) {
        if (user == null) {
            log.warn("Получено пустое тело запроса");
            throw new ValidationException("Получено пустое тело запроса");
        }
        log.debug("Получен объект: {}", user);
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Передано пустое поле с почтой");
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("Передана почта без знака @");
            throw new ValidationException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Передано пустое поле с логином");
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("Передан логин с пробелами");
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday() == null) {
            log.warn("Дата не была передана");
            throw new ValidationException("Поле с датой рождения должно быть заполнено");
        }
        if (user.getBirthday().isAfter(LocalDate.ofInstant(Instant.now(), zoneId))) {
            log.warn("Передана некорректная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Присвоение имени пользвателя значение поля логин");
            user.setName(user.getLogin());
        }
        log.debug("Присвоение id новому пользователю");
        user.setId(getNextId());
        log.info("Добавлен новый пользователь");
        user.setFriendsList(new HashSet<>());
        users.put(user.getId(), user);
        log.debug("Возврат пользователя в теле ответа: {}", users.get(user.getId()));
        return users.get(user.getId());
    }

    @Override
    public User updateUserInStorage(User newUser) {
        if (newUser == null) {
            log.warn("Получено пустое запроса");
            throw new ValidationException("Получено пустое запроса");
        }
        log.debug("Получен объект: {}", newUser);
        if (newUser.getId() == null) {
            log.warn("В запросе на изменение не был передан id");
            throw new ValidationException("Поле id не может быть пустым");
        }
        log.trace("Начата проверка на наличие пользователя в списке пользователей по id = {}", newUser.getId());
        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() != null && newUser.getEmail().contains("@")) {
                log.debug("Обновление почты пользователья с id = {}", newUser.getId());
                users.get(newUser.getId()).setEmail(newUser.getEmail());
            }
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank() && !newUser.getLogin().contains(" ")) {
                log.debug("Обновление логина пользователья с id = {}", newUser.getId());
                users.get(newUser.getId()).setLogin(newUser.getLogin());
            }
            if (newUser.getBirthday() != null
                    && newUser.getBirthday().isBefore(LocalDate.ofInstant(Instant.now(), zoneId))) {
                log.debug("Обновление дня рождения пользователья с id = {}", newUser.getId());
                users.get(newUser.getId()).setBirthday(newUser.getBirthday());
            }
            if (newUser.getName() != null && !newUser.getName().isBlank()) {
                log.debug("Обновление имени пользователья с id = {} на другое имя", newUser.getId());
                users.get(newUser.getId()).setName(newUser.getName());
            }
            log.info("Данные пользователя обновлены. Возвращён объект: {}", users.get(newUser.getId()));
            return users.get(newUser.getId());
        }
        log.warn("Пользователь с переданным id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
    }

    public User getUserByIdFromStorage(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с переданным id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return users.get(userId);
    }

    public List<User> getUserFriendsInList(Long userId) {
        List<User> userFriendsInList = new ArrayList<>();
        for (Long id : users.get(userId).getFriendsList()) {
            userFriendsInList.add(users.get(id));
        }
        return userFriendsInList;
    }

    public List<User> getUserListByListIds(Set<Long> ids) {
        List<User> userList = new ArrayList<>();
        for (Long id : ids) {
            userList.add(users.get(id));
        }
        return userList;
    }

    private Long getNextId() {
        log.info("Запуск метода определения нового id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.info("Возвращение нового уникального id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}
