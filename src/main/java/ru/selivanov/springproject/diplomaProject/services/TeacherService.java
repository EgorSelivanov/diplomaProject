package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.TeacherDAO;
import ru.selivanov.springproject.diplomaProject.dto.*;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.TeachersRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class TeacherService {
    private final TeachersRepository teachersRepository;
    private final TeacherDAO teacherDAO;
    @Autowired
    public TeacherService(TeachersRepository teachersRepository, TeacherDAO teacherDAO) {
        this.teachersRepository = teachersRepository;
        this.teacherDAO = teacherDAO;
    }

    public List<Teacher> findByDepartment(String department) {
        return teachersRepository.findByDepartmentLike(department);
    }

    public List<Teacher> findByPosition(String position) {
        return teachersRepository.findByPositionLike(position);
    }

    @Transactional
    public boolean updateTeacher(int id, Teacher updatedTeacher) {
        Optional<Teacher> teacherOptional = teachersRepository.findById(id);

        if (teacherOptional.isEmpty())
            return false;

        Teacher teacherToBeUpdated = teacherOptional.get();

        Hibernate.initialize(teacherToBeUpdated.getWorkloadList());
        updatedTeacher.setTeacherId(id);
        updatedTeacher.setUser(teacherToBeUpdated.getUser());
        updatedTeacher.setWorkloadList(teacherToBeUpdated.getWorkloadList());
        teachersRepository.save(updatedTeacher);
        return true;
    }

    @Transactional
    public void deleteTeacher(int id) {
        teachersRepository.deleteById(id);
    }

    public User getUserByTeacher(int id) {
        return teachersRepository.findById(id).map(Teacher::getUser).orElse(null);
    }

    @Transactional
    public void assignUser(int id, User selectedUser) {
        teachersRepository.findById(id).ifPresent(
                teacher -> {
                    teacher.setUser(selectedUser);
                }
        );
    }

    public List<Workload> getWorkloadListByTeacher(int id) {
        Optional<Teacher> teacherOptional = teachersRepository.findById(id);

        if (teacherOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(teacherOptional.get().getWorkloadList());

        return teacherOptional.get().getWorkloadList();
    }

    public List<TeacherScheduleDTO> getScheduleDataByTeacher(int id, Date date) {
        return teacherDAO.getTeacherScheduleData(id, date);
    }

    public List<Subject> getSubjectListByTeacher(int id) {
        List<Subject> list = teacherDAO.getSubjectListByTeacherId(id);
        System.out.println(list.get(0).getSubjectId());
        return teacherDAO.getSubjectListByTeacherId(id);
    }

    public List<Group> getGroupListByTeacher(int id) {
        return teacherDAO.getGroupListByTeacherId(id);
    }

    public List<Group> getGroupListByTeacherAndSubject(int id, int subjectId) {
        return teacherDAO.getGroupListByTeacherIdAndSubjectId(id, subjectId);
    }

    public List<Group> getGroupListByTeacherAndSubjectAndType(int id, int subjectId, String type) {
        return teacherDAO.getGroupListByTeacherIdAndSubjectIdAndType(id, subjectId, type);
    }

    public List<AttendanceToShowDTO> getAttendanceList(int teacherId, int subjectId, int groupId, String type){
        List<AttendanceOfStudentsDTO> list = teacherDAO.getAttendanceList(teacherId, subjectId, groupId, type);
        List<AttendanceToShowDTO> finalList = new ArrayList<>();
        Set<String> fio = new HashSet<>();

        AttendanceToShowDTO attendance = null;
        String currentStudent = "";
        for (AttendanceOfStudentsDTO dto : list) {
            if (!currentStudent.equals(dto.getFullName())) {
                if (attendance != null) {
                    finalList.add(attendance);
                    fio.add(attendance.getFullName());
                }
                attendance = new AttendanceToShowDTO();
                attendance.setFullName(dto.getFullName());
                currentStudent = dto.getFullName();
            }
            attendance.addDate(dto.getDateFormat());
            attendance.addPresent(dto.getPresent());
            attendance.addAttendanceId(dto.getAttendanceId());
        }
        if (attendance != null && !fio.contains(attendance.getFullName()))
            finalList.add(attendance);

        return finalList;
    }

    public List<GradesOfStudentsToShowDTO> getGradesList(int teacherId, int subjectId, int groupId, String type) {
        List<GradesOfStudentsOfGroupDTO> list = teacherDAO.getGradesList(teacherId, subjectId, groupId, type);
        List<GradesOfStudentsToShowDTO> finalList = new ArrayList<>();
        Set<Integer> fio = new HashSet<>();

        GradesOfStudentsToShowDTO grade = null;
        int currentStudent = -1;
        for (GradesOfStudentsOfGroupDTO dto : list) {
            if (currentStudent != dto.getStudentId()) {
                if (grade != null) {
                    finalList.add(grade);
                    fio.add(grade.getStudentId());
                }
                grade = new GradesOfStudentsToShowDTO();
                grade.setFullName(dto.getFullName());
                grade.setStudentId(dto.getStudentId());
                currentStudent = dto.getStudentId();
            }
            grade.addType(dto.getType());
            grade.addDescription(dto.getDescription());
            grade.addMaxPoints(dto.getMaxPoints());
            grade.addDate(dto.getDateFormat());
            grade.addAssignmentId(dto.getAssignmentId());
            if (dto.getPoints() == null)
                grade.addPoints("");
            else
                grade.addPoints(String.valueOf(dto.getPoints()));
        }

        if (grade != null && !fio.contains(grade.getStudentId()))
            finalList.add(grade);

        return finalList;
    }

    public List<Assignment> getAssignmentList(int teacherId, int subjectId, int groupId, String type) {
        return teacherDAO.getAssignmentList(teacherId, subjectId, groupId, type);
    }
}
