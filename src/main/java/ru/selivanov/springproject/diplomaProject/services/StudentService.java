package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.StudentDAO;
import ru.selivanov.springproject.diplomaProject.dto.GradesDTO;
import ru.selivanov.springproject.diplomaProject.dto.StudentScheduleDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.StudentsRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentsRepository studentsRepository;
    private final StudentDAO studentDAO;

    @Autowired
    public StudentService(StudentsRepository studentsRepository, StudentDAO studentDAO) {
        this.studentsRepository = studentsRepository;
        this.studentDAO = studentDAO;
    }

    public Optional<Student> findByUser(User user) {
        return studentsRepository.findByUser(user);
    }

    @Transactional
    public void deleteStudent(int id) {
        studentsRepository.deleteById(id);
    }

    public User getUserByStudent(int id) {
        return studentsRepository.findById(id).map(Student::getUser).orElse(null);
    }

    public Group getGroupByStudent(int id) {
        return studentsRepository.findById(id).map(Student::getGroup).orElse(null);
    }

    public List<StudentScheduleDTO> getScheduleDataByStudent(int id) {
        Group group = this.getGroupByStudent(id);

        return studentDAO.getStudentScheduleData(group.getGroupId());

        /*List<StudentScheduleDTO> scheduleDTOS = new ArrayList<>();

        Hibernate.initialize(group.getWorkloadList());
        List<Workload> workloadList = group.getWorkloadList();

        for (Workload workload: workloadList) {
            Subject subject = workload.getSubject();
            Teacher teacher = workload.getTeacher();
            User user = teacher.getUser();

            for (Schedule schedule: workload.getScheduleList()) {
                scheduleDTOS.add(new StudentScheduleDTO(schedule.getAudience(), schedule.getDayOfWeek(), schedule.getStartTime(),
                        schedule.getEndTime(), subject.getName(), workload.getType(),
                        user.getSecondName() + " " + user.getFirstName() + " " + user.getPatronymic(),
                        teacher.getDepartment()));
            }
        }

        return scheduleDTOS;*/
    }

    public List<Subject> getSubjectListByStudent(int id) {
        Group group = this.getGroupByStudent(id);

        return studentDAO.getSubjectListByGroup(group.getGroupId());
    }

    public List<GradesDTO> getGradesListByStudent(int id, int subjectId) {
        Group group = this.getGroupByStudent(id);

        return studentDAO.getGradesDTOListByGroup(group.getGroupId(), subjectId, id);
    }

    @Transactional
    public void assignUser(int id, User selectedUser) {
        studentsRepository.findById(id).ifPresent(
                student -> {
                    student.setUser(selectedUser);
                }
        );
    }

    @Transactional
    public void assignGroup(int id, Group selectedGroup) {
        studentsRepository.findById(id).ifPresent(
                student -> {
                    student.setGroup(selectedGroup);
                }
        );
    }

    public List<Grade> getGradesByStudent(int id) {
        Optional<Student> studentOptional = studentsRepository.findById(id);

        if (studentOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(studentOptional.get().getGradeList());

        return studentOptional.get().getGradeList();
    }

    public List<Attendance> getAttendanceListByStudent(int id) {
        Optional<Student> studentOptional = studentsRepository.findById(id);

        if (studentOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(studentOptional.get().getAttendanceList());

        return studentOptional.get().getAttendanceList();
    }
}