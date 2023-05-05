package ru.selivanov.springproject.diplomaProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.UserService;

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

        if (userService.findByUsername(user.getUsername()).isPresent())
            errors.rejectValue("username", "Already exists", "Человек с таким именем пользователя уже существует!");

        if (userService.findByEmail(user.getEmail()).isPresent())
            errors.rejectValue("email", "Already exists", "Человек с таким адресом электронной почты уже существует!");
    }
}
