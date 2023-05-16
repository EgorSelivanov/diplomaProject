package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.SchedulesRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
    private final SchedulesRepository schedulesRepository;

    @Autowired
    public ScheduleService(SchedulesRepository schedulesRepository) {
        this.schedulesRepository = schedulesRepository;
    }

    public List<Schedule> findByAudienceAndDayOfWeekAndStartTime(String audience, String dayOfWeek, Date startTime) {
        return schedulesRepository.findByAudienceAndDayOfWeekAndStartTime(audience, dayOfWeek, startTime);
    }

    public List<Schedule> findByAudience(String audience) {
        return schedulesRepository.findByAudience(audience);
    }

    public List<Schedule> findByDayOfWeek(String dayOfWeek) {
        return schedulesRepository.findByDayOfWeek(dayOfWeek);
    }

    @Transactional
    public boolean updateSchedule(int id, Schedule updatedSchedule) {
        Optional<Schedule> scheduleOptional = schedulesRepository.findById(id);

        if (scheduleOptional.isEmpty())
            return false;

        Schedule scheduleToBeUpdated = scheduleOptional.get();

        Hibernate.initialize(scheduleToBeUpdated.getAttendanceList());
        updatedSchedule.setScheduleId(id);
        updatedSchedule.setWorkload(scheduleToBeUpdated.getWorkload());
        updatedSchedule.setAttendanceList(scheduleToBeUpdated.getAttendanceList());
        schedulesRepository.save(updatedSchedule);
        return true;
    }

    @Transactional
    public void deleteSchedule(int id) {
        schedulesRepository.deleteById(id);
    }

    public Workload getWorkloadBySchedule(int id) {
        return schedulesRepository.findById(id).map(Schedule::getWorkload).orElse(null);
    }

    @Transactional
    public void assignWorkload(int id, Workload selectedWorkload) {
        schedulesRepository.findById(id).ifPresent(
                schedule -> {
                    schedule.setWorkload(selectedWorkload);
                }
        );
    }

    public List<Attendance> getAttendanceListByGroup(int id) {
        Optional<Schedule> scheduleOptional = schedulesRepository.findById(id);

        if (scheduleOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(scheduleOptional.get().getAttendanceList());

        return scheduleOptional.get().getAttendanceList();
    }
}
