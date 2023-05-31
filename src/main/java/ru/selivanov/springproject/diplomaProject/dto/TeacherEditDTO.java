package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class TeacherEditDTO {
    private int teacherId;
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String username;

    @Email
    @NotEmpty(message = "Данные почты не могут быть пустыми!")
    private String email;

    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String firstName;

    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @Size(min = 2, max = 255, message = "Фамилия пользователя должна быть от 2 до 255 символов!")
    private String secondName;

    private String patronymic;

    private String password;

    @NotEmpty(message = "Название кафедры не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название кафедры должно содержать от 2 до 255 символов!")
    private String department;

    @NotEmpty(message = "Должность не может быть пустой!")
    @Size(min = 2, max = 255, message = "Должность должна содержать от 2 до 255 символов!")
    private String position;

    public TeacherEditDTO() {
    }

    public TeacherEditDTO(int teacherId, String username, String email, String firstName, String secondName, String patronymic, String department, String position) {
        this.teacherId = teacherId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.department = department;
        this.position = position;
    }

    public TeacherEditDTO(int teacherId, String username, String email, String firstName, String secondName, String patronymic, String password, String department, String position) {
        this.teacherId = teacherId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.password = password;
        this.department = department;
        this.position = position;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
