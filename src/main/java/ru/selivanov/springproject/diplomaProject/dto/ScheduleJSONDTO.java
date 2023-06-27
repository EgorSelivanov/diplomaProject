package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleJSONDTO {
    @Transient
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    @NotEmpty(message = "Не указана аудитория!")
    @Size(min = 2, max = 50, message = "Название (номер) аудитории должно быть между 2 и 50 символов!")
    private String audience;

    @NotEmpty(message = "Не указано здание!")
    @Size(min = 1, max = 50, message = "Название (номер) здания должен быть между 1 и 50 символов!")
    private String building;

    @NotEmpty(message = "Не указан день недели!")
    private String dayOfWeek;

    private String startTime;

    private String endTime;

    private String repeat;

    @NotEmpty(message = "Имя пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String username;

    @NotEmpty(message = "Название предмета не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название предмета должно содержать от 2 до 255 символов!")
    private String subjectName;

    @NotEmpty(message = "Тип не может быть пустой!")
    @Size(min = 2, max = 255, message = "Тип должен содержать от 2 до 255 символов!")
    private String type;

    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String groupName;

    public ScheduleJSONDTO() {
    }

    public ScheduleJSONDTO(String audience, String building, String dayOfWeek, String startTime, String endTime, String repeat, String username, String subjectName, String type, String groupName) {
        this.audience = audience;
        this.building = building;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
        this.username = username;
        this.subjectName = subjectName;
        this.type = type;
        this.groupName = groupName;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getStartTimeFormat() {
        try {
            return formatter.parse(this.startTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getEndTimeFormat() {
        try {
            return formatter.parse(this.endTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
