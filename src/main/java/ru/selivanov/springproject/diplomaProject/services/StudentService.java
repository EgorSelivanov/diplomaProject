package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.StudentDAO;
import ru.selivanov.springproject.diplomaProject.dto.AttendanceStudentDTO;
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

    public Student getStudentById(int id) {
        return studentsRepository.findById(id).orElse(null);
    }

    public List<StudentScheduleDTO> getScheduleDataByStudent(int id, Date date) {
        return studentDAO.getStudentScheduleData(id, date);
    }

    public List<Subject> getSubjectListByStudent(int id) {
        Group group = this.getGroupByStudent(id);

        return studentDAO.getSubjectListByGroup(group.getGroupId());
    }

    public List<GradesDTO> getGradesListByStudent(int id, int subjectId) {
        Group group = this.getGroupByStudent(id);

        return studentDAO.getGradesDTOListByGroup(group.getGroupId(), subjectId, id);
    }

    public List<Integer> getCourseNumberList() {
        return studentDAO.getCourseNumberList();
    }

    public List<Student> getStudentListSearch(String search) {
        search = "%" + search + "%";
        Set<Student> set = new TreeSet<>();
        set.addAll(studentsRepository.findAllByUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(search));
        set.addAll(studentsRepository.findAllByUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(search));
        set.addAll(studentsRepository.findAllByUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(search));
        set.addAll(studentsRepository.findAllByUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(search));
        set.addAll(studentsRepository.findAllByUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(search));

        return new ArrayList<>(set);
    }

    public List<Student> getStudentListSearchByGroup(String search, int groupId) {
        search = "%" + search + "%";
        Set<Student> set = new TreeSet<>();
        set.addAll(studentsRepository.findAllByGroup_GroupIdAndUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId, search));
        set.addAll(studentsRepository.findAllByGroup_GroupIdAndUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId, search));
        set.addAll(studentsRepository.findAllByGroup_GroupIdAndUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId, search));
        set.addAll(studentsRepository.findAllByGroup_GroupIdAndUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId, search));
        set.addAll(studentsRepository.findAllByGroup_GroupIdAndUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId, search));

        return new ArrayList<>(set);
    }

    public List<Student> getStudentListByGroup(int groupId) {
        return studentsRepository.findAllByGroup_GroupIdOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(groupId);
    }

    @Transactional
    public int addStudent(Student student) {
        studentsRepository.save(student);
        return student.getStudentId();
    }

    @Transactional
    public void updateStudentList(List<Student> list) {
        studentsRepository.saveAll(list);
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

    public List<AttendanceStudentDTO> getAttendanceListByStudentAndSubject(int studentId, int subjectId) {
        return studentDAO.getAttendanceDTOList(studentId, subjectId);
    }
}
