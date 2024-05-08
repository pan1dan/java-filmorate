package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    Map<Long, User> users = new HashMap<>();
    ZoneId zoneId = ZoneId.of("Europe/Moscow");
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public Collection<User> getAllUsers() {
        log.debug("Возврат списка всех фильмов");
        return users.values();
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
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
        if (user.getBirthday().isAfter(LocalDate.ofInstant(Instant.now(), zoneId))) {
            log.warn("Передана некорректная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Присвоение имени пользвателя значение поля логин");
            user.setName(user.getLogin());
        }
        log.trace("Присвоение id новому пользователю");
        user.setId(getNextId());
        log.info("Добавлен новый пользователь");
        users.put(user.getId(), user);
        log.debug("Возврат пользователя в теле ответа");
        return users.get(user.getId());
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
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
            if (newUser.getLogin() != null && !newUser.getLogin().isBlank()) {
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
            } else {
                log.debug("Обновление имени пользователья с id = {} на значение поля логин", newUser.getId());
                users.get(newUser.getId()).setName(users.get(newUser.getId()).getLogin());
            }
            log.info("Данные пользователя обновлены");
            return users.get(newUser.getId());
        }
        log.warn("Пользователь с переданным id = {} не найден", newUser.getId());
        throw new ValidationException("Пользователь с id " + newUser.getId() + " не найден");
    }

    private Long getNextId() {
        log.trace("Определение текущего максимульного id");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.trace("Возвращение нового уникального id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }
}


