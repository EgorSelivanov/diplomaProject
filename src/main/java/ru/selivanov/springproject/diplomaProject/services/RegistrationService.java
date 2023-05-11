package ru.selivanov.springproject.diplomaProject.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.repositories.UsersRepository;


@Service
public class RegistrationService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        usersRepository.save(user);
    }

    @Transactional
    public void changePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));

        usersRepository.save(user);
    }
}
