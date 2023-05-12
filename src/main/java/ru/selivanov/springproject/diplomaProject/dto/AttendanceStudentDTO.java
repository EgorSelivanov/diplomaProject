package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceStudentDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;

    @NotEmpty(message = "Не указан день недели!")
    private String dayOfWeek;

    @Min(0)
    private int present;

    @NotEmpty(message = "Тип не может быть пустой!")
    @Size(min = 2, max = 255, message = "Тип должен содержать от 2 до 255 символов!")
    private String type;

    public AttendanceStudentDTO() {}

    public AttendanceStudentDTO(Date date, String dayOfWeek, int present, String type) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.present = present;
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateFormat() {
        return formatter.format(date);
    }
}
