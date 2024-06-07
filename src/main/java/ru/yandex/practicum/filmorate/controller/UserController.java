package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.Storage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    Storage storage = new Storage(new InMemoryUserStorage(), new InMemoryFilmStorage());
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("GET /users");

        return storage.getInMemoryUserStorage().getAllUsersFromStorage();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@RequestBody User user) {
        log.info("POST /users");
        return storage.getInMemoryUserStorage().addNewUserInStorage(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User newUser) {
        log.info("PUT /users");
        return storage.getInMemoryUserStorage().updateUserInStorage(newUser);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User getUserById(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}", userId);
        return storage.getInMemoryUserStorage().getUserByIdFromStorage(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> addNewFriend(@PathVariable(name = "id") Long userId,
                             @PathVariable(name = "friendId") Long friendId) {
        log.info("PUT /users/{}/friends/{}", userId, friendId);
        return userService.addNewFriendIdToUserFriendList(userId, friendId, storage.getInMemoryUserStorage());
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<Long> deleteFriend(@PathVariable(name = "id") Long userId,
                                  @PathVariable(name = "friendId") Long friendId) {
        log.info("DELETE /users/{}/friends/{}", userId, friendId);
        return userService.deleteFriendIdFromUserFriendList(userId, friendId, storage.getInMemoryUserStorage());
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getListFriends(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}/friends", userId);
        return userService.getUserListFriends(userId, storage.getInMemoryUserStorage());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriendsList(@PathVariable(name = "id") Long userId,
                                           @PathVariable(name = "otherId") Long otherId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherId);
        return userService.getCommonFriendListTwoUsers(userId, otherId, storage.getInMemoryUserStorage());
    }
}


