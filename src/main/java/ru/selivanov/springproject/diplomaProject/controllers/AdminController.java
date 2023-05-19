package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.EditUserDTO;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.UserService;
import ru.selivanov.springproject.diplomaProject.util.ResetPasswordValidator;
import ru.selivanov.springproject.diplomaProject.util.UserValidator;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserValidator userValidator;
    private final ResetPasswordValidator resetPasswordValidator;
    private final AdminService adminService;
    private final UserService userService;

    @Autowired
    public AdminController(UserValidator userValidator, ResetPasswordValidator resetPasswordValidator, AdminService adminService, UserService userService) {
        this.userValidator = userValidator;
        this.resetPasswordValidator = resetPasswordValidator;
        this.adminService = adminService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String getAdminPage(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        model.addAttribute("user", adminService.getUserById(id));

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken.getToken());
        return "admin/admin";
    }

    @GetMapping("/{id}/personal")
    public String getPersonalPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        model.addAttribute("admins", adminService.getAdminList());

        return "admin/personal";
    }

    @GetMapping("/{id}/new-user")
    public String getNewUserPage(@PathVariable("id") int id, Model model) {
        User user = new User();
        user.setUserId(id);
        model.addAttribute("user", user);

        return "admin/newUser";
    }

    @PostMapping("/{id}/new-user")
    public String saveNewUser(@PathVariable("id") int id,
                              @ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult) {
        user.setUserId(id);
        if (bindingResult.hasErrors()) {
            return "/admin/edit";
        }

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/admin/edit";

        user.setUserId(null);
        userService.addUser(user);
        return "redirect:/admin/" + id;
    }

    @GetMapping("/{id}/edit/{userEditId}")
    public String getEditUserPage(@PathVariable("id") int id, @PathVariable("userEditId") int userEditId, Model model) {
        User user = adminService.getUserById(userEditId);
        EditUserDTO editUserDTO = new EditUserDTO(user, id);
        editUserDTO.setUserId(userEditId);
        model.addAttribute("user", editUserDTO);

        return "admin/edit";
    }

    @PostMapping("/{id}/edit/{userEditId}")
    public String editUser(@PathVariable("id") int id, @PathVariable("userEditId") int userEditId,
                           @ModelAttribute("user") @Valid EditUserDTO userDTO,
                           BindingResult bindingResult) {
        User user = userDTO.getUser();
        user.setUserId(userEditId);
        userDTO.setUserId(userEditId);
        if (bindingResult.hasErrors()) {
            return "/admin/edit";
        }

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors())
            return "/admin/edit";

        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().equals("")) {
            resetPasswordValidator.validate(userDTO.getPassword(), bindingResult);
            if (bindingResult.hasErrors())
                return "/admin/edit";
            userService.updateUser(userEditId, user, userDTO.getPassword());
        }
        else
            userService.updateUser(userEditId, user);
        return "redirect:/admin/" + id;
    }

    @ResponseBody
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteAssignmentById(@PathVariable("id") int id) {
        User user = adminService.getUserById(id);
        if (user == null)
            return ResponseEntity.ofNullable("Данного пользователя не найдено!");
        userService.deleteUser(id);
        return ResponseEntity.ok("Удаление успешно!");
    }

    @GetMapping("/{id}/adminList")
    public String getAdminList(@PathVariable("id") int id, Model model,
                               @RequestParam(value = "search", required = false) String search) {
        model.addAttribute("user", adminService.getUserById(id));
        System.out.println(search);
        if (search == null || search.trim().equals(""))
            model.addAttribute("admins", adminService.getAdminList());
        else
            model.addAttribute("admins", adminService.getAdminListSearch(search));

        return "admin/adminList";
    }
}
