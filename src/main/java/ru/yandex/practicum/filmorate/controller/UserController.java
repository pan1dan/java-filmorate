package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("GET /users");
        return userService.getAllUsers();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@RequestBody User user) {
        log.info("POST /users");
        return userService.addNewUser(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User newUser) {
        log.info("PUT /users");
        return userService.updateUser(newUser);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}", userId);
        return userService.getUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addNewFriend(@PathVariable(name = "id") Long userId,
                             @PathVariable(name = "friendId") Long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);
        userService.addNewFriendIdToUserFriendList(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable(name = "id") Long userId,
                                  @PathVariable(name = "friendId") Long friendId) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        userService.deleteFriendIdFromUserFriendList(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getListFriends(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}/friends", userId);
        return userService.getUserListFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriendsList(@PathVariable(name = "id") Long userId,
                                           @PathVariable(name = "otherId") Long otherId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherId);
        return userService.getCommonFriendListTwoUsers(userId, otherId);
    }
}


