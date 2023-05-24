package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.model.Speciality;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.SpecialityService;
import ru.selivanov.springproject.diplomaProject.util.SpecialityValidator;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminSpecialityController {
    private final SpecialityValidator specialityValidator;
    private final AdminService adminService;
    private final SpecialityService specialityService;

    @Autowired
    public AdminSpecialityController(SpecialityValidator specialityValidator, AdminService adminService, SpecialityService specialityService) {
        this.specialityValidator = specialityValidator;
        this.adminService = adminService;
        this.specialityService = specialityService;
    }

    // -----begin-----Вкладка Специальности-----begin-----
    @GetMapping("/{id}/specialities")
    public String getSpecialitiesPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        return "admin/specialities/speciality";
    }

    @GetMapping("/{id}/specialityList")
    public String getSpecialityList(@PathVariable("id") int id, Model model,
                                    @RequestParam(value = "search", required = false) String search) {
        model.addAttribute("user", adminService.getUserById(id));
        if (search == null || search.trim().equals(""))
            model.addAttribute("specialities", specialityService.getSpecialities());
        else
            model.addAttribute("specialities", specialityService.getSpecialitiesSearch(search));

        return "admin/specialities/specialityList";
    }

    @GetMapping("/new-speciality")
    public String getNewSpecialityPage(Model model) {
        model.addAttribute("errorSpeciality", "");
        return "admin/specialities/modalNewSpeciality";
    }

    @PostMapping("/new-speciality")
    public String saveNewSpeciality(@RequestBody Speciality speciality,
                                    Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorSpeciality", getAllErrors(bindingResult));
            return "/admin/specialities/modalNewSpeciality";
        }

        specialityValidator.validate(speciality, bindingResult);

        if (bindingResult.hasErrors()){
            model.addAttribute("errorSpeciality", getAllErrors(bindingResult));
            return "/admin/specialities/modalNewSpeciality";
        }

        specialityService.createSpeciality(speciality);
        return "admin/empty";
    }

    @ResponseBody
    @PostMapping("/{id}/edit-speciality/{specialityEditId}")
    public ResponseEntity<String> editSpeciality(@PathVariable("id") int id, @PathVariable("specialityEditId") int specialityEditId,
                                                 @RequestBody @Valid Speciality speciality, BindingResult bindingResult) {
        Speciality speciality1 = specialityService.getSpecialityById(specialityEditId);
        if (speciality1 == null)
            bindingResult.rejectValue("specialityId", "Doesn't exist", "Специальности с таким id не существует!");
        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(getAllErrors(bindingResult));

        specialityValidator.validate(speciality, bindingResult);

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(getAllErrors(bindingResult));

        specialityService.updateSpeciality(specialityEditId, speciality);

        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{id}/deleteSpeciality")
    public ResponseEntity<String> deleteSpecialityById(@PathVariable("id") int id) {
        Speciality speciality = specialityService.getSpecialityById(id);
        if (speciality == null)
            return ResponseEntity.ofNullable("Данной специальности не найдено!");
        specialityService.deleteSpeciality(id);
        return ResponseEntity.ok("Удаление успешно!");
    }
    // -----end-----Вкладка Специальности-----end-----

    private String getAllErrors(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // Преобразуем ошибки в текстовое представление
        return String.join(", ", errors);
    }
}
