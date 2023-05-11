package ru.selivanov.springproject.diplomaProject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.selivanov.springproject.diplomaProject.dto.TeacherScheduleDTO;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Subject;

import java.util.List;

@Component
public class TeacherDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TeacherDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TeacherScheduleDTO> getTeacherScheduleData(int id) {
        return jdbcTemplate.query("""
                SELECT schedule.audience, schedule.day_of_week, schedule.start_time, schedule.end_time, subject.name, workload.type, group_.name as group_name, group_.course_number
                FROM workload JOIN schedule ON workload.workload_id = schedule.workload_id
                JOIN subject ON workload.subject_id = subject.subject_id
                JOIN group_ ON workload.group_id = group_.group_id
                WHERE workload.teacher_id = ?
                ORDER BY CASE schedule.day_of_week
                    WHEN 'Понедельник' THEN 1
                    WHEN 'Вторник' THEN 2
                    WHEN 'Среда' THEN 3
                    WHEN 'Четверг' THEN 4
                    WHEN 'Пятница' THEN 5
                    WHEN 'Суббота' THEN 6
                    WHEN 'Воскресенье' THEN 7
                    ELSE 8
                END, schedule.start_time
                """, new Object[]{id},
                new BeanPropertyRowMapper<>(TeacherScheduleDTO.class));
    }

    public List<Subject> getSubjectListByTeacherId(int id) {
        return jdbcTemplate.query("""
                SELECT DISTINCT subject.* FROM workload JOIN subject ON workload.subject_id = subject.subject_id
                WHERE workload.teacher_id = ?
                """, new Object[]{id},
                new BeanPropertyRowMapper<>(Subject.class));
    }

    public List<Group> getGroupListByTeacherId(int id) {
        return jdbcTemplate.query("""
                SELECT DISTINCT group_.* FROM workload JOIN group_ on workload.group_id = group_.group_id
                WHERE workload.teacher_id = ?
                """, new Object[]{id},
                new BeanPropertyRowMapper<>(Group.class));
    }
}
