package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.dto.StudentScheduleDTO;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser(User user);
}
