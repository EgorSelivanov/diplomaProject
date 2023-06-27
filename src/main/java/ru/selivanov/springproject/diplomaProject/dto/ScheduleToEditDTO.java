package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleToEditDTO {
    @Transient
    private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

    private int scheduleId;
    private int workloadId;

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

    public ScheduleToEditDTO() {
    }

    public ScheduleToEditDTO(int scheduleId, String audience, String building, String dayOfWeek, String startTime, String endTime, String repeat) {
        this.scheduleId = scheduleId;
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

    public int getWorkloadId() {
        return workloadId;
    }

    public void setWorkloadId(int workloadId) {
        this.workloadId = workloadId;
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
