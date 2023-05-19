package ru.selivanov.springproject.diplomaProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.repositories.UsersRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {
    private final UsersRepository usersRepository;

    @Autowired
    public AdminService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User getUserById(int id) {
        return usersRepository.findById(id).orElse(null);
    }

    public List<User> getAdminList() {
        return usersRepository.findByRole("ROLE_ADMIN");
    }

    public List<User> getAdminListSearch(String search) {
        List<User> list = new ArrayList<>();
        search = "%" + search + "%";
        list.addAll(usersRepository.findByRoleAndUsernameLikeIgnoreCase("ROLE_ADMIN", search));
        list.addAll(usersRepository.findByRoleAndEmailLikeIgnoreCase("ROLE_ADMIN", search));
        list.addAll(usersRepository.findByRoleAndFirstNameLikeIgnoreCase("ROLE_ADMIN", search));
        list.addAll(usersRepository.findByRoleAndSecondNameLikeIgnoreCase("ROLE_ADMIN", search));
        list.addAll(usersRepository.findByRoleAndPatronymicLikeIgnoreCase("ROLE_ADMIN", search));
        return list;
    }
}
