package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Assignment;

import java.util.Date;
import java.util.List;

@Repository
public interface AssignmentsRepository extends JpaRepository<Assignment, Integer> {
    List<Assignment> findByTypeLike(String type);
    List<Assignment> findByDescription(String description);
    List<Assignment> findByDate(Date date);

}
