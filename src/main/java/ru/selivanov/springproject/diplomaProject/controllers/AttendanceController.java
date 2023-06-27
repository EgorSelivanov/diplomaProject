package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.selivanov.springproject.diplomaProject.model.Attendance;
import ru.selivanov.springproject.diplomaProject.model.Notification;
import ru.selivanov.springproject.diplomaProject.services.AttendanceService;
import ru.selivanov.springproject.diplomaProject.services.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;
    private final NotificationService notificationService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService, NotificationService notificationService) {
        this.attendanceService = attendanceService;
        this.notificationService = notificationService;
    }

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateAttendances(@RequestBody Map<Integer, Integer> dictionary) {
        List<Notification> notificationList = new ArrayList<>();
        List<Attendance> attendanceList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
            int attendanceId = entry.getKey();
            int value = entry.getValue();

            Attendance attendance = attendanceService.getAttendanceById(attendanceId).orElse(null);
            if (attendance == null)
                return ResponseEntity.notFound().build();

            Notification notification = new Notification();
            notification.setStudent(attendance.getStudent());
            notification.setShowed(false);
            notification.setMessage("Изменен балл за посещение по дисциплине «" +
                    attendance.getSchedule().getWorkload().getSubject().getName() + "». Занятие: " +
                    attendance.getSchedule().getWorkload().getType() + ", дата: " + attendance.getDateFormat() +
                    ". Прежнее количество баллов: " + attendance.getPresent() + ". Текущее количество баллов: " + value);

            attendance.setPresent(value);
            attendanceList.add(attendance);
            notificationList.add(notification);
        }
        attendanceService.saveAttendanceList(attendanceList);
        notificationService.createListNotification(notificationList);

        // Возвращаем ответ
        return ResponseEntity.ok().build();
    }
}
