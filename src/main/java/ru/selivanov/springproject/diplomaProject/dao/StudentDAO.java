package ru.selivanov.springproject.diplomaProject.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.selivanov.springproject.diplomaProject.dto.GradesDTO;
import ru.selivanov.springproject.diplomaProject.dto.StudentScheduleDTO;
import ru.selivanov.springproject.diplomaProject.model.Subject;

import java.util.List;

@Component
public class StudentDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StudentScheduleDTO> getStudentScheduleData(int groupId) {
        return jdbcTemplate.query("SELECT schedule.day_of_week, schedule.start_time, subject.name, workload.type, schedule.audience, user_.first_name, user_.second_name, user_.patronymic, teacher.department\n" +
                        "FROM workload JOIN schedule ON workload.workload_id = schedule.workload_id\n" +
                        "JOIN subject ON workload.subject_id = subject.subject_id\n" +
                        "JOIN teacher ON workload.teacher_id = teacher.teacher_id\n" +
                        "JOIN user_ ON teacher.user_id = user_.user_id\n" +
                        "WHERE workload.group_id = ?\n" +
                        "ORDER BY CASE schedule.day_of_week\n" +
                        "    WHEN 'Понедельник' THEN 1\n" +
                        "    WHEN 'Вторник' THEN 2\n" +
                        "    WHEN 'Среда' THEN 3\n" +
                        "    WHEN 'Четверг' THEN 4\n" +
                        "    WHEN 'Пятница' THEN 5\n" +
                        "    WHEN 'Суббота' THEN 6\n" +
                        "    WHEN 'Воскресенье' THEN 7\n" +
                        "    ELSE 8\n" +
                        "END, schedule.start_time;", new Object[]{groupId},
                new BeanPropertyRowMapper<>(StudentScheduleDTO.class));
    }

    public List<Subject> getSubjectListByGroup(int groupId) {
        return jdbcTemplate.query("SELECT DISTINCT s.* FROM workload w JOIN subject s ON w.subject_id = s.subject_id \n" +
                "WHERE w.group_id = ? ORDER BY s.name", new Object[]{groupId}, new BeanPropertyRowMapper<>(Subject.class));
    }

    public List<GradesDTO> getGradesDTOListByGroup(int groupId, int subjectId, int studentId) {
        return jdbcTemplate.query("""
                SELECT assign.type, assign.description, assign.max_points, assign.date, COALESCE(gr.points, 0) as points
                FROM Assignment_ assign JOIN workload w ON assign.workload_id = w.workload_id\s
                LEFT JOIN grade gr ON assign.assignment_id = gr.assignment_id
                WHERE w.group_id = ? AND w.subject_id = ? AND gr.student_id = ? ORDER BY "date"
                """, new Object[]{groupId, subjectId, studentId}, new BeanPropertyRowMapper<>(GradesDTO.class));
    }
}
