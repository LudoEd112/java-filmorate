package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService service;

    @GetMapping
    public Collection<Mpa> getMpa() {
        log.info("Запрос на получение списка рейтингов");
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable("id") Long mpaId) {
        return service.getMpa(mpaId);
    }


}
