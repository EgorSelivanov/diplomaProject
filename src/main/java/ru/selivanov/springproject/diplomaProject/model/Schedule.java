package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Schedule")
public class Schedule {
    @Id
    @Column(name = "schedule_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int scheduleId;

    @ManyToOne
    @JoinColumn(name = "workload_id", referencedColumnName = "workload_id")
    private Workload workload;

    @Column(name = "audience")
    @NotEmpty(message = "Не указана аудитория!")
    @Size(min = 2, max = 50, message = "Название (номер) аудитории должно быть между 2 и 50 символов!")
    private String audience;

    @Column(name = "building")
    @NotEmpty(message = "Не указано здание!")
    @Size(min = 1, max = 50, message = "Название (номер) здания должен быть между 1 и 50 символов!")
    private String building;

    @Column(name = "day_of_week")
    @NotEmpty(message = "Не указан день недели!")
    private String dayOfWeek;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIME)
    private Date endTime;

    @Column(name = "repeat")
    private String repeat;

    @OneToMany(mappedBy = "schedule")
    private List<Attendance> attendanceList;

    public Schedule() {}

    public Schedule(Workload workload, String audience, String building, String dayOfWeek, Date startTime, Date endTime) {
        this.workload = workload;
        this.building = building;
        this.audience = audience;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule(String audience, String building, String dayOfWeek, Date startTime, Date endTime) {
        this.audience = audience;
        this.building = building;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Workload getWorkload() {
        return workload;
    }

    public void setWorkload(Workload workload) {
        this.workload = workload;
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

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return workload.equals(schedule.workload) && audience.equals(schedule.audience) && dayOfWeek.equals(schedule.dayOfWeek) && startTime.equals(schedule.startTime) && endTime.equals(schedule.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workload, audience, dayOfWeek, startTime, endTime);
    }
}
