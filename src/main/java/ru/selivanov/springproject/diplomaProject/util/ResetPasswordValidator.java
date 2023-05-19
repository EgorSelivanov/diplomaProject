package ru.selivanov.springproject.diplomaProject.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ResetPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return String.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        String password = (String) target;
        password = password.trim();

        if (password.length() < 6 || password.length() > 255)
            errors.rejectValue("password", "Doesn't match", "Пароль должен содержать не менее 6 символов!");
    }
}
