package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Subject;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectsRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findAllByOrderByName();
    List<Subject> findAllByNameLikeIgnoreCaseOrderByName(String name);
    List<Subject> findAllByDescriptionLikeIgnoreCaseOrderByName(String description);
    List<Subject> findByNameLike(String name);
    List<Subject> findByDescriptionLike(String description);
    Optional<Subject> findByNameAndDescription(String name, String description);
}
