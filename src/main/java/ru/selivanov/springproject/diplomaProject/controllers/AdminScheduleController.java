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
import ru.selivanov.springproject.diplomaProject.dto.ScheduleJSONDTO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleToEditDTO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleWorkloadDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.services.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminScheduleController {
    private final AdminService adminService;
    private final ScheduleService scheduleService;
    private final TeacherService teacherService;
    private final WorkloadService workloadService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AdminScheduleController(AdminService adminService, ScheduleService scheduleService, TeacherService teacherService, WorkloadService workloadService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.scheduleService = scheduleService;
        this.teacherService = teacherService;
        this.workloadService = workloadService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}/schedules")
    public String getSchedulePage(@PathVariable("id") int adminId, Model model) {
        model.addAttribute("user", adminService.getUserById(adminId));
        model.addAttribute("departmentList", teacherService.getDepartmentList());
        return "admin/schedule/schedule";
    }

    @GetMapping("/{id}/scheduleList")
    public String getScheduleList(@PathVariable("id") int id,
                                  @RequestParam(value = "search", required = false) String search,
                                  Model model) {
        if (search == null || search.trim().equals(""))
            model.addAttribute("workloadList", scheduleService.getWorkloadList());
        else {
            List<ScheduleWorkloadDTO> list = new ArrayList<>();
            list.addAll(scheduleService.getWorkloadListBySearch(search));
            list.addAll(scheduleService.getWorkloadListBySearchTeacher(search));
            model.addAttribute("workloadList", list);
        }

        return "admin/schedule/scheduleList";
    }

    @GetMapping("/workload-schedule/{workloadId}")
    public String getWorkloadPage(@PathVariable("workloadId") int workloadId, Model model) {
        model.addAttribute("workload", workloadService.getWorkloadById(workloadId));
        return "admin/schedule/scheduleOfWorkload";
    }

    @GetMapping("/{id}/scheduleList/{workloadId}")
    public String getWorkloadList(@PathVariable("id") int id,
                                  @PathVariable("workloadId") int workloadId,
                                  @RequestParam(value = "search", required = false) String search,
                                  Model model) {
        model.addAttribute("admin", adminService.getUserById(id));
        model.addAttribute("scheduleList", scheduleService.getScheduleListByWorkload(workloadId));

        return "admin/schedule/scheduleOfWorkloadList";
    }

    @GetMapping("/new-schedule")
    public String getNewSchedulePage(Model model) {
        model.addAttribute("errorSchedule", "");
        return "admin/schedule/modalNewSchedule";
    }

    @PostMapping("/new-schedule")
    public String saveNewSchedule(@Valid @RequestBody ScheduleToEditDTO scheduleNewDTO,
                                  Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorSchedule", getAllErrors(bindingResult));
            return "/admin/schedule/modalNewSchedule";
        }

        Schedule schedule = new Schedule();
        schedule.setWorkload(workloadService.getWorkloadById(scheduleNewDTO.getWorkloadId()));
        schedule.setRepeat(scheduleNewDTO.getRepeat());
        schedule.setBuilding(scheduleNewDTO.getBuilding());
        schedule.setAudience(scheduleNewDTO.getAudience());
        schedule.setDayOfWeek(scheduleNewDTO.getDayOfWeek());
        schedule.setStartTime(scheduleNewDTO.getStartTimeFormat());
        schedule.setEndTime(scheduleNewDTO.getEndTimeFormat());

        scheduleService.createNewSchedule(schedule);

        workloadService.createStudentsAttendancesForNewSchedule(schedule);

        return "admin/empty";
    }

    @ResponseBody
    @PostMapping("/{id}/schedule/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                 @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<ScheduleJSONDTO> scheduleJSONDTOList = objectMapper.readValue(file.getInputStream(), new TypeReference<List<ScheduleJSONDTO>>() {});
                scheduleService.updateDataByJSON(scheduleJSONDTOList);
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
    @PostMapping("/{id}/edit-schedule")
    public ResponseEntity<String> editSchedule(@PathVariable("id") int id,
                                               @Valid @RequestBody List<ScheduleToEditDTO> list, BindingResult bindingResult) {
        List<Schedule> scheduleList = new ArrayList<>();

        for (ScheduleToEditDTO editDTO : list) {
            Schedule schedule = scheduleService.getScheduleById(editDTO.getScheduleId());
            schedule.setEndTime(editDTO.getEndTimeFormat());
            schedule.setStartTime(editDTO.getStartTimeFormat());
            schedule.setAudience(editDTO.getAudience());
            schedule.setBuilding(editDTO.getBuilding());
            schedule.setRepeat(editDTO.getRepeat());
            schedule.setDayOfWeek(editDTO.getDayOfWeek());

            scheduleList.add(schedule);
        }

        scheduleService.updateScheduleList(scheduleList);
        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{scheduleId}/deleteSchedule")
    public ResponseEntity<String> deleteScheduleById(@PathVariable("scheduleId") int id) {
        Schedule schedule = scheduleService.getScheduleById(id);
        if (schedule == null)
            return ResponseEntity.ofNullable("Данного занятия не найдено!");
        scheduleService.deleteSchedule(id);
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
