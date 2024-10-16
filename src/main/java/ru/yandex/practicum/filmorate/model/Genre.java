package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Genre {
    Long id;
    String name;
}
