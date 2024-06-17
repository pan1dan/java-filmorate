# java-filmorate
Template repository for Filmorate project.

![ER-диаграмма базы данных проекта](https://github.com/pan1dan/java-filmorate/assets/146234042/2a8b02c0-393f-437a-ad8a-18028a20033d)

**Примеры запросов**

Добавление пользователя

*INSERT INTO user (email, login, name, birthday) VALUES ('mail@mail.ru', 'login', 'Name Name', '2024-09-06');*

Добавление фильма

*INSERT INTO film (name, description, release_date, duration, MPA_id) VALUES ('film', 'good film', '2024-09-06', '120', '3');*
