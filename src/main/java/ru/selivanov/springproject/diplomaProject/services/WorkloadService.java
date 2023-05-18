package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dto.NewScheduleTeacherDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class WorkloadService {
    private final WorkloadsRepository workloadsRepository;
    private final SchedulesRepository schedulesRepository;
    private final GroupsRepository groupsRepository;
    private final TeachersRepository teachersRepository;
    private final SubjectsRepository subjectsRepository;
    private final AttendancesRepository attendancesRepository;

    @Autowired
    public WorkloadService(WorkloadsRepository workloadsRepository, SchedulesRepository schedulesRepository,
                           GroupsRepository groupsRepository, TeachersRepository teachersRepository,
                           SubjectsRepository subjectsRepository, AttendancesRepository attendancesRepository) {
        this.workloadsRepository = workloadsRepository;
        this.schedulesRepository = schedulesRepository;
        this.groupsRepository = groupsRepository;
        this.teachersRepository = teachersRepository;
        this.subjectsRepository = subjectsRepository;
        this.attendancesRepository = attendancesRepository;
    }

    public List<Workload> findByType(String type) {
        return workloadsRepository.findByTypeLike(type);
    }

    @Transactional
    public boolean updateWorkload(int id, Workload updatedWorkload) {
        Optional<Workload> workloadOptional = workloadsRepository.findById(id);

        if (workloadOptional.isEmpty())
            return false;

        Workload workloadToBeUpdated = workloadOptional.get();

        workloadToBeUpdated.setType(updatedWorkload.getType());
        workloadsRepository.save(workloadToBeUpdated);
        return true;
    }

    @Transactional
    public void deleteWorkload(int id) {
        workloadsRepository.deleteById(id);
    }

    public Teacher getTeacherByWorkload(int id) {
        return workloadsRepository.findById(id).map(Workload::getTeacher).orElse(null);
    }

    public Subject getSubjectByWorkload(int id) {
        return workloadsRepository.findById(id).map(Workload::getSubject).orElse(null);
    }

    public Group getGroupByWorkload(int id) {
        return workloadsRepository.findById(id).map(Workload::getGroup).orElse(null);
    }

    @Transactional
    public void assignTeacher(int id, Teacher selectedTeacher) {
        workloadsRepository.findById(id).ifPresent(
                workload -> {
                    workload.setTeacher(selectedTeacher);
                }
        );
    }

    @Transactional
    public void assignSubject(int id, Subject selectedSubject) {
        workloadsRepository.findById(id).ifPresent(
                workload -> {
                    workload.setSubject(selectedSubject);
                }
        );
    }

    @Transactional
    public void assignGroup(int id, Group selectedGroup) {
        workloadsRepository.findById(id).ifPresent(
                workload -> {
                    workload.setGroup(selectedGroup);
                }
        );
    }

    public List<Schedule> getScheduleListByWorkload(int id) {
        Optional<Workload> workloadOptional = workloadsRepository.findById(id);

        if (workloadOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(workloadOptional.get().getScheduleList());

        return workloadOptional.get().getScheduleList();
    }

    public List<Assignment> getAssignmentListByWorkload(int id) {
        Optional<Workload> workloadOptional = workloadsRepository.findById(id);

        if (workloadOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(workloadOptional.get().getAssignmentList());

        return workloadOptional.get().getAssignmentList();
    }

    public Workload getWorkloadByTeacherSubjectGroupType(int teacherId, int subjectId, int groupId, String type) {
        Teacher teacher = teachersRepository.findById(teacherId).orElse(null);
        Subject subject = subjectsRepository.findById(subjectId).orElse(null);
        Group group = groupsRepository.findById(groupId).orElse(null);
        if (teacher == null || subject == null || group == null || type == null)
            return null;
        return workloadsRepository.findByTeacherAndSubjectAndGroupAndType(teacher, subject, group, type).orElse(null);
    }

    @Transactional
    public void createNewTeacherSchedule(NewScheduleTeacherDTO newScheduleTeacherDTO) {
        for (int i = 0; i < newScheduleTeacherDTO.getGroupsId().size(); i++) {
            int teacherId = newScheduleTeacherDTO.getTeacherId();
            int subjectId = newScheduleTeacherDTO.getSubjectId();
            int groupId = newScheduleTeacherDTO.getGroupsId().get(i);
            String type = newScheduleTeacherDTO.getType();

            Workload workload = getWorkloadByTeacherSubjectGroupType(teacherId, subjectId, groupId, type);
            if (workload == null) {
                workload = new Workload();
                workload.setGroup(groupsRepository.findById(groupId).get());
                workload.setTeacher(teachersRepository.findById(teacherId).get());
                workload.setSubject(subjectsRepository.findById(subjectId).get());
                workload.setType(type);
            }
            workloadsRepository.save(workload);

            Schedule schedule = new Schedule();
            schedule.setWorkload(workload);
            schedule.setAudience(newScheduleTeacherDTO.getAudience());
            schedule.setStartTime(newScheduleTeacherDTO.getStartTime());
            schedule.setEndTime(newScheduleTeacherDTO.getEndTime());
            schedule.setDayOfWeek(newScheduleTeacherDTO.getDayOfWeek());
            schedulesRepository.save(schedule);

            Group group = groupsRepository.findById(newScheduleTeacherDTO.getGroupsId().get(i)).get();
            Hibernate.initialize(group.getStudents());
            List<Student> studentList = group.getStudents();
            List<Date> dateList = processDates(newScheduleTeacherDTO.getDayOfWeek(), newScheduleTeacherDTO.getRepeat());

            //Создаем посещаемости
            List<Attendance> attendanceList = new ArrayList<>();
            for (Student student: studentList) {
                for (Date date : dateList) {
                    attendanceList.add(new Attendance(student, schedule, 0, date));
                }
            }

            attendancesRepository.saveAll(attendanceList);
        }
    }

    private List<Date> processDates(String dayOfWeek, String repeat) {
        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();

        // Получаем день недели, указанный пользователем
        DayOfWeek userSelectedDayOfWeek = processDayOfWeek(dayOfWeek);

        // Находим ближайший день недели, начиная с текущей даты
        LocalDate startDay = currentDate.with(TemporalAdjusters.next(userSelectedDayOfWeek));

        // Определение номера недели текущей даты
        int currentWeekNumber = startDay.get(WeekFields.ISO.weekOfWeekBasedYear()) + 1;

        int repeatable = 0;
        switch (repeat) {
            case "Еженедельно", "Неч./Чет.", "Чет./Неч." -> repeatable = 1;
            case "Чет." -> {
                repeatable = 2;
                if (currentWeekNumber % 2 == 0)
                    startDay = startDay.plusWeeks(1);
            }
            case "Неч." -> {
                repeatable = 2;
                if (currentWeekNumber % 2 != 0)
                    startDay = startDay.plusWeeks(1);
            }
            case "Одно занятие" -> repeatable = 0;
            default -> throw new RuntimeException("Неверный формат повтора занятия!");
        }

        if (repeatable == 0)
            return Collections.singletonList(Date.from(startDay.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        List<Date> dateList = new ArrayList<>();

        LocalDate nextDate = startDay;
        LocalDate endDate = getEndDate();

        while (!nextDate.isAfter(endDate)) {
            dateList.add(Date.from(nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            nextDate = nextDate.plusWeeks(repeatable);
        }

        return dateList;
    }

    private DayOfWeek processDayOfWeek(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "Понедельник" -> DayOfWeek.MONDAY;
            case "Вторник" -> DayOfWeek.TUESDAY;
            case "Среда" -> DayOfWeek.WEDNESDAY;
            case "Четверг" -> DayOfWeek.THURSDAY;
            case "Пятница" -> DayOfWeek.FRIDAY;
            case "Суббота" -> DayOfWeek.SATURDAY;
            case "Воскресенье" -> DayOfWeek.SUNDAY;
            default -> throw new RuntimeException("Неверный формат дня недели!");
        };
    }

    private LocalDate getEndDate() {
        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();
        // Даты для сравнения
        LocalDate date1 = LocalDate.of(currentDate.getYear(), 7, 1); // 01.07.23
        LocalDate date2 = LocalDate.of(currentDate.getYear() + 1, 7, 1); // 01.07.24
        LocalDate date3 = LocalDate.of(currentDate.getYear(), 2, 1); // 01.02.23
        LocalDate date4 = LocalDate.of(currentDate.getYear() + 1, 2, 1); // 01.02.24

        //01.02.23 -> 01.07.23 -> 01.02.24 -> 01.07.24
        //date3    -> date1    -> date4    -> date2

        if (currentDate.isAfter(date1) && currentDate.isBefore(date4))
            return date4;
        else if (currentDate.isAfter(date3) && currentDate.isBefore(date1))
            return date1;
        else if (currentDate.isAfter(date4) && currentDate.isBefore(date2))
            return date2;
        else if (currentDate.isBefore(date3))
            return date3;
        else
            return currentDate;
    }
}
