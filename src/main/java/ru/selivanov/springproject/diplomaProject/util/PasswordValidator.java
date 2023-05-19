package ru.selivanov.springproject.diplomaProject.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.selivanov.springproject.diplomaProject.dto.PasswordDTO;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.security.UserDetails;
import ru.selivanov.springproject.diplomaProject.services.UserService;

@Component
public class PasswordValidator implements Validator {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordValidator(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PasswordDTO passwordDTO = (PasswordDTO) target;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        if (!passwordEncoder.matches(passwordDTO.getPassword(), user.getPassword())) {
            errors.rejectValue("password", "Doesn't match", "Неверный пароль!");
        }

        if (!passwordDTO.getNewPassword().equals(passwordDTO.getNewPasswordConfirmation())) {
            errors.rejectValue("newPassword", "Doesn't match", "Пароли не совпадают!");
            errors.rejectValue("newPasswordConfirmation", "Doesn't match", "Пароли не совпадают!");
        }
    }
}
