package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ScheduleWorkloadDTO {
    private int workloadId;
    @NotEmpty(message = "Название предмета не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название предмета должно содержать от 2 до 255 символов!")
    private String subjectName;

    private String subjectDescription;

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

    @Min(1)
    private int courseNumber;

    @NotEmpty(message = "Название специальности не должно быть пустым!")
    private String specialityName;

    public ScheduleWorkloadDTO() {
    }

    public ScheduleWorkloadDTO(String subjectName, String description, String type, String firstName, String secondName, String patronymic, String groupName, int courseNumber, String specialityName) {
        this.subjectName = subjectName;
        this.subjectDescription = description;
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.groupName = groupName;
        this.courseNumber = courseNumber;
        this.specialityName = specialityName;
    }

    public ScheduleWorkloadDTO(int workloadId, String subjectName, String subjectDescription, String type, String firstName, String secondName, String patronymic, String groupName, int courseNumber, String specialityName) {
        this.workloadId = workloadId;
        this.subjectName = subjectName;
        this.subjectDescription = subjectDescription;
        this.type = type;
        this.firstName = firstName;
        this.secondName = secondName;
        this.patronymic = patronymic;
        this.groupName = groupName;
        this.courseNumber = courseNumber;
        this.specialityName = specialityName;
    }

    public int getWorkloadId() {
        return workloadId;
    }

    public void setWorkloadId(int workloadId) {
        this.workloadId = workloadId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
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

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String specialityName) {
        this.specialityName = specialityName;
    }

    public String getTeacherFio() {
        return this.secondName + " " + this.firstName + " " + this.getPatronymic();
    }
}
