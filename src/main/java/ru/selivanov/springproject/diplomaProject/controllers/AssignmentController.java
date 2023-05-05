package ru.selivanov.springproject.diplomaProject.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.services.AssignmentService;

import java.util.List;

@RestController
@RequestMapping("/assignment")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final ModelMapper modelMapper;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, ModelMapper modelMapper) {
        this.assignmentService = assignmentService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{type}")
    public List<Assignment> showAssignmentByType(@PathVariable("type") String type) {
        return assignmentService.findByType(type);
    }
}
