package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Workload;

import java.util.List;

@Repository
public interface WorkloadsRepository extends JpaRepository<Workload, Integer> {
    List<Workload> findByTypeLike(String type);


}
