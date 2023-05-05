package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StudentScheduleDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    @NotEmpty(message = "Не указана аудитория!")
    @Size(min = 2, max = 50, message = "Название (номер) аудитории должно быть между 2 и 50 символов!")
    private String audience;

    @NotEmpty(message = "Не указан день недели!")
    private String dayOfWeek;

    @NotEmpty(message = "Не указано время окончания занятия!")
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @NotEmpty(message = "Не указано время окончания занятия!")
    @Temporal(TemporalType.TIME)
    private Date endTime;

    @NotEmpty(message = "Название предмета не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название предмета должно содержать от 2 до 255 символов!")
    private String name;

    @NotEmpty(message = "Тип не может быть пустой!")
    @Size(min = 2, max = 255, message = "Тип должен содержать от 2 до 255 символов!")
    private String type;

    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String firstName;

    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @Size(min = 2, max = 255, message = "Фамилия пользователя должна быть от 2 до 255 символов!")
    private String secondName;


    private String patronymic;

    @NotEmpty(message = "Название кафедры не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название кафедры должно содержать от 2 до 255 символов!")
    private String department;

    public StudentScheduleDTO() {
    }

    public StudentScheduleDTO(String audience, String dayOfWeek, Date startTime, Date endTime, String name, String type, String firstName, String secondName, String patronymic, String department) {
        this.audience = audience;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.department = department;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTeacherName() {
        return secondName + " " + firstName + " " + patronymic;
    }

    public String getStartTimeFormat() {
        return formatter.format(startTime);
    }

    public String getEndTimeFormat() {
        return formatter.format(endTime);
    }
}
