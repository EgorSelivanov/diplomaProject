package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;

public class TeacherScheduleDTO {
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    @NotEmpty(message = "Не указана аудитория!")
    @Size(min = 2, max = 50, message = "Название (номер) аудитории должно быть между 2 и 50 символов!")
    private String audience;

    @NotEmpty(message = "Не указано здание!")
    @Size(min = 1, max = 50, message = "Название (номер) здания должен быть между 1 и 50 символов!")
    private String building;

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

    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String groupName;

    @Min(1)
    @NotEmpty(message = "Не указан курс группы!")
    private int courseNumber;

    private String className;

    public TeacherScheduleDTO() {}

    public TeacherScheduleDTO(String audience, String building, String dayOfWeek, Date startTime, Date endTime, String name, String type, String groupName, int courseNumber) {
        this.audience = audience;
        this.building = building;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.name = name;
        this.type = type;
        this.groupName = groupName;
        this.courseNumber = courseNumber;
    }

    public SimpleDateFormat getFormatter() {
        return formatter;
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
        this.className = getClassNameByDay().toString();
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getStartTimeFormat() {
        return formatter.format(startTime);
    }

    public String getEndTimeFormat() {
        return formatter.format(endTime);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    private DayOfWeek getClassNameByDay() {
        return switch (this.dayOfWeek) {
            case "Понедельник" -> DayOfWeek.MONDAY;
            case "Вторник" -> DayOfWeek.TUESDAY;
            case "Среда" -> DayOfWeek.WEDNESDAY;
            case "Четверг" -> DayOfWeek.THURSDAY;
            case "Пятница" -> DayOfWeek.FRIDAY;
            case "Суббота" -> DayOfWeek.SATURDAY;
            case "Воскресенье" -> DayOfWeek.SUNDAY;
            default -> throw new RuntimeException("Неверный формат дня недели!");
        };
    }
}
