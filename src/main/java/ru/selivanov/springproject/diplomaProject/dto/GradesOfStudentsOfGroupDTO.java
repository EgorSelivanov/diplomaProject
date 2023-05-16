package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GradesOfStudentsOfGroupDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    private int studentId;
    private int assignmentId;
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String firstName;

    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @Size(min = 2, max = 255, message = "Фамилия пользователя должна быть от 2 до 255 символов!")
    private String secondName;

    private String patronymic;

    @NotEmpty(message = "Тип оценки не может быть пустым!")
    private String type;

    private String description;

    @NotEmpty(message = "Количество максимальных баллов не указано!")
    @Min(0)
    private Integer maxPoints;

    @Temporal(TemporalType.DATE)
    private Date date;

    private Integer points;

    public GradesOfStudentsOfGroupDTO() {}

    public GradesOfStudentsOfGroupDTO(int studentId, int assignmentId, String firstName, String secondName, String patronymic, String type, String description, Integer maxPoints, Date date, Integer points) {
        this.studentId = studentId;
        this.assignmentId = assignmentId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.type = type;
        this.description = description;
        this.maxPoints = maxPoints;
        this.date = date;
        this.points = points;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getDateFormat() {
        return formatter.format(date);
    }

    public String getFullName() {
        return this.secondName + " " + this.firstName + " " + this.patronymic;
    }
}
