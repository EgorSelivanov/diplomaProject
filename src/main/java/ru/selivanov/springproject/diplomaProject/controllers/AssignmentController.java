package ru.selivanov.springproject.diplomaProject.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.services.AssignmentService;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateAssignments(@RequestBody Map<Integer, List<String>> dictionary) {
        for (Map.Entry<Integer, List<String>> entry : dictionary.entrySet()) {
            int assignmentId = entry.getKey();
            List<String> list = entry.getValue();

            Assignment assignment = assignmentService.getAssignmentById(assignmentId).orElse(null);
            if (assignment == null || list.size() != 4)
                return ResponseEntity.notFound().build();

            String type = list.get(0);
            String description = list.get(1);
            int maxPoints = Integer.parseInt(list.get(2));
            String[] dateFormat = list.get(3).split("-");
            int year = Integer.parseInt(dateFormat[0]);
            int month = Integer.parseInt(dateFormat[1]);
            int day = Integer.parseInt(dateFormat[2]);
            Date date = new Date(year - 1900, month - 1, day);

            assignment.setType(type);
            assignment.setDescription(description);
            assignment.setMaxPoints(maxPoints);
            assignment.setDate(date);
            assignmentService.saveAssignment(assignment);
        }

        // Возвращаем ответ
        return ResponseEntity.ok().build();
    }
}
