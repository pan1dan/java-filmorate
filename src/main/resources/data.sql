MERGE INTO genres (genre_id, genre_name)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');

MERGE INTO film_rating_mpa (mpa_id, mpa_name)
    VALUES (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');

MERGE INTO usabilitys (usability_id, usability, weigh)
    VALUES (1, 'USEFUL', 1),
           (2, 'USELESS', -1);