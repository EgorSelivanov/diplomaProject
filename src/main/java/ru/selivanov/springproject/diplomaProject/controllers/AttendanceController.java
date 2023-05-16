package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.selivanov.springproject.diplomaProject.model.Attendance;
import ru.selivanov.springproject.diplomaProject.services.AttendanceService;

import java.util.Map;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    @Autowired
    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateAttendances(@RequestBody Map<Integer, Integer> dictionary) {
        for (Map.Entry<Integer, Integer> entry : dictionary.entrySet()) {
            int attendanceId = entry.getKey();
            int value = entry.getValue();

            Attendance attendance = attendanceService.getAttendanceById(attendanceId).orElse(null);
            if (attendance == null)
                return ResponseEntity.notFound().build();

            attendance.setPresent(value);
            attendanceService.saveAttendance(attendance);
        }

        // Возвращаем ответ
        return ResponseEntity.ok().build();
    }
}
