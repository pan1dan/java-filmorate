# java-filmorate
Template repository for Filmorate project.

![ER-диаграмма базы данных проекта](https://github.com/pan1dan/java-filmorate/assets/146234042/5398f924-bf25-4607-b8ff-aac3cc533015)

**Примеры запросов**

Добавление пользователя

*INSERT INTO user (email, login, name, birthday) VALUES ('mail@mail.ru', 'login', 'Name Name', '2024-09-06');*

Добавление фильма

*INSERT INTO film (name, description, release_date, duration, MPA_id) VALUES ('film', 'good film', '2024-09-06', '120', '3');*
