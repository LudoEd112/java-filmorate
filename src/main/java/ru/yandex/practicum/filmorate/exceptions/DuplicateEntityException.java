package ru.yandex.practicum.filmorate.exceptions;

public class DuplicateEntityException extends Throwable {
    public DuplicateEntityException(String message) {
        super(message);
    }
}
