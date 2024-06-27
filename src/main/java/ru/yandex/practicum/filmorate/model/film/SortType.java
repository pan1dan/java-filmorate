package ru.yandex.practicum.filmorate.model.film;

public enum SortType {
    YEAR,
    LIKES;
    public static SortType fromString(String sortField) {
        try {
            return SortType.valueOf(sortField.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort field: " + sortField, e);
        }
    }
}
