package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserFriendsStorage {
    void addUserFriend(long user1Id, long user2Id);

    void deleteUserFriends(long user1Id, long user2Id);

    List<User> getUserFriends(long userId);
}
