package ru.yandex.practicum.filmorate.exception;

public class InsertDbException extends RuntimeException {
    public InsertDbException(String message) {
        super(message);
    }
}
