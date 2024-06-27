package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.enums.SearchType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.UserLikesFilms;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.model.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmRatingMpaStorage;
import ru.yandex.practicum.filmorate.storage.model.FilmStorage;
import ru.yandex.practicum.filmorate.storage.model.GenresStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("filmDbStorage")
@Primary
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final LocalDate BIRTHDAY_OF_THE_MOVIE = LocalDate.of(1895, 12, 28);
    private final GenresStorage genresStorage;
    private final FilmRatingMpaStorage filmRatingMpaStorage;
    private final DirectorStorage directorStorage;

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
    public void deleteFilmById(long filmId) {
        try {
            String deleteFilmSql = "DELETE FROM films WHERE film_id = ?";
            int rowsDeleted = jdbcTemplate.update(deleteFilmSql, filmId);
            if (rowsDeleted == 0) {
                log.warn("Фильм с id " + filmId + " не найден");
                throw new NotFoundException("Фильм с id " + filmId + " не найден");
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении фильма с id " + filmId, e);
            throw new RuntimeException("Ошибка при удалении фильма с id " + filmId, e);
        }
    }

    @Override
    public List<Film> getAllFilms() {
        try {
            return jdbcTemplate.query("SELECT * FROM films", new FilmRowMapper(jdbcTemplate,
                                                                                   genresStorage,
                                                                                   filmRatingMpaStorage,
                                                                                   directorStorage));
        } catch (Exception e) {
            log.warn("Ошибка при получении всех фильмов из БД");
            throw new NotFoundException("Ошибка при получении всех фильмов из БД");
        }
    }

    @Override
    public Film create(Film film) {
        filmValidation(film);
        try {
            SimpleJdbcInsert insertNewFilmSql = new SimpleJdbcInsert(jdbcTemplate)
                    .withSchemaName("public")
                    .withTableName("films")
                    .usingGeneratedKeyColumns("film_id");
            long filmId = (long) insertNewFilmSql.executeAndReturnKey(
                    new MapSqlParameterSource("name", film.getName())
                            .addValue("description", film.getDescription())
                            .addValue("release_date", film.getReleaseDate())
                            .addValue("duration", film.getDuration())
                            .addValue("mpa_id", film.getMpa().getId()));
            film.setId(filmId);

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                SimpleJdbcInsert insertFilmGenreSql = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_genre")
                        .usingColumns("film_id", "genre_id");

                for (Genre genre : film.getGenres()) {
                    insertFilmGenreSql.execute(new MapSqlParameterSource("film_id", filmId)
                            .addValue("genre_id", genre.getId()));
                }

                Set<Genre> sortedGenres = film.getGenres()
                        .stream()
                        .sorted((genre1, genre2) -> Integer.compare(genre1.getId(), genre2.getId()))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                film.setGenres(sortedGenres);
            }

            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                SimpleJdbcInsert insertFilmDirectorSql = new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("film_director")
                        .usingColumns("film_id", "director_id");

                for (Director director : film.getDirectors()) {
                    insertFilmDirectorSql.execute(new MapSqlParameterSource("film_id", filmId)
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
    public Film update(Film newFilm) {
        filmValidation(newFilm);
        try {
            String updateFilmSql = "UPDATE films SET name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "mpa_id = ? " +
                    "WHERE film_id = ?";
            int rowsUpdated = jdbcTemplate.update(updateFilmSql, newFilm.getName(),
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
                List<Genre> sortedGenres = newFilm.getGenres()
                        .stream()
                        .sorted(Comparator.comparingInt(Genre::getId))
                        .toList();
                newFilm.setGenres(new LinkedHashSet<>(sortedGenres));
            } else {
                newFilm.setGenres(new HashSet<>());
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
    public Film getFilmById(Long filmId) {
        try {
            String getFilmByIdSqlObj = "SELECT * " +
                    "FROM films " +
                    "WHERE film_id = ?";
            return jdbcTemplate.queryForObject(getFilmByIdSqlObj,
                                               new FilmRowMapper(jdbcTemplate,
                                                                 genresStorage,
                                                                 filmRatingMpaStorage,
                                                                 directorStorage),
                                               filmId);
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

    private static final String FILMS_SEARCH_BY_TITLE = """
            SELECT
                f.film_id AS film_id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                r.mpa_id AS mpa_id
            FROM films AS f
            LEFT JOIN users_likes_films AS l ON l.film_id = f.film_id
            LEFT JOIN film_rating_mpa AS r ON  f.mpa_id = r.mpa_id
            WHERE LOWER(f.name) LIKE LOWER('%' || ? || '%')
            GROUP BY f.name, f.film_id
            ORDER BY film_id DESC;
            """;

    private static final String FILMS_SEARCH_BY_DIRECTOR = """
            SELECT
                f.film_id AS film_id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                r.mpa_id AS mpa_id
            FROM films AS f
            LEFT JOIN users_likes_films AS l ON l.film_id = f.film_id
            LEFT JOIN film_rating_mpa AS r ON  f.mpa_id = r.mpa_id
            LEFT JOIN film_director AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            WHERE LOWER(d.director_name) LIKE LOWER('%' || ? || '%')
            GROUP BY f.name, f.film_id
            ORDER BY film_id DESC;
            """;

    private static final String FILMS_SEARCH_BY_TITLE_AND_DIRECTOR = """
            SELECT
                f.film_id AS film_id,
                f.name AS name,
                f.description AS description,
                f.release_date AS release_date,
                f.duration AS duration,
                r.mpa_id AS mpa_id
            FROM films AS f
            LEFT JOIN users_likes_films AS l ON l.film_id = f.film_id
            LEFT JOIN film_rating_mpa AS r ON  f.mpa_id = r.mpa_id
            LEFT JOIN film_director AS fd ON f.film_id = fd.film_id
            LEFT JOIN directors AS d ON fd.director_id = d.director_id
            WHERE LOWER(d.director_name) LIKE LOWER('%' || ? || '%')
                OR LOWER(f.name) LIKE LOWER('%' || ? || '%')
            GROUP BY f.name, f.film_id
            ORDER BY film_id DESC;
            """;

    public List<Film> getSearchFilms(String query, SearchType searchType) {
        log.info("Получение фильмов по запросу = {}", query);
        switch (searchType) {
            case TITLE_AND_DIRECTOR -> {
                return jdbcTemplate.query(FILMS_SEARCH_BY_TITLE_AND_DIRECTOR,
                                          new FilmRowMapper(jdbcTemplate,
                                                            genresStorage,
                                                            filmRatingMpaStorage,
                                                            directorStorage),
                                          query,
                                          query);
            }
            case DIRECTOR -> {
                return jdbcTemplate.query(FILMS_SEARCH_BY_DIRECTOR,
                                          new FilmRowMapper(jdbcTemplate,
                                                            genresStorage,
                                                            filmRatingMpaStorage,
                                                            directorStorage),
                                          query);
            }
            default -> {
                return jdbcTemplate.query(FILMS_SEARCH_BY_TITLE,
                                          new FilmRowMapper(jdbcTemplate,
                                                            genresStorage,
                                                            filmRatingMpaStorage,
                                                            directorStorage),
                                          query);
            }
        }
    }

    @Override
    public List<Film> getTopFilmsByLikes(Integer count, Integer genreId, Integer year) {
        try {
            String sql = "SELECT * FROM films WHERE " +
                    "(? IS NULL OR EXISTS (SELECT 1 FROM film_genre WHERE film_id = films.film_id AND genre_id = ?)) " +
                    "AND (? IS NULL OR EXTRACT(YEAR FROM release_date) = ?) " +
                    "ORDER BY (SELECT COUNT(*) FROM users_likes_films WHERE film_id = films.film_id) DESC " +
                    "LIMIT ?";
            return jdbcTemplate.query(sql, this::mapRow, genreId, genreId, year, year, count);
        } catch (Exception e) {
            log.warn("Ошибка при получении топа фильмов из БД", e);
            throw new NotFoundException("Ошибка при получении топа фильмов из БД");
        }
    }

}
