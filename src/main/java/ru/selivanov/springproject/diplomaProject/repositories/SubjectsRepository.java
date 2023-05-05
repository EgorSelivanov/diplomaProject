package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Subject;

import java.util.List;

@Repository
public interface SubjectsRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByNameLike(String name);
    List<Subject> findByDescriptionLike(String description);
}
