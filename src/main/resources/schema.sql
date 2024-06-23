CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(100),
    birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    release_date DATE,
    duration INTEGER,
    mpa_id INTEGER
);

CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER NOT NULL PRIMARY KEY,
    genre_name varchar
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT NOT NULL REFERENCES films(film_id) ON DELETE CASCADE,
    genre_id INTEGER NOT NULL REFERENCES genres(genre_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS film_rating_mpa (
    mpa_id INTEGER NOT NULL PRIMARY KEY,
    mpa_name varchar
);

CREATE TABLE IF NOT EXISTS users_likes_films (
    film_id BIGINT,
    user_id BIGINT,
    FOREIGN KEY (film_id) REFERENCES films(film_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    status ENUM('UNCONFIRMED', 'CONFIRMED'),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS usabilitys (
    usability_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    usability VARCHAR(15) UNIQUE NOT NULL,
    weigh INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    content VARCHAR(500),
    is_positive BOOLEAN
);

CREATE TABLE IF NOT EXISTS usability_reviews (
    usability_review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    review_id BIGINT NOT NULL REFERENCES reviews (review_id) ON DELETE CASCADE,
    usability_id INT REFERENCES usabilitys (usability_id) ON DELETE CASCADE,
    UNIQUE (review_id, user_id)
);
