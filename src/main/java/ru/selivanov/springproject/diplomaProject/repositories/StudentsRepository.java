package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsRepository extends JpaRepository<Student, Integer> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByUser_Username(String username);
    List<Student> findAllByUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(String username);
    List<Student> findAllByUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(String email);
    List<Student> findAllByUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(String firstName);
    List<Student> findAllByUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(String secondName);
    List<Student> findAllByUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(String patronymic);

    List<Student> findAllByGroup_GroupIdAndUser_UsernameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId, String username);
    List<Student> findAllByGroup_GroupIdAndUser_EmailLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId, String patronymic);
    List<Student> findAllByGroup_GroupIdAndUser_FirstNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId, String firstName);
    List<Student> findAllByGroup_GroupIdAndUser_SecondNameLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId, String patronymic);
    List<Student> findAllByGroup_GroupIdAndUser_PatronymicLikeIgnoreCaseOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId, String patronymic);

    List<Student> findAllByGroup_GroupIdOrderByUser_SecondNameAscUser_FirstNameAscUser_PatronymicAscGroup_NameAsc(int groupId);
}
