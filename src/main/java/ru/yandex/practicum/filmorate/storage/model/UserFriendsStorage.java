package ru.yandex.practicum.filmorate.storage.model;

import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;

public interface UserFriendsStorage {
    void addFriend(long user1Id, long user2Id);

    void deleteFriend(long user1Id, long user2Id);

    List<User> getFriends(long userId);
}
