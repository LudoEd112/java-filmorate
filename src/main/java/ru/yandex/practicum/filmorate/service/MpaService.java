package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateEntityException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage storage;

    public Mpa getMpaByFilm(Long filmId) {
        return storage.getMpa(filmId);
    }

    public Mpa getMpa(Long mpaId) {
        if (!storage.isMpaExistId(mpaId)) {
            throw new NotFoundException("Mpa с id %s не найден".formatted(mpaId));
        }
        return storage.getMpaById(mpaId);
    }

    public boolean addMpa(Mpa mpa, Long filmId) {
        if (!storage.isMpaExistId(mpa.getId())) {
            throw new IncorrectDataException("Mpa с id %s не сущетсвует".formatted(mpa.getId()));
        }
        return storage.addMpa(mpa, filmId);
    }

    public Collection<Mpa> findAll() {
        return storage.findAll().stream().toList();
    }

    public Mpa update(Mpa mpa) throws DuplicateEntityException {
        if (storage.isMpaExist(mpa.getName())) {
            throw new DuplicateEntityException("Рейтинг %s уже существует".formatted(mpa.getName()));
        }
        return storage.update(mpa);
    }

    public boolean isMpaExistName(Mpa mpa) {
        return storage.isMpaExist(mpa.getName());
    }

    public boolean isMpaExistId(Mpa mpa) {
        return storage.isMpaExistId(mpa.getId());
    }

    public boolean delete(Long id) {
        return storage.delete(id);
    }
}
