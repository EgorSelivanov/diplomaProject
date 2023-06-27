package ru.selivanov.springproject.diplomaProject.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.selivanov.springproject.diplomaProject.dto.TeacherEditDTO;
import ru.selivanov.springproject.diplomaProject.model.Teacher;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.TeacherService;
import ru.selivanov.springproject.diplomaProject.services.UserService;
import ru.selivanov.springproject.diplomaProject.util.UserValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminTeacherController {
    private final AdminService adminService;
    private final TeacherService teacherService;
    private final UserService userService;
    private final UserValidator userValidator;
    private final jakarta.validation.Validator validatorCast;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminTeacherController(AdminService adminService, TeacherService teacherService, UserService userService, UserValidator userValidator, Validator validator, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.teacherService = teacherService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.validatorCast = validator;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}/teachers")
    public String getTeacherPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        model.addAttribute("departmentList", teacherService.getDepartmentList());
        return "admin/teachers/teacher";
    }

    @GetMapping("/{id}/teacherList")
    public String getTeacherList(@PathVariable("id") int id, Model model,
                                 @RequestParam(value = "search", required = false) String search,
                                 @RequestParam(value = "department", required = false) String department) {
        model.addAttribute("user", adminService.getUserById(id));
        if (search == null || search.trim().equals("")) {
            if (department == null || department.trim().equals(""))
                model.addAttribute("teacherList", teacherService.getTeacherList());
            else
                model.addAttribute("teacherList", teacherService.getTeacherListSearchDepartment("", department));
        }
        else {
            if (department == null || department.trim().equals(""))
                model.addAttribute("teacherList", teacherService.getTeacherListSearch(search));
            else
                model.addAttribute("teacherList", teacherService.getTeacherListSearchDepartment(search, department));
        }

        return "admin/teachers/teacherList";
    }

    @GetMapping("/new-teacher")
    public String getNewTeacherPage(Model model) {
        model.addAttribute("errorTeacher", "");
        return "admin/teachers/modalNewTeacher";
    }

    @PostMapping("/new-teacher")
    public String saveNewTeacher(@Valid @RequestBody TeacherEditDTO teacherEditDTO,
                               Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorTeacher", getAllErrors(bindingResult));
            return "/admin/teachers/modalNewTeacher";
        }

        User user = new User();
        user.setFirstName(teacherEditDTO.getFirstName());
        user.setSecondName(teacherEditDTO.getSecondName());
        user.setPatronymic(teacherEditDTO.getPatronymic());
        user.setPassword(teacherEditDTO.getPassword());
        user.setUsername(teacherEditDTO.getUsername());
        user.setEmail(teacherEditDTO.getEmail());
        user.setRole("ROLE_TEACHER");

        BindingResult bindingResult1 = new BeanPropertyBindingResult(user, "user");
        userValidator.validate(user, bindingResult1);
        if (bindingResult1.hasErrors()) {
            model.addAttribute("errorTeacher", getAllErrors(bindingResult1));
            return "/admin/teachers/modalNewTeacher";
        }

        int userId = userService.addUser(user);
        user.setUserId(userId);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setPosition(teacherEditDTO.getPosition());
        teacher.setDepartment(teacherEditDTO.getDepartment());
        teacherService.addTeacher(teacher);

        return "admin/empty";
    }

    @ResponseBody
    @PostMapping("/{id}/edit-teachers")
    public ResponseEntity<String> editTeachers(@PathVariable("id") int id,
                                               @Valid @RequestBody List<TeacherEditDTO> list, BindingResult bindingResult) {
        List<Teacher> teacherList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for (TeacherEditDTO editDTO : list) {
            Teacher teacher = teacherService.getTeacherById(editDTO.getTeacherId());
            teacher.setDepartment(editDTO.getDepartment());
            teacher.setPosition(editDTO.getPosition());
            teacherList.add(teacher);

            Set<ConstraintViolation<Teacher>> violations = validatorCast.validate(teacher);
            if (!violations.isEmpty()) {
                return ResponseEntity.badRequest().body(getAllErrors(violations));
            }

            User user = teacherService.getUserByTeacher(editDTO.getTeacherId());
            user.setSecondName(editDTO.getSecondName());
            user.setFirstName(editDTO.getFirstName());
            user.setPatronymic(editDTO.getPatronymic());
            user.setEmail(editDTO.getEmail());
            user.setUsername(editDTO.getUsername());
            userList.add(user);
            Set<ConstraintViolation<User>> violationsUser = validatorCast.validate(user);
            if (!violationsUser.isEmpty()) {
                return ResponseEntity.badRequest().body(getAllErrors(violationsUser));
            }
        }

        for (User user : userList) {
            BindingResult bindingResult1 = new BeanPropertyBindingResult(user, "user");
            userValidator.validate(user, bindingResult1);

            if (bindingResult1.hasErrors()) {
                return ResponseEntity.badRequest().body(getAllErrors(bindingResult1));
            }
        }

        teacherService.updateTeacherList(teacherList);
        userService.updateUserList(userList);
        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{id}/deleteTeacher")
    public ResponseEntity<String> deleteTeacherById(@PathVariable("id") int id) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher == null)
            return ResponseEntity.ofNullable("Данного преподавателя не найдено!");
        teacherService.deleteTeacher(id);
        return ResponseEntity.ok("Удаление успешно!");
    }

    @ResponseBody
    @PostMapping("/{id}/teacher/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                 @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<TeacherEditDTO> teacherEditDTOList = objectMapper.readValue(file.getInputStream(), new TypeReference<List<TeacherEditDTO>>() {});
                teacherService.updateDataByJSON(teacherEditDTOList);
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
        }
        return new ResponseEntity<>("Ошибка: Файл не найден", HttpStatus.BAD_REQUEST);
    }

    private String getAllErrors(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // Преобразуем ошибки в текстовое представление
        return String.join(", ", errors);
    }

    private <T> String getAllErrors(Set<ConstraintViolation<T>> violations) {
        StringJoiner joiner = new StringJoiner(", ");

        for (ConstraintViolation<T> violation : violations) {
            String errorMessage = violation.getPropertyPath() + ": " + violation.getMessage();
            joiner.add(errorMessage);
        }

        return joiner.toString();
    }
}
