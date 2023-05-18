package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.*;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Subject;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.security.UserDetails;
import ru.selivanov.springproject.diplomaProject.services.TeacherService;
import ru.selivanov.springproject.diplomaProject.services.WorkloadService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public String show(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        model.addAttribute("user", teacherService.getUserByTeacher(id));

        model.addAttribute("scheduleDataList", teacherService.getScheduleDataByTeacher(id, new Date()));
        model.addAttribute("subjectList", teacherService.getSubjectListByTeacher(id));

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken.getToken());
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

        return "redirect:/teacher/" + user.getTeacher().getTeacherId();
    }

    @ResponseBody
    @GetMapping("/{id}/subjects")
    public List<Subject> getSubjectList(@PathVariable("id") int id) {
        return teacherService.getSubjectListByTeacher(id);
    }

    @ResponseBody
    @GetMapping("/{id}/groups")
    public List<Group> getGroupList(@PathVariable("id") int id,
                                    @RequestParam(value = "discipline", required = false) Integer subjectId,
                                    @RequestParam(value = "type", required = false) String type) {
        if (subjectId == null)
            return teacherService.getGroupListByTeacher(id);
        else {
            if (type == null)
                return teacherService.getGroupListByTeacherAndSubject(id, subjectId);
            else
                return teacherService.getGroupListByTeacherAndSubjectAndType(id, subjectId, type);
        }
    }

    @ResponseBody
    @GetMapping("/{id}/schedule")
    public List<TeacherScheduleDTO> getScheduleByDate(@PathVariable("id") int id, @RequestParam(value = "date") String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date getDate = dateFormat.parse(date);
        return teacherService.getScheduleDataByTeacher(id, getDate);
    }

    @ResponseBody
    @GetMapping("/{id}/journal")
    public List<AttendanceToShowDTO> getAttendanceList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId,
                                                   @RequestParam("group") int groupId,
                                                   @RequestParam("type") String type) {
        return teacherService.getAttendanceList(id, subjectId, groupId, type);
    }

    @ResponseBody
    @GetMapping("/{id}/grades")
    public List<GradesOfStudentsToShowDTO> getGradesList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId,
                                                       @RequestParam("group") int groupId,
                                                       @RequestParam("type") String type) {
        return teacherService.getGradesList(id, subjectId, groupId, type);
    }

    @ResponseBody
    @GetMapping("/{id}/assignments")
    public List<Assignment> getAssignmentList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId,
                                              @RequestParam("group") int groupId,
                                              @RequestParam("type") String type) {
        return teacherService.getAssignmentList(id, subjectId, groupId, type);
    }
}
