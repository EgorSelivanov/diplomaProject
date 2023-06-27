package ru.selivanov.springproject.diplomaProject.services;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.ScheduleDAO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleJSONDTO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleToShowDTO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleWorkloadDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ScheduleService {
    private final SchedulesRepository schedulesRepository;
    private final UsersRepository usersRepository;
    private final GroupsRepository groupsRepository;
    private final SubjectsRepository subjectsRepository;
    private final WorkloadsRepository workloadsRepository;
    private final WorkloadService workloadService;
    private final ScheduleDAO scheduleDAO;

    @Autowired
    public ScheduleService(SchedulesRepository schedulesRepository, UsersRepository usersRepository, GroupsRepository groupsRepository, SubjectsRepository subjectsRepository, WorkloadsRepository workloadsRepository, WorkloadService workloadService, ScheduleDAO scheduleDAO) {
        this.schedulesRepository = schedulesRepository;
        this.usersRepository = usersRepository;
        this.groupsRepository = groupsRepository;
        this.subjectsRepository = subjectsRepository;
        this.workloadsRepository = workloadsRepository;
        this.workloadService = workloadService;
        this.scheduleDAO = scheduleDAO;
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
    public void createNewSchedule(Schedule schedule) {
        schedulesRepository.save(schedule);
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

    public List<ScheduleWorkloadDTO> getWorkloadList() {
        return scheduleDAO.getWorkloadData();
    }

    public List<ScheduleWorkloadDTO> getWorkloadListBySearch(String search) {
        search = "%" + search + "%";
        return scheduleDAO.getWorkloadDataSearch(search);
    }

    public List<ScheduleWorkloadDTO> getWorkloadListBySearchTeacher(String search) {
        return scheduleDAO.getWorkloadDataSearchByTeacher(search);
    }

    public List<ScheduleToShowDTO> getScheduleListByWorkload(int workloadId) {
        return scheduleDAO.getScheduleListByWorkload(workloadId);
    }

    public Schedule getScheduleById(int id) {
        return schedulesRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateScheduleList(List<Schedule> list) {
        schedulesRepository.saveAll(list);
    }

    @Transactional
    public void updateDataByJSON(@Valid List<ScheduleJSONDTO> studentJSONDTOList) throws NoSuchFieldException {
        List<Schedule> scheduleList = new ArrayList<>();
        List<Workload> workloadList = new ArrayList<>();

        for (ScheduleJSONDTO scheduleJSONDTO : studentJSONDTOList) {
            User user = usersRepository.findByUsername(scheduleJSONDTO.getUsername()).orElse(null);
            if (user == null)
                throw new NoSuchFieldException("Не найдено пользователя с именем: " + scheduleJSONDTO.getUsername());

            Teacher teacher = user.getTeacher();
            if (teacher == null)
                throw new NoSuchFieldException("Пользователь с именем: " + scheduleJSONDTO.getUsername() + " не является преподавателем!");

            Group group = groupsRepository.findByName(scheduleJSONDTO.getGroupName()).orElse(null);
            if (group == null)
                throw new NoSuchFieldException("Не найдено группы с именем: " + scheduleJSONDTO.getGroupName());

            List<Subject> subjectList = subjectsRepository.findByNameLike(scheduleJSONDTO.getSubjectName());
            if (subjectList.size() == 0)
                throw new NoSuchFieldException("Не найдено дисциплины с названием: " + scheduleJSONDTO.getSubjectName());

            Subject subject = subjectList.get(0);

            Workload workload = workloadsRepository.findByTeacherAndSubjectAndGroupAndType(teacher, subject, group, scheduleJSONDTO.getType()).orElse(null);
            if (workload == null) {
                workload = new Workload();
                workload.setType(scheduleJSONDTO.getType());
                workload.setSubject(subject);
                workload.setGroup(group);
                workload.setTeacher(teacher);
            }

            Schedule checkSchedule = schedulesRepository.findByAudienceAndBuildingAndDayOfWeekAndStartTime(scheduleJSONDTO.getAudience(),
                    scheduleJSONDTO.getBuilding(), scheduleJSONDTO.getDayOfWeek(), scheduleJSONDTO.getStartTimeFormat());
            if (checkSchedule != null)
                throw new NoSuchFieldException("Для данного занятия аудитория уже занята: " + scheduleJSONDTO.getSubjectName() + " " +
                        scheduleJSONDTO.getUsername() + " " + scheduleJSONDTO.getAudience() + " " + scheduleJSONDTO.getBuilding() + " " +
                        scheduleJSONDTO.getDayOfWeek() + " " + scheduleJSONDTO.getStartTime());

            Schedule schedule = new Schedule();
            schedule.setWorkload(workload);
            schedule.setRepeat(scheduleJSONDTO.getRepeat());
            schedule.setBuilding(scheduleJSONDTO.getBuilding());
            schedule.setAudience(scheduleJSONDTO.getAudience());
            schedule.setDayOfWeek(scheduleJSONDTO.getDayOfWeek());
            schedule.setStartTime(scheduleJSONDTO.getStartTimeFormat());
            schedule.setEndTime(scheduleJSONDTO.getEndTimeFormat());
            workloadList.add(workload);
            scheduleList.add(schedule);
        }

        workloadsRepository.saveAll(workloadList);
        schedulesRepository.saveAll(scheduleList);

        //Создать посещаемости
        for (Schedule schedule : scheduleList) {
            workloadService.createStudentsAttendancesForNewSchedule(schedule);
        }
    }
}
