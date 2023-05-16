package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.selivanov.springproject.diplomaProject.model.Grade;
import ru.selivanov.springproject.diplomaProject.services.GradeService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/grade")
public class GradeController {
    private final GradeService gradeService;

    @Autowired
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateStudentGrade(@RequestBody Map<String, List<Integer>> dictionary) {
        for (Map.Entry<String, List<Integer>> entry : dictionary.entrySet()) {
            List<Integer> list = entry.getValue();
            int value = list.get(0);
            int studentId = list.get(1);
            int assignmentId = list.get(2);

            Grade grade = gradeService.getGradeByStudentAndAssignment(studentId, assignmentId);
            if (grade == null)
                return ResponseEntity.notFound().build();
            grade.setPoints(value);

            gradeService.saveGrade(grade);
        }

        // Возвращаем ответ
        return ResponseEntity.ok().build();
    }
}
