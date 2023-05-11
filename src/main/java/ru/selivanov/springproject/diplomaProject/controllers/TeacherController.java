package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.NewScheduleTeacherDTO;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Subject;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.security.UserDetails;
import ru.selivanov.springproject.diplomaProject.services.TeacherService;
import ru.selivanov.springproject.diplomaProject.services.WorkloadService;

import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final WorkloadService workloadService;

    @Autowired
    public TeacherController(TeacherService teacherService, WorkloadService workloadService) {
        this.teacherService = teacherService;
        this.workloadService = workloadService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", teacherService.getUserByTeacher(id));

        model.addAttribute("scheduleDataList", teacherService.getScheduleDataByTeacher(id));
        model.addAttribute("subjectList", teacherService.getSubjectListByTeacher(id));
        return "teacher/teacher";
    }

    @GetMapping("/new-schedule")
    public String modalNewSchedulePage(@ModelAttribute("teacherScheduleDTO") NewScheduleTeacherDTO teacherScheduleDTO) {
        return "teacher/modalNewSchedule";
    }

    @PostMapping("/new-schedule")
    public String createNewSchedule(@ModelAttribute("teacherScheduleDTO") NewScheduleTeacherDTO teacherScheduleDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        teacherScheduleDTO.setTeacherId(user.getTeacher().getTeacherId());

        workloadService.createNewTeacherSchedule(teacherScheduleDTO);

        return "redirect:/teacher/1";
    }

    @ResponseBody
    @GetMapping("/{id}/subjects")
    public List<Subject> getSubjectList(@PathVariable("id") int id) {
        return teacherService.getSubjectListByTeacher(id);
    }

    @ResponseBody
    @GetMapping("/{id}/groups")
    public List<Group> getGroupList(@PathVariable("id") int id) {
        return teacherService.getGroupListByTeacher(id);
    }
}
