package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Speciality;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupsRepository extends JpaRepository<Group, Integer> {
    Optional<Group> findByName(String name);
    List<Group> findBySpecialityLike(Speciality speciality);
    List<Group> findByCourseNumber(int courseNumber);
}
