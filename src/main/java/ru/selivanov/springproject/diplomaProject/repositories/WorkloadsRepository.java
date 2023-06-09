package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkloadsRepository extends JpaRepository<Workload, Integer> {
    List<Workload> findByTypeLike(String type);
    List<Workload> findAllByGroup(Group group);
    List<Workload> findAllByTeacherOrderBySubject_NameAscTypeAscGroup_NameAsc(Teacher teacher);
    Optional<Workload> findByTeacherAndSubjectAndGroupAndType(Teacher teacher, Subject subject, Group group, String type);
}
