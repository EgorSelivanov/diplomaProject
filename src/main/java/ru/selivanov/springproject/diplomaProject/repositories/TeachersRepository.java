package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Teacher;

import java.util.List;

@Repository
public interface TeachersRepository extends JpaRepository<Teacher, Integer> {
    List<Teacher> findByDepartmentLike(String department);
    List<Teacher> findByPositionLike(String position);
}
