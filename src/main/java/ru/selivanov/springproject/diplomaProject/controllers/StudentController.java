package ru.selivanov.springproject.diplomaProject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.GradesDTO;
import ru.selivanov.springproject.diplomaProject.services.StudentService;

import java.util.List;


@Controller
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;


    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", studentService.getUserByStudent(id));

        model.addAttribute("scheduleDataList", studentService.getScheduleDataByStudent(id));
        model.addAttribute("subjectList", studentService.getSubjectListByStudent(id));
        return "student/student";
    }

    @ResponseBody
    @GetMapping("/{id}/journal")
    public List<GradesDTO> getGradesList(@PathVariable("id") int id, @RequestParam("discipline") int subjectId) {
        return studentService.getGradesListByStudent(id, subjectId);
    }
}
