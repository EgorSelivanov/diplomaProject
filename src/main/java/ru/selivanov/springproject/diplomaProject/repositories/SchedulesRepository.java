package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Schedule;

import java.util.Date;
import java.util.List;

@Repository
public interface SchedulesRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByAudienceAndDayOfWeekAndStartTime(String audience, String dayOfWeek, Date startTime);
    List<Schedule> findByAudience(String audience);
    List<Schedule> findByDayOfWeek(String dayOfWeek);

}
