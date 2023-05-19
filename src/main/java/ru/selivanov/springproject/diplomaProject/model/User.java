package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "User_")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(name = "username")
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String username;

    @Column(name = "password")
    @NotEmpty(message = "Пароль не может быть пустым!")
    @Size(min = 6, max = 255, message = "Пароль должен быть длиной минимум 6 символов!")
    private String password;

    @Column(name = "email")
    @Email
    @NotEmpty(message = "Данные почты не могут быть пустыми!")
    private String email;

    @Column(name = "role")
    @NotEmpty(message = "Не выбрана роль пользователя")
    private String role;

    @Column(name = "first_name")
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String firstName;

    @Column(name = "second_name")
    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @Size(min = 2, max = 255, message = "Фамилия пользователя должна быть от 2 до 255 символов!")
    private String secondName;

    @Column(name = "patronymic")
    private String patronymic;

    @OneToOne(mappedBy = "user")
    //@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Student student;

    @OneToOne(mappedBy = "user")
    //@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    private Teacher teacher;

    public User() {}

    public User(String username, String password, String email, String role, String firstName, String secondName, String patronymic) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
    }

    public User(String username, String email, String role, String firstName, String secondName, String patronymic) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && password.equals(user.password) && email.equals(user.email) &&
                role.equals(user.role) && firstName.equals(user.firstName) && secondName.equals(user.secondName) &&
                Objects.equals(patronymic, user.patronymic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
