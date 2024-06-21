package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable(name = "id") Long userId) {
        log.info("DELETE /users/{}", userId);
        userService.deleteUserById(userId);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers() {
        log.info("GET /users");
        List<User> allUsers = userService.getAllUsers();
        log.info("GET /users возвращает значение: {}", allUsers);
        return allUsers;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNewUser(@RequestBody User user) {
        log.info("POST /users");
        User addedUser = userService.addNewUser(user);
        log.info("POST /users возвращает значение: {}", addedUser);
        return addedUser;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@RequestBody User newUser) {
        log.info("PUT /users");
        User updateUser = userService.updateUser(newUser);
        log.info("PUT /users возвращает значение: {}", updateUser);
        return updateUser;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable(name = "id") Long userId) {
        log.info("GET /users/{}", userId);
        User user = userService.getUserById(userId);
        log.info("GET /users/{} возвращает значение: {}", userId, user);
        return user;
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
        List<User> listFriends = userService.getUserListFriends(userId);
        log.info("GET /users/{}/friends возвращает значение: {}", userId, listFriends);
        return listFriends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriendsList(@PathVariable(name = "id") Long userId,
                                           @PathVariable(name = "otherId") Long otherId) {
        log.info("GET /users/{}/friends/common/{}", userId, otherId);
        List<User> commonFriendsList = userService.getCommonFriendListTwoUsers(userId, otherId);
        log.info("GET /users/{}/friends/common/{} возвращает значение: {}", userId, otherId, commonFriendsList);
        return commonFriendsList;
    }
}


