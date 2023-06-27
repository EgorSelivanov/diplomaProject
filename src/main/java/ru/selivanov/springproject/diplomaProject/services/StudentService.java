package ru.selivanov.springproject.diplomaProject.services;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.StudentDAO;
import ru.selivanov.springproject.diplomaProject.dto.*;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.GroupsRepository;
import ru.selivanov.springproject.diplomaProject.repositories.StudentsRepository;
import ru.selivanov.springproject.diplomaProject.repositories.UsersRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentsRepository studentsRepository;
    private final UsersRepository usersRepository;
    private final GroupsRepository groupsRepository;
    private final StudentDAO studentDAO;
    private final PasswordEncoder passwordEncoder;
    private final WorkloadService workloadService;

    @Autowired
    public StudentService(StudentsRepository studentsRepository, UsersRepository usersRepository, GroupsRepository groupsRepository, StudentDAO studentDAO, PasswordEncoder passwordEncoder, WorkloadService workloadService) {
        this.studentsRepository = studentsRepository;
        this.usersRepository = usersRepository;
        this.groupsRepository = groupsRepository;
        this.studentDAO = studentDAO;
        this.passwordEncoder = passwordEncoder;
        this.workloadService = workloadService;
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

    @Transactional
    public void updateDataByJSON(@Valid List<StudentJSONDTO> studentJSONDTOList) throws NoSuchFieldException {
        List<Student> studentList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        List<Integer> attendanceList = new ArrayList<>();
        for (StudentJSONDTO studentJSONDTO : studentJSONDTOList) {
            Student student = studentsRepository.findByUser_Username(studentJSONDTO.getUsername().trim()).orElse(null);

            if (student == null) {
                student = new Student();
                User user = new User();
                user.setUsername(studentJSONDTO.getUsername().trim());

                updateUser(user, student, studentJSONDTO);
                student.setUser(user);
                user.setStudent(student);

                userList.add(user);
                studentList.add(student);
                attendanceList.add(studentList.size() - 1);
                continue;
            }

            User user = student.getUser();
            if (!student.getGroup().getName().equals(studentJSONDTO.getGroupName().trim()))
                attendanceList.add(studentList.size());

            updateUser(user, student, studentJSONDTO);
            userList.add(user);
            studentList.add(student);
        }

        usersRepository.saveAll(userList);
        studentsRepository.saveAll(studentList);
        //Создать посещаемости
        for (Integer index : attendanceList) {
            Student student = studentList.get(index);
            workloadService.deleteStudentAttendances(student.getStudentId());
            workloadService.createStudentAttendances(student.getStudentId());
        }
    }

    private void updateUser(User user, Student student, StudentJSONDTO studentJSONDTO) throws NoSuchFieldException {
        if (studentJSONDTO.getPassword().trim().length() < 6)
            throw new NoSuchFieldException("Пароль меньше 6 символов для пользователя: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(studentJSONDTO.getPassword().trim()));

        User emailUser = usersRepository.findByEmail(studentJSONDTO.getEmail().trim()).orElse(null);
        if (emailUser != null && !Objects.equals(user.getUserId(), emailUser.getUserId()))
            throw new NoSuchFieldException("Встречен пользователь с зарегестрированным email: " + user.getUsername());

        user.setEmail(studentJSONDTO.getEmail().trim());
        user.setRole("ROLE_STUDENT");
        user.setFirstName(studentJSONDTO.getFirstName().trim());
        user.setSecondName(studentJSONDTO.getSecondName().trim());
        user.setPatronymic(studentJSONDTO.getPatronymic().trim());

        Group group = groupsRepository.findByName(studentJSONDTO.getGroupName().trim()).orElse(null);
        if (group == null)
            throw new NoSuchFieldException("Данной группы не найдено: " + studentJSONDTO.getGroupName());

        student.setGroup(group);
    }
}
