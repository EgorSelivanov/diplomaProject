package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Speciality;

import java.util.Optional;

@Repository
public interface SpecialitiesRepository extends JpaRepository<Speciality, Integer> {
    Optional<Speciality> findByNameLike(String name);
    Optional<Speciality> findByCodeLike(String code);
}
