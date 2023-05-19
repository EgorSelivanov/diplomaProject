package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByFirstNameLikeOrSecondNameLike(String firstName, String secondName);
    List<User> findByRole(String role);
    List<User> findByRoleAndUsernameLikeIgnoreCase(String role, String username);
    List<User> findByRoleAndEmailLikeIgnoreCase(String role, String email);
    List<User> findByRoleAndFirstNameLikeIgnoreCase(String role, String firstName);
    List<User> findByRoleAndSecondNameLikeIgnoreCase(String role, String secondName);
    List<User> findByRoleAndPatronymicLikeIgnoreCase(String role, String patronymic);
}
