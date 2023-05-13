package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewScheduleTeacherDTO {
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

    @Min(1)
    private int subjectId;

    @NotEmpty(message = "Тип не может быть пустой!")
    @Size(min = 2, max = 255, message = "Тип должен содержать от 2 до 255 символов!")
    private String type;


    private String repeat;

    @Min(1)
    private int groupId;

    @Min(1)
    private int teacherId;

    public NewScheduleTeacherDTO() {};

    public NewScheduleTeacherDTO(String audience, String dayOfWeek, Date startTime, Date endTime, int subjectId, String type, int groupId) {
        this.audience = audience;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectId = subjectId;
        this.type = type;
        this.groupId = groupId;
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

    public void setStartTime(String startTime) {
        try {
            this.startTime = formatter.parse(startTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        try {
            this.endTime = formatter.parse(endTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
