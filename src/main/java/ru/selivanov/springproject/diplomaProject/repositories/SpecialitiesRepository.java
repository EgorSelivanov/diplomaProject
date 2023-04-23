package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Speciality;

@Repository
public interface SpecialitiesRepository extends JpaRepository<Speciality, Integer> {
}
