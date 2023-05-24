package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Speciality;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialitiesRepository extends JpaRepository<Speciality, Integer> {
    List<Speciality> findAllByOrderByCode();
    List<Speciality> findAllBySpecialityNameLikeIgnoreCaseOrderByCode(String name);
    List<Speciality> findAllByCodeLikeIgnoreCaseOrderByCode(String code);
    Optional<Speciality> findBySpecialityName(String name);
    Optional<Speciality> findByCode(String code);
}
