package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserEvent;
import ru.yandex.practicum.filmorate.service.UserServiceJdbc;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserServiceJdbc userServiceJdbc;

    @Autowired
    public UserController(UserServiceJdbc userServiceJdbc) {
        this.userServiceJdbc = userServiceJdbc;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("GET /users");
        List<User> allUsers = userServiceJdbc.getAllUsers();
        log.info("GET /users возвращает значение: {}", allUsers);
        return allUsers;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@RequestBody User user) {
        log.info("POST /users");
        User addedUser = userServiceJdbc.addNewUser(user);
        log.info("POST /users возвращает значение: {}", addedUser);
        return addedUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User newUser) {
        log.info("PUT /users");
        User updateUser = userServiceJdbc.updateUser(newUser);
        log.info("PUT /users возвращает значение: {}", updateUser);
        return updateUser;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}", userId);
        User user = userServiceJdbc.getUserById(userId);
        log.info("GET /users/{} возвращает значение: {}", userId, user);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addNewFriend(@PathVariable(name = "id") Long userId,
                             @PathVariable(name = "friendId") Long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);
        userServiceJdbc.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable(name = "id") Long userId,
                                  @PathVariable(name = "friendId") Long friendId) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        userServiceJdbc.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getListFriends(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}/friends", userId);
        List<User> listFriends = userServiceJdbc.getFriends(userId);
        log.info("GET /users/{}/friends возвращает значение: {}", userId, listFriends);
        return listFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriendsList(@PathVariable(name = "id") Long userId,
                                           @PathVariable(name = "otherId") Long otherId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherId);
        List<User> commonFriendsList = userServiceJdbc.getCommonFriends(userId, otherId);
        log.info("GET /users/{}/friends/common/{} возвращает значение: {}", userId, otherId, commonFriendsList);
        return commonFriendsList;
    }

    @GetMapping("/{id}/feed")
    public List<UserEvent> getUserFeed(@PathVariable long id) {
        log.info("try to GET/users/{}/feed started", id);
        List<UserEvent> events = userServiceJdbc.getEvents(id);
        log.info("finish to GET/users/{}/feed. Got {} events", id, events.size());
        return events;
    }
}


