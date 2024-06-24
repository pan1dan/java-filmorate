package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLikesFilmsMapper implements RowMapper<UserLikesFilms> {
    public UserLikesFilms mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserLikesFilms.builder()
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }

}
