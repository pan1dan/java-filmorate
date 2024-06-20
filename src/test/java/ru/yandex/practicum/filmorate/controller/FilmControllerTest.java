package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemory.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;

    @BeforeEach
    void newFilmController() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), null, null, null));
    }

    @Test
    void getAllFilms_withoutFilms() {
        assertEquals(0, filmController.getAllFilms().size());
    }

    @Test
    void getAllFilms_addNewFilm_withOneFilm() {
        Film film = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        filmController.addNewFilm(film);
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void getAllFilms_addNewFilm_withTwoFilms() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        Film film2 = Film.builder()
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .build();

        filmController.addNewFilm(film1);
        filmController.addNewFilm(film2);
        assertEquals(2, filmController.getAllFilms().size());
    }

    @Test
    void addNewFilm_ShouldReturnFilm() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        filmController.addNewFilm(film1);
        assertEquals(film1, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void addNewFilm_withoutName() {
        Film film1 = Film.builder()
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void addNewFilm_withoutDescription() {
        Film film1 = Film.builder()
                .name("asd")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        filmController.addNewFilm(film1);
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    void addNewFilm_withDescriptionWithExcessiveSymbolNumbers() {
        Film film1 = Film.builder()
                .name("abc")
                .description("1".repeat(201))
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void addNewFilm_withoutReleaseDate() {
        Film film1 = Film.builder()
                .name("213")
                .description("Blablabla")
                .duration(300)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void addNewFilm_withWrongReleaseDate() {
        Film film1 = Film.builder()
                .name("213")
                .description("Blablabla")
                .releaseDate(LocalDate.of(777, 7, 7))
                .duration(300)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void addNewFilm_withoutDuration() {
        Film film1 = Film.builder()
                .name("213")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void addNewFilm_withWrongDuration() {
        Film film1 = Film.builder()
                .name("213")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(-100)
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.addNewFilm(film1));
    }

    @Test
    void updateFilm() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();

        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithoutNewName() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setName("asd");
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithWrongNewName() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setName("asd");
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithoutNewDescription() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setDescription("Blablabla");
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WitWrongNewDescription() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("1".repeat(201))
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setDescription("Blablabla");
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithoutNewReleaseDate() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setReleaseDate(LocalDate.of(2000, 12, 12));
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithWrongNewReleaseDate() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setReleaseDate(LocalDate.of(2000, 12, 12));
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithoutDuration() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setDuration(300);
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithWrongDuration() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        Long id = Long.parseLong("1");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(-100)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        filmController.updateFilm(film2);
        film2.setDuration(300);
        assertEquals(film2, filmController.getAllFilms().toArray()[0]);
    }

    @Test
    void updateFilm_WithWrongID() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        Long id = Long.parseLong("123");
        Film film2 = Film.builder()
                .id(id)
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        assertThrowsExactly(NotFoundException.class, () -> filmController.updateFilm(film2));
    }

    @Test
    void updateFilm_WithoutID() {
        Film film1 = Film.builder()
                .name("asd")
                .description("Blablabla")
                .releaseDate(LocalDate.of(2000, 12, 12))
                .duration(300)
                .userLikesFilms(new HashSet<>())
                .build();
        filmController.addNewFilm(film1);
        Film film2 = Film.builder()
                .name("qwe")
                .description("bybyby")
                .releaseDate(LocalDate.of(7777, 7, 7))
                .duration(777)
                .userLikesFilms(new HashSet<>())
                .build();
        assertThrowsExactly(ValidationException.class, () -> filmController.updateFilm(film2));
    }

}