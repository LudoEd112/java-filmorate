package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.validation.CinemasBirthday;

import java.time.Duration;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UpdateFilmDto {
    private Long id;
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;
    @CinemasBirthday
    private LocalDate releaseDate;
    @JsonDeserialize(using = DurationDeserializer.class)
    @JsonSerialize(using = DurationSerializer.class)
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private Duration duration;
    private Set<GenreDto> genres = new LinkedHashSet<>();
    private MpaDto mpa;

    public boolean hasName() {
        return StringUtils.isNotBlank(this.name);
    }

    public boolean hasDescription() {
        return StringUtils.isNotBlank(this.description);
    }

    public boolean hasReleaseDate() {
        return this.releaseDate != null;
    }

    public boolean hasDuration() {
        return this.duration != null;
    }

    public boolean hasMpa() {
        return this.mpa != null;
    }

}
