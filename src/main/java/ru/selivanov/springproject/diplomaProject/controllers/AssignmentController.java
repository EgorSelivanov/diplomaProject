package ru.selivanov.springproject.diplomaProject.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.CreateNewAssignmentDTO;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Workload;
import ru.selivanov.springproject.diplomaProject.services.AssignmentService;
import ru.selivanov.springproject.diplomaProject.services.WorkloadService;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/assignment")
public class AssignmentController {
    private final AssignmentService assignmentService;
    private final WorkloadService workloadService;
    private final ModelMapper modelMapper;

    @Autowired
    public AssignmentController(AssignmentService assignmentService, WorkloadService workloadService, ModelMapper modelMapper) {
        this.assignmentService = assignmentService;
        this.workloadService = workloadService;
        this.modelMapper = modelMapper;
    }

    @ResponseBody
    @GetMapping("/{type}")
    public List<Assignment> showAssignmentByType(@PathVariable("type") String type) {
        return assignmentService.findByType(type);
    }

    @GetMapping("/new-assignment")
    public String showNewAssignmentPage() {
        return "teacher/modalNewAssignment";
    }

    @ResponseBody
    @PostMapping("/new-assignment")
    public ResponseEntity<String> createAssignment(@RequestBody CreateNewAssignmentDTO assignment) {
        if (assignment == null || assignment.getDate() == null || assignment.getMaxPoints() == 0 || assignment.getType() == null ||
        assignment.getType().trim().equals(""))
            return ResponseEntity.notFound().build();

        int teacherId = assignment.getTeacherId();
        int subjectId = assignment.getSubjectId();
        int groupId = assignment.getGroupId();
        String type = assignment.getWorkloadType();
        Workload workload = workloadService.getWorkloadByTeacherSubjectGroupType(teacherId, subjectId, groupId, type);

        Assignment assignmentToCreate = new Assignment();
        assignmentToCreate.setWorkload(workload);
        assignmentToCreate.setType(assignment.getType());
        assignmentToCreate.setDescription(assignment.getDescription());
        assignmentToCreate.setMaxPoints(assignment.getMaxPoints());
        assignmentToCreate.setDate(assignment.getDate());
        assignmentService.saveAssignment(assignmentToCreate);
        return ResponseEntity.ok("Сохранение успешно!");
    }

    @ResponseBody
    @PostMapping()
    public ResponseEntity<?> updateAssignments(@RequestBody Map<Integer, List<String>> dictionary) {
        for (Map.Entry<Integer, List<String>> entry : dictionary.entrySet()) {
            int assignmentId = entry.getKey();
            List<String> list = entry.getValue();

            Assignment assignment = assignmentService.getAssignmentById(assignmentId);
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

    @ResponseBody
    @DeleteMapping()
    public ResponseEntity<String> deleteAssignmentById(@RequestBody int id) {
        Assignment assignment = assignmentService.getAssignmentById(id);
        if (assignment == null)
            return ResponseEntity.ofNullable("Данной работы не найдено!");
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok("Удаление успешно!");
    }
}
