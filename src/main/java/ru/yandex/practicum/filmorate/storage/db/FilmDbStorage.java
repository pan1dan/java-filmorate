package ru.yandex.practicum.filmorate.storage.db;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmRatingMpaStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmStorage;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private static final LocalDate BIRTHDAY_OF_THE_MOVIE = LocalDate.of(1895, 12, 28);
    GenresStorage genresStorage;
    FilmRatingMpaStorage filmRatingMpaStorage;
    DirectorStorage directorStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("genresDbStorage") GenresStorage genresStorage,
                         @Qualifier("filmDbRatingMpaStorage") FilmRatingMpaStorage filmRatingMpaStorage,
                         @Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genresStorage = genresStorage;
        this.filmRatingMpaStorage = filmRatingMpaStorage;
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Film> getAllFilmsFromStorage() {
        try {
            return jdbcTemplate.query("SELECT * FROM films", this::mapRow);
        } catch (Exception e) {
            log.warn("Ошибка при получении всех фильмов из БД");
            throw new NotFoundException("Ошибка при получении всех фильмов из БД");
        }
    }

    @Override
    public Film addNewFilmToStorage(Film film) {
        filmValidation(film);
        try {
            SimpleJdbcInsert insert1 = new SimpleJdbcInsert(jdbcTemplate)
                    .withSchemaName("public")
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            long filmId = (long) insert1.executeAndReturnKey(
                    new MapSqlParameterSource("name", film.getName())
                            .addValue("description", film.getDescription())
                            .addValue("release_date", film.getReleaseDate())
                            .addValue("duration", film.getDuration())
                            .addValue("mpa_id", film.getMpa().getId()));
            film.setId(filmId);

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                SimpleJdbcInsert insert2 = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_genre")
                        .usingColumns("film_id", "genre_id");

                for (Genre genre : film.getGenres()) {
                    insert2.execute(new MapSqlParameterSource("film_id", filmId)
                                                            .addValue("genre_id", genre.getId()));
                }
            }

            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                SimpleJdbcInsert insert3 = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_director")
                        .usingColumns("film_id", "director_id");

                for(Director director : film.getDirectors()) {
                    insert3.execute(new MapSqlParameterSource("film_id", filmId)
                                                            .addValue("director_id", director.getId()));
                }
            }

        } catch (Exception e) {
            log.warn("Ошибка при добавлении фильма в БД", e);
            throw new ValidationException("Ошибка при добавлении фильма в БД");
        }

        return film;
    }

    @Override
    public Film updateFilmInStorage(Film newFilm) {
        filmValidation(newFilm);
        try {
            String sql = "UPDATE films SET name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "mpa_id = ? " +
                    "WHERE film_id = ?";
            int rowsUpdated = jdbcTemplate.update(sql, newFilm.getName(),
                    newFilm.getDescription(),
                    newFilm.getReleaseDate(),
                    newFilm.getDuration(),
                    newFilm.getMpa().getId(),
                    newFilm.getId());
            if (rowsUpdated == 0) {
                log.warn("Фильм с id " + newFilm.getId() + " не найден");
                throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
            }

            jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ? ", newFilm.getId());
            if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
                for (Genre genre : newFilm.getGenres()) {
                    jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id)" +
                                    "VALUES(?, ?)",
                            newFilm.getId(),
                            genre.getId());
                }
            }

            jdbcTemplate.update("DELETE FROM users_likes_films WHERE film_id = ? ", newFilm.getId());
            if (newFilm.getUserLikesFilms() != null && !newFilm.getUserLikesFilms().isEmpty()) {
                for (UserLikesFilms userLikesFilms : newFilm.getUserLikesFilms()) {
                    jdbcTemplate.update("INSERT INTO users_likes_films(film_id, user_id)" +
                                    "VALUES(?, ?)",
                                    userLikesFilms.getFilmId(),
                                    userLikesFilms.getUserId());
                }
            }

            jdbcTemplate.update("DELETE FROM film_director WHERE film_id = ?", newFilm.getId());
            if (newFilm.getDirectors() != null && !newFilm.getDirectors().isEmpty()) {
                for (Director director : newFilm.getDirectors()) {
                    if (director.getName() == null || director.getName().isBlank()) {
                        director.setName(directorStorage.getDirectorById(director.getId()).getName());
                    }
                    jdbcTemplate.update("INSERT INTO film_director(film_id, director_id)" +
                            "VALUES(?, ?)",
                            newFilm.getId(),
                            director.getId());
                }
            }

        } catch (NotFoundException e) {
            log.warn("Фильм с id " + newFilm.getId() + " не найден");
            throw new NotFoundException("Фильм с id " + newFilm.getId() + " не найден");
        } catch (Exception e) {
            log.warn("Ошибка при обновлении фильма в БД", e);
            throw new RuntimeException("Ошибка при обновлении фильма в БД", e);
        }
        return newFilm;
    }

    @Override
    public Film getFilmByIdFromStorage(Long filmId) {
        try {
            String sql = "SELECT * " +
                    "FROM films " +
                    "WHERE film_id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRow, filmId);
        } catch (Exception e) {
            log.warn("Ошибка при получении фильма по id из БД", e);
            throw new NotFoundException("Ошибка при получении фильма по id из БД");
        }
    }

    private Film mapRow(ResultSet rs, int rowNum) throws SQLException {
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

    private void filmValidation(Film film) {
        if (film == null) {
            log.warn("Получено пустое тело запроса");
            throw new ValidationException("Получено пустое тело запроса");
        }
        log.debug("Получен объект: {}", film);
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("В запросе отсутствует название фильма");
            throw new ValidationException("Название фильма должно быть обязательно");
        }
        if (film.getDescription() == null) {
            film.setDescription("");
        }
        if (film.getDescription().length() > 200) {
            log.warn("В запросе передана слишком большая строка");
            throw new ValidationException("Длина описания должна быть меньше 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.warn("В запросе нет даты создания фильма");
            throw new ValidationException("В запросе нет даты создания фильма");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_OF_THE_MOVIE)) {
            log.warn("В запросе передана невозможная дата");
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            log.warn("В запросе передана невозможная длительность фильма");
            throw new ValidationException("Длительность фильма должна быть положительной");
        }

        try {
            filmRatingMpaStorage.getMpaById(film.getMpa().getId());
        } catch (NotFoundException e) {
            throw new ValidationException("Добавление фильма с несуществующим рейтингом mpa");
        }
    }

}
