package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.AttendanceStudentDTO;
import ru.selivanov.springproject.diplomaProject.dto.GradesDTO;
import ru.selivanov.springproject.diplomaProject.dto.StudentScheduleDTO;
import ru.selivanov.springproject.diplomaProject.model.Notification;
import ru.selivanov.springproject.diplomaProject.services.NotificationService;
import ru.selivanov.springproject.diplomaProject.services.StudentService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final NotificationService notificationService;
    @Autowired
    public StudentController(StudentService studentService, NotificationService notificationService) {
        this.studentService = studentService;
        this.notificationService = notificationService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        model.addAttribute("user", studentService.getUserByStudent(id));

        model.addAttribute("scheduleDataList", studentService.getScheduleDataByStudent(id, new Date()));
        model.addAttribute("subjectList", studentService.getSubjectListByStudent(id));
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        model.addAttribute("_csrf", csrfToken.getToken());
        return "student/student";
    }

    @GetMapping("/{id}/notifications")
    public String showNotifications(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", studentService.getUserByStudent(id));

        model.addAttribute("notificationListNew", notificationService.getNotificationListNotShowedByStudentId(id));
        List<Notification> notificationListOld = notificationService.getNotificationListShowedByStudentId(id);
        if (notificationListOld.size() > 10)
            notificationListOld = notificationListOld.subList(0, 10);
        model.addAttribute("notificationListOld", notificationListOld);
        return "student/studentNotifications";
    }

    @ResponseBody
    @PostMapping("/{id}/notifications")
    public ResponseEntity<?> setShowedNotifications(@PathVariable("id") int id) {
        notificationService.setShowedNotificationByStudent(id);
        System.out.println("Updated");
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @DeleteMapping("/{id}/notifications")
    public ResponseEntity<?> deleteNotifications(@PathVariable("id") int id) {
        notificationService.deleteAllNotificationsByStudent(id);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @GetMapping("/{id}/journal")
    public List<GradesDTO> getGradesList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId) {
        return studentService.getGradesListByStudent(id, subjectId);
    }

    @ResponseBody
    @GetMapping("/{id}/attendance")
    public List<AttendanceStudentDTO> getAttendanceList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId) {
        return studentService.getAttendanceListByStudentAndSubject(id, subjectId);
    }

    @ResponseBody
    @GetMapping("/{id}/schedule")
    public List<StudentScheduleDTO> getScheduleByDate(@PathVariable("id") int id, @RequestParam(value = "date") String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date getDate = dateFormat.parse(date);
        return studentService.getScheduleDataByStudent(id, getDate);
    }
}
