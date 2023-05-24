package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Speciality;
import ru.selivanov.springproject.diplomaProject.util.SpecialityValidator;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends JpaRepository<Group, Integer> {
    List<Group> findAllByOrderByCourseNumberAscNameAsc();
    List<Group> findAllByCourseNumberOrderByCourseNumberAscNameAsc(int courseNumber);
    List<Group> findAllByNameLikeIgnoreCase(String name);
    List<Group> findAllByCourseNumber(int courseNumber);
    List<Group> findAllByCourseNumberAndNameLikeIgnoreCaseOrderByCourseNumberAscNameAsc(int courseNumber, String name);
    List<Group> findAllBySpeciality(Speciality speciality);
    List<Group> findAllByCourseNumberAndSpeciality(int courseNumber, Speciality speciality);
    Optional<Group> findByName(String name);
    List<Group> findBySpecialityLike(Speciality speciality);
    List<Group> findByCourseNumber(int courseNumber);
}
