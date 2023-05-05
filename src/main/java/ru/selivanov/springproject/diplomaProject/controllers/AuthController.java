package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.PasswordDTO;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.RegistrationService;
import ru.selivanov.springproject.diplomaProject.util.PasswordValidator;
import ru.selivanov.springproject.diplomaProject.util.UserValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserValidator userValidator;
    private final PasswordValidator passwordValidator;
    private final RegistrationService registrationService;

    @Autowired
    public AuthController(UserValidator userValidator, PasswordValidator passwordValidator, RegistrationService registrationService) {
        this.userValidator = userValidator;
        this.passwordValidator = passwordValidator;
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") User user) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("user") @Valid User user,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "/auth/registration";

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";

        registrationService.register(user);

        return "redirect:/auth/login";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(@ModelAttribute("passwordDTO")PasswordDTO passwordDTO) {
        return "auth/modalChangePassword";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordDTO") @Valid PasswordDTO passwordDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "/auth/modalChangePassword";

        passwordValidator.validate(passwordDTO, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/modalChangePassword";

        registrationService.changePassword(passwordDTO.getNewPassword());

        return "redirect:/student/1";
    }
}
