package ru.selivanov.springproject.diplomaProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.model.Teacher;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.repositories.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return usersRepository.findAll();
    }

    public Optional<User> findById(int id) {
        return usersRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public List<User> findByFirstNameOrSecondName(String firstName, String secondName) {
        return usersRepository.findByFirstNameLikeOrSecondNameLike(firstName, secondName);
    }

    @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    @Transactional
    public void updateUser(int id, User updatedUser, String password) {
        Optional<User> userById = usersRepository.findById(id);

        User userToBeUpdated = userById.orElse(null);
        if (userToBeUpdated == null)
            throw new RuntimeException();

        updatedUser.setUserId(id);
        updatedUser.setPassword(passwordEncoder.encode(password));
        updatedUser.setStudent(userToBeUpdated.getStudent());
        updatedUser.setTeacher(userToBeUpdated.getTeacher());
        usersRepository.save(updatedUser);
    }

    @Transactional
    public void updateUser(int id, User updatedUser) {
        Optional<User> userById = usersRepository.findById(id);

        User userToBeUpdated = userById.orElse(null);
        if (userToBeUpdated == null)
            throw new RuntimeException();

        updatedUser.setUserId(id);
        updatedUser.setPassword(userToBeUpdated.getPassword());
        updatedUser.setStudent(userToBeUpdated.getStudent());
        updatedUser.setTeacher(userToBeUpdated.getTeacher());
        usersRepository.save(updatedUser);
    }

    @Transactional
    public void deleteUser(int id) {
        usersRepository.deleteById(id);
    }

    public Student getStudentByUser(int id) {
        return usersRepository.findById(id).map(User::getStudent).orElse(null);
    }

    public Teacher getTeacherByUser(int id) {
        return usersRepository.findById(id).map(User::getTeacher).orElse(null);
    }

    @Transactional
    public void assignStudent(int id, Student selectedStudent) {
        usersRepository.findById(id).ifPresent(
                user -> {
                    user.setStudent(selectedStudent);
                }
        );
    }

    @Transactional
    public void assignTeacher(int id, Teacher selectedTeacher) {
        usersRepository.findById(id).ifPresent(
                user -> {
                    user.setTeacher(selectedTeacher);
                }
        );
    }
}
