package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.serializers.DurationToSecondsSerializer;
import ru.yandex.practicum.filmorate.validation.CinemasBirthday;
import ru.yandex.practicum.filmorate.validation.PositiveDuration;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

/**
 * Film.
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200)
    private String description;
    @CinemasBirthday
    private LocalDate releaseDate;
    @JsonSerialize(using = DurationToSecondsSerializer.class)
    @PositiveDuration(message = "Продолжительность фильма не может быть отрицательной")
    private Duration duration;
    private Set<Long> likes = new HashSet<>();
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
}