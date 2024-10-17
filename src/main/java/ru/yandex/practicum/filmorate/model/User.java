package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.NotBlankLogin;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    private Long id;
    @Email
    @NotNull(message = "email не может быть пустой или не содержать @.")
    private String email;
    @NotBlankLogin
    @NotBlank
    private String login;
    private String name;
    @Past(message = "Дата рождения введена не корректно")
    private LocalDate birthday;
    private Set<Long> friends = new LinkedHashSet<>();

    public void setEmptyName() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }
}