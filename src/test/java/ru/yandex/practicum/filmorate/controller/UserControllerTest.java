package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
class UserControllerTest {
    ZoneId zoneId = ZoneId.of("Europe/Moscow");
    UserController userController;

    @BeforeEach
    void newUserController() {
        userController = new UserController(new UserService(new InMemoryUserStorage(), null));
    }

    @Test
    void getAllUsers_withoutUser() {
        assertEquals(0, userController.getAllUsers().size());
    }

    @Test
    void getAllUsers_withOneUsers() {
        User user = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .build();
        userController.addNewUser(user);
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void getAllUsers_withTwoUsers() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        User user2 = User.builder()
                .email("qweqwe@.gmail")
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user2);
        assertEquals(2, userController.getAllUsers().size());
    }

    @Test
    void addNewUser_ShouldReturnUser() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        assertEquals(user1, userController.getAllUsers().toArray()[0]);
    }

    @Test
    void addNewUser_WithoutEmail() {
        User user1 = User.builder()
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void addNewUser_WithWrongEmail() {
        User user1 = User.builder()
                .email("2323.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void addNewUser_WithoutLogin() {
        User user1 = User.builder()
                .email("2323@.ru")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void addNewUser_WithWrongLogin() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("n d")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void addNewUser_WithoutName() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        User returnableUser = (User)userController.getAllUsers().toArray()[0];
        assertEquals(user1.getLogin(), returnableUser.getName());
    }

    @Test
    void addNewUser_WithoutBirthday() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void addNewUser_WithWrongBirthday() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.ofInstant(Instant.now(), zoneId).plusWeeks(1))
                .userFriends(new UserFriends())
                .build();
        assertThrowsExactly(ValidationException.class, () -> userController.addNewUser(user1));
    }

    @Test
    void updateUser() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .email("qweqwe@.gmail")
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithoutID() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        User user2 = User.builder()
                .email("qweqwe@.gmail")
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        assertThrowsExactly(ValidationException.class, () -> userController.updateUser(user2));
    }

    @Test
    void updateUser_WithWrongID() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("123");
        User user2 = User.builder()
                .id(id)
                .email("qweqwe@.gmail")
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        assertThrowsExactly(NotFoundException.class, () -> userController.updateUser(user2));
    }

    @Test
    void updateUser_WithoutEmail() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        assertEquals("2323@.ru", userController.getAllUsers().get(0).getEmail());
    }

    @Test
    void updateUser_WithWrongEmail() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .email("223fdffd.ru")
                .login("dfffd")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setEmail("2323@.ru");
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithoutLogin() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .email("567@fd.ru")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setLogin("nd");
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithWrongLogin() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .login("D  F")
                .email("567@fd.ru")
                .name("DFFFD")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setLogin("nd");
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithoutBirthday() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .login("DF")
                .email("567@fd.ru")
                .name("DFFFD")
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setBirthday(LocalDate.of(1999,12,19));
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithWrongBirthday() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .login("DF")
                .email("567@fd.ru")
                .name("DFFFD")
                .birthday(LocalDate.of(3000,12,19))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setBirthday(LocalDate.of(1999,12,19));
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithoutName() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .email("567@fd.ru")
                .login("DF")
                .birthday(LocalDate.of(2020,2,20))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setName("Na Da");
        assertEquals(user2, userController.getAllUsers().get(0));
    }

    @Test
    void updateUser_WithoutNameAndLogin() {
        User user1 = User.builder()
                .email("2323@.ru")
                .login("nd")
                .name("Na Da")
                .birthday(LocalDate.of(1999,12,19))
                .userFriends(new UserFriends())
                .build();
        Long id = Long.parseLong("1");
        User user2 = User.builder()
                .id(id)
                .email("567@fd.ru")
                .birthday(LocalDate.of(2020,2,20))
                .userFriends(new UserFriends())
                .build();
        userController.addNewUser(user1);
        userController.updateUser(user2);
        user2.setLogin("nd");
        user2.setName("Na Da");
        assertEquals(user2, userController.getAllUsers().get(0));
    }
}