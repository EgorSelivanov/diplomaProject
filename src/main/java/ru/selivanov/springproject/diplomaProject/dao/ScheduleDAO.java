package ru.selivanov.springproject.diplomaProject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleToShowDTO;
import ru.selivanov.springproject.diplomaProject.dto.ScheduleWorkloadDTO;

import java.util.List;

@Component
public class ScheduleDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ScheduleDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ScheduleWorkloadDTO> getWorkloadData() {
        return jdbcTemplate.query("""
                    SELECT workload.workload_id, subject.name as subject_name, subject.description as subject_description, workload.type, user_.second_name, user_.first_name, user_.patronymic,
                     group_.name as group_name, group_.course_number, speciality.speciality_name FROM workload
                     JOIN teacher ON workload.teacher_id = teacher.teacher_id
                     JOIN user_ ON user_.user_id = teacher.user_id
                     JOIN subject ON workload.subject_id = subject.subject_id
                     JOIN group_ ON workload.group_id = group_.group_id
                     JOIN speciality ON speciality.speciality_id = group_.speciality_id
                     ORDER BY user_.second_name, user_.first_name, user_.patronymic, subject.name, workload.type
                    """, new Object[]{},
                new BeanPropertyRowMapper<>(ScheduleWorkloadDTO.class));
    }

    public List<ScheduleWorkloadDTO> getWorkloadDataSearch(String search) {
        return jdbcTemplate.query("""
                    SELECT workload.workload_id, subject.name as subject_name, subject.description as subject_description, workload.type, user_.second_name, user_.first_name, user_.patronymic,
                    group_.name as group_name, group_.course_number, speciality.speciality_name FROM workload
                    JOIN teacher ON workload.teacher_id = teacher.teacher_id
                    JOIN user_ ON user_.user_id = teacher.user_id
                    JOIN subject ON workload.subject_id = subject.subject_id
                    JOIN group_ ON workload.group_id = group_.group_id
                    JOIN speciality ON speciality.speciality_id = group_.speciality_id
                    WHERE subject.name ILIKE ? OR workload.type ILIKE ? OR group_.name ILIKE ? OR speciality.speciality_name ILIKE ?
                    ORDER BY user_.second_name, user_.first_name, user_.patronymic, subject.name, workload.type
                    """, new Object[]{search, search, search, search},
                new BeanPropertyRowMapper<>(ScheduleWorkloadDTO.class));
    }

    public List<ScheduleWorkloadDTO> getWorkloadDataSearchByTeacher(String search) {
        String[] fio = search.trim().split(" ");
        String secondName = "%" + fio[0] + "%";
        String firstName = "%%";
        String patronymic = "%%";
        if (fio.length == 3) {
            firstName = "%" + fio[1] + "%";
            patronymic = "%" + fio[2] + "%";
        } else if (fio.length == 2)
            secondName = "%" + fio[1] + "%";

        return jdbcTemplate.query("""
                    SELECT workload.workload_id, subject.name as subject_name, subject.description as subject_description, workload.type, user_.second_name, user_.first_name, user_.patronymic,
                    group_.name as group_name, group_.course_number, speciality.speciality_name FROM workload
                    JOIN teacher ON workload.teacher_id = teacher.teacher_id
                    JOIN user_ ON user_.user_id = teacher.user_id
                    JOIN subject ON workload.subject_id = subject.subject_id
                    JOIN group_ ON workload.group_id = group_.group_id
                    JOIN speciality ON speciality.speciality_id = group_.speciality_id
                    WHERE user_.second_name ILIKE ? AND user_.first_name ILIKE ? AND user_.patronymic ILIKE ?
                    ORDER BY user_.second_name, user_.first_name, user_.patronymic, subject.name, workload.type
                    """, new Object[]{secondName, firstName, patronymic},
                new BeanPropertyRowMapper<>(ScheduleWorkloadDTO.class));
    }

    public List<ScheduleToShowDTO> getScheduleListByWorkload(int workloadId) {
        return jdbcTemplate.query("""
                    SELECT schedule.schedule_id, subject.name as subject_name, workload.type, user_.second_name, user_.first_name, user_.patronymic,
                   group_.name as group_name, schedule.audience, schedule.building, schedule.day_of_week, schedule.repeat, schedule.start_time, schedule.end_time
                   FROM workload
                   JOIN schedule ON schedule.workload_id = workload.workload_id
                   JOIN teacher ON workload.teacher_id = teacher.teacher_id
                   JOIN user_ ON user_.user_id = teacher.user_id
                   JOIN subject ON workload.subject_id = subject.subject_id
                   JOIN group_ ON workload.group_id = group_.group_id
                   WHERE workload.workload_id = ?
                   ORDER BY user_.second_name, user_.first_name, user_.patronymic, subject.name, workload.type, schedule.day_of_week, schedule.start_time
                    """, new Object[]{workloadId},
                new BeanPropertyRowMapper<>(ScheduleToShowDTO.class));
    }
}
