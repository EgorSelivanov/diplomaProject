package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceOfStudentsDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    @Min(0)
    private int attendanceId;
    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String firstName;

    @NotEmpty(message = "Фамилия пользователя не может быть пустой!")
    @Size(min = 2, max = 255, message = "Фамилия пользователя должна быть от 2 до 255 символов!")
    private String secondName;

    private String patronymic;

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    @Min(0)
    private int present;

    public AttendanceOfStudentsDTO() {}

    public AttendanceOfStudentsDTO(int attendanceId, String firstName, String secondName, String patronymic, Date date, int present) {
        this.attendanceId = attendanceId;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.date = date;
        this.present = present;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getDateFormat() {
        return formatter.format(date);
    }

    public String getFullName() {
        return this.secondName + " " + this.firstName + " " + this.patronymic;
    }
}
