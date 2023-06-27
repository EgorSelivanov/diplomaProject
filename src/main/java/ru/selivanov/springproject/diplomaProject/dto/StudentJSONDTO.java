package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class StudentJSONDTO {
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

    @NotEmpty(message = "Пароль не может быть пустым!")
    @Size(min = 6, max = 255, message = "Пароль должен быть длиной минимум 6 символов!")
    private String password;

    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String groupName;

    public StudentJSONDTO() {
    }

    public StudentJSONDTO(String username, String email, String firstName, String secondName, String patronymic, String password, String groupName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.password = password;
        this.groupName = groupName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
