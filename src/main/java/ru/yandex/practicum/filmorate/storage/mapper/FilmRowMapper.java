package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmRatingMpaStorage;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class FilmRowMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;
    private final GenresStorage genresStorage;
    private final FilmRatingMpaStorage filmRatingMpaStorage;
    private final DirectorStorage directorStorage;

    public FilmRowMapper(JdbcTemplate jdbcTemplate,
                         GenresStorage genresStorage,
                         FilmRatingMpaStorage filmRatingMpaStorage,
                         DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
        this.filmRatingMpaStorage = filmRatingMpaStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(filmRatingMpaStorage.getMpaById(rs.getInt("mpa_id")).getName());

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .build();

        String sql1 = "SELECT genre_id " +
                "FROM film_genre " +
                "WHERE film_id = " + rs.getLong("film_id") + " ORDER BY genre_id";
        List<Integer> genresIds = jdbcTemplate.query(sql1,
                (resultSet, rowNumber) -> {
                    return resultSet.getInt("genre_id");
                });
        Set<Genre> filmGenres = genresIds.stream()
                .map(id -> {
                    try {
                        return genresStorage.getGenreNameById(id);
                    } catch (Exception e) {
                        log.warn("Ошибка при получении жанров по их id", e);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());
        film.setGenres(filmGenres);

        String sql2 = "SELECT user_id " +
                "FROM users_likes_films " +
                "WHERE film_id = " + rs.getLong("film_id");
        List<Long> usersIds = jdbcTemplate.query(sql2,
                (resultSet, rowNumber) -> {
                    return resultSet.getLong("user_id");
                });
        Set<UserLikesFilms> userLikesFilms = usersIds.stream()
                .map(userId -> {
                    try {
                        return new UserLikesFilms(rs.getLong("film_id"), userId);
                    } catch (SQLException e) {
                        log.warn("Ошибка при создании объекта UserLikesFilms", e);
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());
        film.setUserLikesFilms(userLikesFilms);

        String sql3 = "SELECT director_id " +
                "FROM film_director " +
                "WHERE film_id = " + rs.getLong("film_id");
        List<Long> directorsIds = jdbcTemplate.query(sql3,
                (resultSet, rowNumber) -> {
                    return resultSet.getLong("director_id");
                });
        Set<Director> filmDirectors = directorsIds.stream()
                .map(id -> {
                    try {
                        return directorStorage.getDirectorById(id);
                    } catch (Exception e) {
                        log.warn("Ошибка при получении жанров по их id", e);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toSet());
        film.setDirectors(filmDirectors);

        return film;
    }
}
