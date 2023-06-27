package ru.selivanov.springproject.diplomaProject.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.selivanov.springproject.diplomaProject.model.Subject;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.SubjectService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminSubjectController {
    private final AdminService adminService;
    private final SubjectService subjectService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminSubjectController(AdminService adminService, SubjectService subjectService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.subjectService = subjectService;
        this.objectMapper = objectMapper;
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
    public ResponseEntity<String> editSubject(@PathVariable("id") int id, @PathVariable("subjectEditId") int subjectEditId,
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

    @PostMapping("/{id}/subject/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                 @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<Subject> groupJSONDTO = objectMapper.readValue(file.getInputStream(), new TypeReference<List<Subject>>() {});
                subjectService.updateDataByJSON(groupJSONDTO);
            }
            catch (NoSuchFieldException e) {
                return new ResponseEntity<>("Ошибка: Файл содержит некорректные данные: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                return new ResponseEntity<>("Ошибка при попытке чтения данных: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (ConstraintViolationException e) {
                return new ResponseEntity<>("Ошибка: Файл содержит пустые или некорректные данные: " +
                        e.getConstraintViolations().stream().iterator().next().getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Файл успешно загружен", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Ошибка: Файл не найден", HttpStatus.BAD_REQUEST);
        }
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
