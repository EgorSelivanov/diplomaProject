package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Teacher;

import java.util.List;

@Repository
public interface TeachersRepository extends JpaRepository<Teacher, Integer> {
    List<Teacher> findAllByOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc();
    List<Teacher> findAllByUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String secondName);
    List<Teacher> findAllByUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String firstName);
    List<Teacher> findAllByUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String patronymic);
    List<Teacher> findAllByUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String email);
    List<Teacher> findAllByUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String username);
    List<Teacher> findAllByPositionLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String position);
    List<Teacher> findAllByDepartmentAndUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String secondName);
    List<Teacher> findAllByDepartmentAndUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String firstName);
    List<Teacher> findAllByDepartmentAndUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String patronymic);
    List<Teacher> findAllByDepartmentAndUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String email);
    List<Teacher> findAllByDepartmentAndUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String username);
    List<Teacher> findAllByDepartmentAndPositionLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAsc(String department, String position);
    List<Teacher> findByDepartmentLike(String department);
    List<Teacher> findByPositionLike(String position);
}
