package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GradesDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    @NotEmpty(message = "Тип оценки не может быть пустым!")
    private String type;

    private String description;

    @NotEmpty(message = "Количество максимальных баллов не указано!")
    @Min(0)
    private int maxPoints;

    @Temporal(TemporalType.DATE)
    private Date date;

    private String dateFormat;
    @Min(0)
    @NotEmpty(message = "Количество баллов не должно быть пустым!")
    private int points;

    private String workloadType;

    public GradesDTO() {}

    public GradesDTO(String type, String description, int maxPoints, Date date, int points) {
        this.type = type;
        this.description = description;
        this.maxPoints = maxPoints;
        this.date = date;
        this.points = points;
        this.dateFormat = formatter.format(this.date);
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

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(int max_points) {
        this.maxPoints = max_points;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        this.dateFormat = formatter.format(date);
    }

    public String getWorkloadType() {
        return workloadType;
    }

    public void setWorkloadType(String workloadType) {
        this.workloadType = workloadType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
