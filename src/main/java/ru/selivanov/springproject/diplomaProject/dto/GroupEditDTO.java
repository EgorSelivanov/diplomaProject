package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.selivanov.springproject.diplomaProject.model.Group;

public class GroupEditDTO {
    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String name;

    private Integer specialityId;

    @Min(1)
    private int courseNumber;

    public GroupEditDTO() {
    }

    public GroupEditDTO(String name, Integer specialityId, int courseNumber) {
        this.name = name;
        this.specialityId = specialityId;
        this.courseNumber = courseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(Integer specialityId) {
        this.specialityId = specialityId;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public Group getGroup() {
        return new Group(name, courseNumber);
    }
}
