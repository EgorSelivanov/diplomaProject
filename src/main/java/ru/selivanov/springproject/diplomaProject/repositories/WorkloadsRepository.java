package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Workload;

@Repository
public interface WorkloadsRepository extends JpaRepository<Workload, Integer> {
}
