package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.user.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.model.user.UserFriends;
import ru.yandex.practicum.filmorate.storage.model.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("userDbStorage")
@Primary
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    ZoneId zoneId = ZoneId.of("Europe/Moscow");

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<User> getAllUsers() {
        try {
            String sql = "SELECT * " +
                    "FROM users";
            return jdbcTemplate.query(sql, this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех пользователей из БД", e);
            throw new NotFoundException("Ошибка при получении всех пользователей из БД");
        }
    }

    @Override
    public User create(User user) {
        userValidation(user);
        try {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("user_id");

            long userId = (long) insert.executeAndReturnKey(new MapSqlParameterSource("name", user.getName())
                    .addValue("email", user.getEmail())
                    .addValue("login", user.getLogin())
                    .addValue("birthday", user.getBirthday()));

            user.setId(userId);

            if (user.getUserFriends() != null && !user.getUserFriends().getFriendsIds().isEmpty()) {
                for (Long id : user.getUserFriends().getFriendsIds()) {
                    jdbcTemplate.update("INSERT INTO user_friends(user_id, status, friend_id) " +
                            "VALUES (?, ?, ?)",
                            user.getId(),
                            id,
                            user.getUserFriends().getFriendsStatusList().get(id));
                }
            }

            if (user.getLikesFilms() != null && !user.getLikesFilms().isEmpty()) {
                for (UserLikesFilms userLikesFilms : user.getLikesFilms()) {
                    jdbcTemplate.update("INSERT INTO users_likes_films(film_id, user_id)" +
                                    "VALUES(?, ?)",
                            userLikesFilms.getFilmId(),
                            userLikesFilms.getUserId());
                }
            }
        } catch (ValidationException e) {
            log.warn("Ошибка при добавлении пользователя в БД: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Ошибка при добавлении пользователя в БД", e);
            throw new RuntimeException("Ошибка при добавлении пользователя в БД", e);
        }

        return user;
    }

    @Override
    public User update(User newUser) {
        userValidation(newUser);
        try {
            String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
            int rowsUpdated = jdbcTemplate.update(sql, newUser.getEmail(),
                    newUser.getLogin(),
                    newUser.getName(),
                    newUser.getBirthday(),
                    newUser.getId());

            if (rowsUpdated == 0) {
                log.warn("Пользователь с id " + newUser.getId() + " не найден");
                throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден");
            }
        } catch (Exception e) {
            log.warn("Ошибка при обновлении пользователя в БД", e);
            throw new NotFoundException("Ошибка при обновлении пользователя в БД");
        }

        return newUser;
    }

    @Override
    public User getUserById(Long userId) {
        try {
            String sql = "SELECT * " +
                    "FROM users " +
                    "WHERE user_id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRow, userId);
        } catch (Exception e) {
            log.warn("Ошибка при получении пользователя по id из БД", e);
            throw new NotFoundException("Ошибка при получении пользователя по id из БД");
        }
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        String sql1 = "SELECT film_id " +
                "FROM users_likes_films " +
                "WHERE user_id = " + rs.getLong("user_id");
        List<Long> filmIds = jdbcTemplate.query(sql1,
                (resultSet, rowNumber) -> {
                    return resultSet.getLong("film_id");
                });
        Set<UserLikesFilms> userLikesFilms = filmIds.stream()
                .map(id -> {
                    try {
                        return new UserLikesFilms(rs.getLong("user_id"), (long)id);
                    } catch (SQLException e) {
                        log.warn("Ошибка при создании объекта UserLikesFilms", e);
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        user.setLikesFilms(userLikesFilms);

        String sql2 = "SELECT * " +
                "FROM user_friends " +
                "WHERE user_id = " + rs.getLong("user_id");
        HashMap<Long, FriendshipStatus> friendsStatusList = new HashMap<>();
        List<Long> friendsIds = jdbcTemplate.query(sql2,
                (resultSet, rowNumber) -> {
                    friendsStatusList.put(resultSet.getLong("friend_id"),
                                          FriendshipStatus.valueOf(resultSet.getString("status")));
                    return resultSet.getLong("friend_id");
                });

        user.setUserFriends(new UserFriends(user.getId(), new HashSet<>(friendsIds), friendsStatusList));

        return user;
    }

    private User userValidation(User user) {
        log.debug("Начало валидации пользователя");
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
        return user;
    }
}
