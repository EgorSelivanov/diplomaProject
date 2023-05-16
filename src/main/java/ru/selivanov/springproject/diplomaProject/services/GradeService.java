package ru.selivanov.springproject.diplomaProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Grade;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.repositories.AssignmentsRepository;
import ru.selivanov.springproject.diplomaProject.repositories.GradesRepository;
import ru.selivanov.springproject.diplomaProject.repositories.StudentsRepository;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GradeService {
    private final GradesRepository gradesRepository;
    private final StudentsRepository studentsRepository;
    private final AssignmentsRepository assignmentsRepository;

    @Autowired
    public GradeService(GradesRepository gradesRepository, StudentsRepository studentsRepository, AssignmentsRepository assignmentsRepository) {
        this.gradesRepository = gradesRepository;
        this.studentsRepository = studentsRepository;
        this.assignmentsRepository = assignmentsRepository;
    }

    @Transactional
    public boolean updateGrade(int id, Grade updatedGrade) {
        Optional<Grade> gradeOptional = gradesRepository.findById(id);

        if (gradeOptional.isEmpty())
            return false;

        Grade gradeToBeUpdated = gradeOptional.get();

        updatedGrade.setGradeId(id);
        updatedGrade.setAssignment(gradeToBeUpdated.getAssignment());
        updatedGrade.setStudent(gradeToBeUpdated.getStudent());
        gradesRepository.save(updatedGrade);
        return true;
    }

    @Transactional
    public void saveGrade(Grade grade) {
        gradesRepository.save(grade);
    }
    @Transactional
    public void deleteGrade(int id) {
        gradesRepository.deleteById(id);
    }

    public Student getStudentByGrade(int id) {
        return gradesRepository.findById(id).map(Grade::getStudent).orElse(null);
    }

    public Assignment getAssignmentByGrade(int id) {
        return gradesRepository.findById(id).map(Grade::getAssignment).orElse(null);
    }

    @Transactional
    public void assignStudent(int id, Student selectedStudent) {
        gradesRepository.findById(id).ifPresent(
                grade -> {
                    grade.setStudent(selectedStudent);
                }
        );
    }

    @Transactional
    public void assignAssignment(int id, Assignment selectedAssignment) {
        gradesRepository.findById(id).ifPresent(
                grade -> {
                    grade.setAssignment(selectedAssignment);
                }
        );
    }

    public Grade getGradeByStudentAndAssignment(int studentId, int assignmentId) {
        Student student = studentsRepository.findById(studentId).orElse(null);
        Assignment assignment = assignmentsRepository.findById(assignmentId).orElse(null);
        if (student == null || assignment == null)
            return null;

        return gradesRepository.findByStudentAndAssignment(student, assignment).orElse(null);
    }
}
