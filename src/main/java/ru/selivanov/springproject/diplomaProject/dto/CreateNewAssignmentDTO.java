package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;

public class CreateNewAssignmentDTO {
    @NotEmpty(message = "Тип оценки не может быть пустым!")
    private String type;

    private String description;

    @Min(0)
    private int maxPoints;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Min(1)
    private int teacherId;

    @Min(1)
    private int subjectId;

    @Min(1)
    private int groupId;

    @NotEmpty(message = "Вид занятия не может быть пустым!")
    private String workloadType;

    public CreateNewAssignmentDTO() {}

    public CreateNewAssignmentDTO(String type, String description, int maxPoints, Date date, int teacherId, int subjectId, int groupId, String workloadType) {
        this.type = type;
        this.description = description;
        this.maxPoints = maxPoints;
        this.date = date;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.groupId = groupId;
        this.workloadType = workloadType;
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

    public void setMaxPoints(int maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getWorkloadType() {
        return workloadType;
    }

    public void setWorkloadType(String workloadType) {
        this.workloadType = workloadType;
    }
}
