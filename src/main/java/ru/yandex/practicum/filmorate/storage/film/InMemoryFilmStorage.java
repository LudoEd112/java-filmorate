package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();
    private long seq = 0;

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        log.info("Получение фильма с ID: {}", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Создание фильма с ID: {}", film);
        film.setId(generateNextId());
        films.put(film.getId(), film);
        log.debug("Фильм создан: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Film oldFilm = getFilmById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + film.getId() + " не найден"));
        log.debug("Обновление фильма: {}", oldFilm);
        if (film.getName() != null) oldFilm.setName(film.getName());
        if (film.getDescription() != null) oldFilm.setDescription(film.getDescription());
        if (film.getReleaseDate() != null) oldFilm.setReleaseDate(film.getReleaseDate());
        if (oldFilm.getDuration() != null) oldFilm.setDuration(film.getDuration());
        log.debug("Фильм обновлен: {}", film);
        return oldFilm;
    }

    @Override
    public void addLike(Film film, User user) {
        log.debug("Добавление лайка на фильм: {}", film);
        Set<Long> likes = filmLikes.computeIfAbsent(film.getId(), k -> new HashSet<>());
        likes.add(user.getId());
        log.debug("Лайк добавлен на фильм: {}", film);
    }

    @Override
    public void removeLike(Film film, User user) {
        log.info("Удаление лайка с фильма: {}", film);
        Set<Long> likes = filmLikes.computeIfAbsent(film.getId(), k -> new HashSet<>());
        likes.remove(user.getId());
        log.debug("Лайк удален с фильма: {}", film);
    }

    public Collection<Film> getPopularFilms(int count) {
        log.debug("Получение популярных фильмов");
        return filmLikes.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getValue().size(), Comparator.reverseOrder()))
                .limit(count)
                .map(entry -> films.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    private Long generateNextId() {
        return ++seq;
    }
}