package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private long reviewId;

    @NotNull
    private Long filmId;

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 500)
    private String content;

    @NotNull
    private Boolean isPositive;
    private int useful;
}
