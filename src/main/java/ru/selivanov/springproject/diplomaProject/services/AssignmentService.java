package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Grade;
import ru.selivanov.springproject.diplomaProject.model.Workload;
import ru.selivanov.springproject.diplomaProject.repositories.AssignmentsRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AssignmentService {
    private final AssignmentsRepository assignmentsRepository;

    @Autowired
    public AssignmentService(AssignmentsRepository assignmentsRepository) {
        this.assignmentsRepository = assignmentsRepository;
    }

    public List<Assignment> findByType(String type) {
        return assignmentsRepository.findByTypeLike(type);
    }

    public List<Assignment> findByDescription(String description) {
        return assignmentsRepository.findByDescription(description);
    }

    public List<Assignment> findByDate(Date date) {
        return assignmentsRepository.findByDate(date);
    }

    @Transactional
    public boolean updateAssignment(int id, Assignment updatedAssignment) {
        Optional<Assignment> assignmentById = assignmentsRepository.findById(id);

        if (assignmentById.isEmpty())
            return false;

        Assignment assignmentToBeUpdated = assignmentById.get();

        Hibernate.initialize(assignmentToBeUpdated.getGradeList());
        updatedAssignment.setAssignmentId(id);
        updatedAssignment.setGradeList(assignmentToBeUpdated.getGradeList());
        updatedAssignment.setWorkload(assignmentToBeUpdated.getWorkload());
        assignmentsRepository.save(updatedAssignment);
        return true;
    }

    @Transactional
    public void deleteAssignment(int id) {
        assignmentsRepository.deleteById(id);
    }

    public Workload getWorkloadByAssignment(int id) {
        return assignmentsRepository.findById(id).map(Assignment::getWorkload).orElse(null);
    }

    public List<Grade> getGradesByAssignment(int id) {
        Optional<Assignment> assignmentOptional = assignmentsRepository.findById(id);

        if (assignmentOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(assignmentOptional.get().getGradeList());

        return assignmentOptional.get().getGradeList();
    }

    @Transactional
    public void assignWorkload(int id, Workload selectedWorkload) {
        assignmentsRepository.findById(id).ifPresent(
                assignment -> {
                    assignment.setWorkload(selectedWorkload);
                }
        );
    }
}
