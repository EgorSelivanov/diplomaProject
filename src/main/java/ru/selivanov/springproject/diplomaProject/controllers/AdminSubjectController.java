package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.model.Subject;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.SubjectService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminSubjectController {
    private final AdminService adminService;
    private final SubjectService subjectService;

    @Autowired
    public AdminSubjectController(AdminService adminService, SubjectService subjectService) {
        this.adminService = adminService;
        this.subjectService = subjectService;
    }

    @GetMapping("/{id}/subjects")
    public String getSubjectPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        return "admin/subjects/subject";
    }

    @GetMapping("/{id}/subjectList")
    public String getSubjectList(@PathVariable("id") int id, Model model,
                               @RequestParam(value = "search", required = false) String search) {
        model.addAttribute("user", adminService.getUserById(id));
        if (search == null || search.trim().equals(""))
            model.addAttribute("subjects", subjectService.getSubjects());
        else
            model.addAttribute("subjects", subjectService.getSubjectsSearch(search));

        return "admin/subjects/subjectList";
    }

    @GetMapping("/new-subject")
    public String getNewSubjectPage(Model model) {
        model.addAttribute("errorSubject", "");
        return "admin/subjects/modalNewSubject";
    }

    @PostMapping("/new-subject")
    public String saveNewSubject(@RequestBody @Valid Subject subject,
                               Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorSubject", getAllErrors(bindingResult));
            return "/admin/subjects/modalNewSubject";
        }

        subjectService.createSubject(subject);
        return "admin/empty";
    }

    // -----end-----Вкладка Группы-----end-----

    @ResponseBody
    @PostMapping("/{id}/edit-subject/{subjectEditId}")
    public ResponseEntity<String> editSpeciality(@PathVariable("id") int id, @PathVariable("subjectEditId") int subjectEditId,
                                                 @RequestBody @Valid Subject subject, BindingResult bindingResult) {
        if (subjectService.getSubjectById(subjectEditId) == null)
            bindingResult.rejectValue("subjectId", "Doesn't exist", "Дисциплины с таким id не существует!");

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(getAllErrors(bindingResult));

        subjectService.updateSubject(subjectEditId, subject);

        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{id}/deleteSubject")
    public ResponseEntity<String> deleteSubjectById(@PathVariable("id") int id) {
        Subject subject = subjectService.getSubjectById(id);
        if (subject == null)
            return ResponseEntity.ofNullable("Данной дисциплины не найдено!");
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Удаление успешно!");
    }

    private String getAllErrors(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // Преобразуем ошибки в текстовое представление
        return String.join(", ", errors);
    }
}
