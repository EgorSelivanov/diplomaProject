package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Grade;
import ru.selivanov.springproject.diplomaProject.model.Notification;
import ru.selivanov.springproject.diplomaProject.services.AssignmentService;
import ru.selivanov.springproject.diplomaProject.services.GradeService;
import ru.selivanov.springproject.diplomaProject.services.NotificationService;
import ru.selivanov.springproject.diplomaProject.services.StudentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/grade")
public class GradeController {
    private final GradeService gradeService;
    private final StudentService studentService;
    private final AssignmentService assignmentService;
    private final NotificationService notificationService;

    @Autowired
    public GradeController(GradeService gradeService, StudentService studentService, AssignmentService assignmentService, NotificationService notificationService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.assignmentService = assignmentService;
        this.notificationService = notificationService;
    }

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateStudentGrade(@RequestBody Map<String, List<Integer>> dictionary) {
        List<Notification> notificationList = new ArrayList<>();
        List<Grade> gradeList = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : dictionary.entrySet()) {
            List<Integer> list = entry.getValue();
            int value = list.get(0);
            int studentId = list.get(1);
            int assignmentId = list.get(2);

            Notification notification = new Notification();
            notification.setStudent(studentService.getStudentById(studentId));
            notification.setShowed(false);

            Grade grade = gradeService.getGradeByStudentAndAssignment(studentId, assignmentId);
            if (grade == null) {
                grade = new Grade();
                grade.setStudent(studentService.getStudentById(studentId));
                grade.setAssignment(assignmentService.getAssignmentById(assignmentId));

                Assignment assignment = grade.getAssignment();
                notification.setMessage("Была получена оценка по дисциплине «" +
                        assignment.getWorkload().getSubject().getName() + "». Работа: " +
                        assignment.getType() + ", дата: " + assignment.getDateFormat() +
                        ". Количество баллов: " + value + " из " + grade.getAssignment().getMaxPoints());
            }
            if (notification.getMessage() == null) {
                Assignment assignment = grade.getAssignment();
                notification.setMessage("Изменена оценка по дисциплине «" +
                        assignment.getWorkload().getSubject().getName() + "». Работа: " +
                        assignment.getType() + ", дата: "  + assignment.getDateFormat() +
                        ". Прежнее количество баллов: " + grade.getPoints() + ". Текущее количество баллов: " +
                        value + " из " + assignment.getMaxPoints());
            }

            grade.setPoints(value);

            gradeList.add(grade);
            notificationList.add(notification);
        }

        gradeService.saveGradeList(gradeList);
        notificationService.createListNotification(notificationList);

        // Возвращаем ответ
        return ResponseEntity.ok().build();
    }
}
