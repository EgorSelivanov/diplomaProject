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
import ru.selivanov.springproject.diplomaProject.dto.StudentEditDTO;
import ru.selivanov.springproject.diplomaProject.dto.StudentJSONDTO;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.*;
import ru.selivanov.springproject.diplomaProject.util.UserValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminStudentController {
    private final AdminService adminService;
    private final StudentService studentService;
    private final UserService userService;
    private final GroupService groupService;
    private final WorkloadService workloadService;
    private final UserValidator userValidator;
    private final jakarta.validation.Validator validatorCast;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminStudentController(AdminService adminService, StudentService studentService, UserService userService, GroupService groupService, WorkloadService workloadService, UserValidator userValidator, Validator validatorCast, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.studentService = studentService;
        this.userService = userService;
        this.groupService = groupService;
        this.workloadService = workloadService;
        this.userValidator = userValidator;
        this.validatorCast = validatorCast;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}/students")
    public String getStudentPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        model.addAttribute("courseNumberList", studentService.getCourseNumberList());
        return "admin/students/student";
    }

    @GetMapping("/{id}/studentList")
    public String getStudentList(@PathVariable("id") int id, Model model,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "group", required = false) Integer groupId) {
        model.addAttribute("user", adminService.getUserById(id));
        if (search == null || search.trim().equals(""))
            if (groupId == null)
                model.addAttribute("studentList", studentService.getStudentListSearch(""));
            else
                model.addAttribute("studentList", studentService.getStudentListByGroup(groupId));
        else
            if (groupId == null)
                model.addAttribute("studentList", studentService.getStudentListSearch(search));
            else
                model.addAttribute("studentList", studentService.getStudentListSearchByGroup(search, groupId));

        model.addAttribute("courseNumberList", studentService.getCourseNumberList());

        return "admin/students/studentList";
    }

    @GetMapping("/new-student")
    public String getNewStudentPage(Model model) {
        model.addAttribute("courseNumberList", studentService.getCourseNumberList());
        model.addAttribute("errorStudent", "");
        return "admin/students/modalNewStudent";
    }

    @PostMapping("/new-student")
    public String saveNewStudent(@Valid @RequestBody StudentEditDTO studentEditDTO,
                                 Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorStudent", getAllErrors(bindingResult));
            return "/admin/students/modalNewStudent";
        }

        User user = new User();
        user.setFirstName(studentEditDTO.getFirstName());
        user.setSecondName(studentEditDTO.getSecondName());
        user.setPatronymic(studentEditDTO.getPatronymic());
        user.setPassword(studentEditDTO.getPassword());
        user.setUsername(studentEditDTO.getUsername());
        user.setEmail(studentEditDTO.getEmail());
        user.setRole("ROLE_STUDENT");

        BindingResult bindingResult1 = new BeanPropertyBindingResult(user, "user");
        userValidator.validate(user, bindingResult1);
        if (bindingResult1.hasErrors()) {
            model.addAttribute("errorStudent", getAllErrors(bindingResult1));
            return "/admin/students/modalNewStudent";
        }

        int userId = userService.addUser(user);
        user.setUserId(userId);

        Student student = new Student();
        student.setUser(user);
        student.setGroup(groupService.getGroupById(studentEditDTO.getGroupId()));
        studentService.addStudent(student);

        return "admin/empty";
    }

    @ResponseBody
    @PostMapping("/{id}/edit-students")
    public ResponseEntity<String> editStudents(@PathVariable("id") int id,
                                               @Valid @RequestBody List<StudentEditDTO> list, BindingResult bindingResult) {
        List<Student> studentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<Student> studentListToChangeAttendances = new ArrayList<>();
        for (StudentEditDTO editDTO : list) {
            Student student = studentService.getStudentById(editDTO.getStudentId());
            Group group = student.getGroup();
            student.setGroup(groupService.getGroupById(editDTO.getGroupId()));
            studentList.add(student);
            if (!group.getName().equals(student.getGroup().getName()))
                studentListToChangeAttendances.add(student);

            User user = studentService.getUserByStudent(editDTO.getStudentId());
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

        studentService.updateStudentList(studentList);
        userService.updateUserList(userList);

        for (Student student :studentListToChangeAttendances) {
            workloadService.deleteStudentAttendances(student.getStudentId());
            workloadService.createStudentAttendances(student.getStudentId());
        }
        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{id}/deleteStudent")
    public ResponseEntity<String> deleteStudentById(@PathVariable("id") int id) {
        Student student = studentService.getStudentById(id);
        if (student == null)
            return ResponseEntity.ofNullable("Данного студента не найдено!");
        studentService.deleteStudent(id);
        return ResponseEntity.ok("Удаление успешно!");
    }

    @ResponseBody
    @PostMapping("/{id}/student/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                 @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<StudentJSONDTO> teacherEditDTOList = objectMapper.readValue(file.getInputStream(), new TypeReference<List<StudentJSONDTO>>() {});
                studentService.updateDataByJSON(teacherEditDTOList);
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

    public <T> String getAllErrors(Set<ConstraintViolation<T>> violations) {
        StringJoiner joiner = new StringJoiner(", ");

        for (ConstraintViolation<T> violation : violations) {
            String errorMessage = violation.getPropertyPath() + ": " + violation.getMessage();
            joiner.add(errorMessage);
        }

        return joiner.toString();
    }
}
