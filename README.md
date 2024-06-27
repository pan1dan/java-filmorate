# java-filmorate
Template repository for Filmorate project.

![ER-диаграмма базы данных проекта](/filmorateERDiagram.png)

**Примеры запросов**

Добавление пользователя

*INSERT INTO user (email, login, name, birthday) VALUES ('mail@mail.ru', 'login', 'Name Name', '2024-09-06');*

Добавление фильма

*INSERT INTO film (name, description, release_date, duration, MPA_id) VALUES ('film', 'good film', '2024-09-06', '120', '3');*
