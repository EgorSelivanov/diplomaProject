package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.PasswordDTO;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.security.UserDetails;
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

    @GetMapping("/redirect")
    public String redirect() {
        User user = getAuthorizedUser();

        if ("ROLE_STUDENT".equals(user.getRole()))
            return "redirect:/student/" + user.getStudent().getStudentId();
        if ("ROLE_TEACHER".equals(user.getRole()))
            return "redirect:/teacher/" + user.getTeacher().getTeacherId();
        else
            return "redirect:/logout";
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

        registrationService.changePassword(getAuthorizedUser(), passwordDTO.getNewPassword());

        return redirect();
    }

    private User getAuthorizedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
