package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleToShowDTO {
    @Transient
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
    private int scheduleId;

    @NotEmpty(message = "Название предмета не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название предмета должно содержать от 2 до 255 символов!")
    private String subjectName;

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

    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String groupName;

    @NotEmpty(message = "Не указана аудитория!")
    @Size(min = 2, max = 50, message = "Название (номер) аудитории должно быть между 2 и 50 символов!")
    private String audience;

    @NotEmpty(message = "Не указано здание!")
    @Size(min = 1, max = 50, message = "Название (номер) здания должен быть между 1 и 50 символов!")
    private String building;

    @NotEmpty(message = "Не указан день недели!")
    private String dayOfWeek;

    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Temporal(TemporalType.TIME)
    private Date endTime;

    private String repeat;

    public ScheduleToShowDTO() {
    }

    public ScheduleToShowDTO(int workloadId, String subjectName, String type, String firstName, String secondName, String patronymic, String groupName, String audience, String building, String dayOfWeek, Date startTime, Date endTime, String repeat) {
        this.scheduleId = workloadId;
        this.subjectName = subjectName;
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.groupName = groupName;
        this.audience = audience;
        this.building = building;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.repeat = repeat;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
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

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getTeacherFio() {
        return this.secondName + " " + this.firstName + " " + this.patronymic;
    }

    public String getStartTimeFormat() {
        return formatter.format(this.startTime);
    }

    public String getEndTimeFormat() {
        return formatter.format(this.endTime);
    }
}
