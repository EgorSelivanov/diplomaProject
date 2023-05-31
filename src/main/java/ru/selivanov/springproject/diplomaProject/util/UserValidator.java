package ru.selivanov.springproject.diplomaProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.UserService;

import java.util.Objects;
import java.util.Optional;

@Component
public class UserValidator implements Validator {
    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        Optional<User> userByUsername = userService.findByUsername(user.getUsername());
        Optional<User> userByEmail = userService.findByEmail(user.getEmail());

        if (userByUsername.isPresent() && !Objects.equals(userByUsername.get().getUserId(), user.getUserId())) {
            errors.rejectValue("username", "Already exists", "Человек с таким именем пользователя уже существует: " + user.getUsername());
        }
        if (userByEmail.isPresent() && !Objects.equals(userByEmail.get().getUserId(), user.getUserId())) {
            errors.rejectValue("email", "Already exists", "Человек с таким адресом электронной почты уже существует: " + user.getEmail());
        }
    }
}
