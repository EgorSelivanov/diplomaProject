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
import ru.selivanov.springproject.diplomaProject.dto.WorkloadJSONDTO;
import ru.selivanov.springproject.diplomaProject.dto.WorkloadNewDTO;
import ru.selivanov.springproject.diplomaProject.model.Workload;
import ru.selivanov.springproject.diplomaProject.services.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminWorkloadController {
    private final AdminService adminService;
    private final WorkloadService workloadService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final GroupService groupService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminWorkloadController(AdminService adminService, WorkloadService workloadService, TeacherService teacherService, StudentService studentService, SubjectService subjectService, GroupService groupService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.workloadService = workloadService;
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.groupService = groupService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/teacher-workload/{teacherId}")
    public String getWorkloadPage(@PathVariable("teacherId") int teacherId, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(teacherId));
        return "admin/workload/workload";
    }

    @GetMapping("/{id}/workloadList/{teacherId}")
    public String getWorkloadList(@PathVariable("id") int id,
                                 @PathVariable("teacherId") int teacherId,
                                 Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(teacherId));
        model.addAttribute("workloadList", workloadService.getWorkloadListToShowTeacher(teacherId));

        return "admin/workload/workloadList";
    }

    @GetMapping("/new-workload")
    public String getNewWorkloadPage(Model model) {
        model.addAttribute("courseNumberList", studentService.getCourseNumberList());
        model.addAttribute("subjectList", subjectService.getSubjects());
        model.addAttribute("errorWorkload", "");
        return "admin/workload/modalNewWorkload";
    }

    @PostMapping("/new-workload")
    public String saveNewWorkload(@Valid @RequestBody WorkloadNewDTO workloadNewDTO,
                                 Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorWorkload", getAllErrors(bindingResult));
            return "/admin/workload/modalNewWorkload";
        }

        Workload workload = new Workload();
        workload.setType(workloadNewDTO.getType());
        workload.setTeacher(teacherService.getTeacherById(workloadNewDTO.getTeacherId()));
        workload.setSubject(subjectService.getSubjectById(workloadNewDTO.getSubjectId()));
        workload.setGroup(groupService.getGroupById(workloadNewDTO.getGroupId()));
        workloadService.createWorkload(workload);

        return "admin/empty";
    }

    @ResponseBody
    @PostMapping("/{id}/workload/{teacherId}/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                 @PathVariable("teacherId") int teacherId,
                                                 @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<WorkloadJSONDTO> workloadJSONDTOList = objectMapper.readValue(file.getInputStream(), new TypeReference<List<WorkloadJSONDTO>>() {});
                workloadService.updateDataByJSON(teacherId, workloadJSONDTOList);
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

    @ResponseBody
    @DeleteMapping("/{workloadId}/deleteWorkload")
    public ResponseEntity<String> deleteWorkloadById(@PathVariable("workloadId") int workloadId) {
        Workload workload = workloadService.getWorkloadById(workloadId);
        if (workload == null)
            return ResponseEntity.ofNullable("Данной нагрузки не найдено!");
        workloadService.deleteWorkload(workloadId);
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
