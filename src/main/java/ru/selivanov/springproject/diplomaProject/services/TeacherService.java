package ru.selivanov.springproject.diplomaProject.services;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dao.TeacherDAO;
import ru.selivanov.springproject.diplomaProject.dto.*;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.TeachersRepository;
import ru.selivanov.springproject.diplomaProject.repositories.UsersRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class TeacherService {
    private final TeachersRepository teachersRepository;
    private final UsersRepository usersRepository;
    private final TeacherDAO teacherDAO;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public TeacherService(TeachersRepository teachersRepository, UsersRepository usersRepository, TeacherDAO teacherDAO, PasswordEncoder passwordEncoder) {
        this.teachersRepository = teachersRepository;
        this.usersRepository = usersRepository;
        this.teacherDAO = teacherDAO;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Teacher> findByDepartment(String department) {
        return teachersRepository.findByDepartmentLike(department);
    }

    public List<Teacher> findByPosition(String position) {
        return teachersRepository.findByPositionLike(position);
    }

    public Teacher getTeacherById(int id) {
        return teachersRepository.findById(id).orElse(null);
    }

    @Transactional
    public int addTeacher(Teacher teacher) {
        teachersRepository.save(teacher);
        return teacher.getTeacherId();
    }

    @Transactional
    public void updateTeacherList(List<Teacher> list) {
        teachersRepository.saveAll(list);
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
        Teacher teacher = teachersRepository.findById(id).orElse(null);
        int userId = teacher.getUser().getUserId();
        usersRepository.deleteById(userId);
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

    public List<String> getDepartmentList() {
        return teacherDAO.getDepartmentList();
    }

    public List<Teacher> getTeacherList() {
        return teachersRepository.findAllByOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc();
    }

    public List<Teacher> getTeacherListSearch(String search) {
        search = "%" + search + "%";
        Set<Teacher> set = new TreeSet<>();
        set.addAll(teachersRepository.findAllByUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));
        set.addAll(teachersRepository.findAllByUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));
        set.addAll(teachersRepository.findAllByUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));
        set.addAll(teachersRepository.findAllByUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));
        set.addAll(teachersRepository.findAllByUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));
        set.addAll(teachersRepository.findAllByPositionLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(search));

        return new ArrayList<>(set);
    }

    public List<Teacher> getTeacherListSearchDepartment(String search, String department) {
        search = "%" + search + "%";
        Set<Teacher> set = new HashSet<>();
        set.addAll(teachersRepository.findAllByDepartmentAndUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));
        set.addAll(teachersRepository.findAllByDepartmentAndUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));
        set.addAll(teachersRepository.findAllByDepartmentAndUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));
        set.addAll(teachersRepository.findAllByDepartmentAndUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));
        set.addAll(teachersRepository.findAllByDepartmentAndUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));
        set.addAll(teachersRepository.findAllByDepartmentAndPositionLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(department, search));

        return new ArrayList<>(set);
    }

    @Transactional
    public void updateDataByJSON(@Valid List<TeacherEditDTO> teacherEditDTOList) throws NoSuchFieldException {
        List<Teacher> teacherList = new ArrayList<>();
        List<User> userList = new ArrayList<>();
        for (TeacherEditDTO teacherEditDTO : teacherEditDTOList) {
            Teacher teacher = teachersRepository.findByUser_Username(teacherEditDTO.getUsername().trim()).orElse(null);
            if (teacher == null) {
                teacher = new Teacher();
                User user = new User();
                user.setUsername(teacherEditDTO.getUsername().trim());

                updateUser(user, teacher, teacherEditDTO);
                teacher.setUser(user);
                user.setTeacher(teacher);

                userList.add(user);
                teacherList.add(teacher);
                continue;
            }

            User user = teacher.getUser();

            updateUser(user, teacher, teacherEditDTO);
            userList.add(user);
            teacherList.add(teacher);
        }

        usersRepository.saveAll(userList);
        teachersRepository.saveAll(teacherList);
    }

    private void updateUser(User user, Teacher teacher, TeacherEditDTO teacherEditDTO) throws NoSuchFieldException {
        if (teacherEditDTO.getPassword().trim().length() < 6)
            throw new NoSuchFieldException("Пароль меньше 6 символов для пользователя: " + user.getUsername());
        user.setPassword(passwordEncoder.encode(teacherEditDTO.getPassword().trim()));

        User emailUser = usersRepository.findByEmail(teacherEditDTO.getEmail().trim()).orElse(null);
        if (emailUser != null && !Objects.equals(user.getUserId(), emailUser.getUserId()))
            throw new NoSuchFieldException("Встречен пользователь с зарегестрированным email: " + user.getUsername());

        user.setEmail(teacherEditDTO.getEmail().trim());
        user.setRole("ROLE_TEACHER");
        user.setFirstName(teacherEditDTO.getFirstName().trim());
        user.setSecondName(teacherEditDTO.getSecondName().trim());
        user.setPatronymic(teacherEditDTO.getPatronymic().trim());

        teacher.setDepartment(teacherEditDTO.getDepartment().trim());
        teacher.setPosition(teacherEditDTO.getPosition().trim());
    }
}
