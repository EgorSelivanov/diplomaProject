package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Assignment;
import ru.selivanov.springproject.diplomaProject.model.Grade;
import ru.selivanov.springproject.diplomaProject.model.Student;

import java.util.Optional;

@Repository
public interface GradesRepository extends JpaRepository<Grade, Integer> {
    Optional<Grade> findByStudentAndAssignment(Student student, Assignment assignment);
}
