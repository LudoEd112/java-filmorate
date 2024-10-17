package ru.yandex.practicum.filmorate.mappers.film;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Component
public class RatedFilmsMapper implements RowMapper<List<Film>> {
    @Override
    public List<Film> mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<Film> list = new ArrayList<>();
        do {
            Film film = new Film();
            film.setId(resultSet.getLong("id"));
            film.setName(resultSet.getString("name"));
            film.setDescription(resultSet.getString("description"));
            film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
            film.setDuration(Duration.ofSeconds(resultSet.getLong("duration_minutes")));
            film.setMpa(new Mpa(resultSet.getLong("rating_id"), resultSet.getString("r_name")));

            String genresString = resultSet.getString("genres");
            if (!StringUtils.isBlank(genresString)) {
                Set<Genre> genres = Arrays.stream(genresString.split(","))
                        .map(genre -> genre.split("/"))
                        .map(parts -> new Genre(Long.parseLong(parts[0]), parts[1]))
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                film.setGenres(genres);
            } else {
                film.setGenres(Set.of());
            }

            String likesString = resultSet.getString("likes");
            if (!StringUtils.isBlank(likesString)) {
                Set<Long> likes = Arrays.stream(likesString.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toSet());
                film.setLikes(likes);
            } else {
                film.setLikes(Set.of());
            }

            list.add(film);
        } while (resultSet.next());
        return list;
    }
}
