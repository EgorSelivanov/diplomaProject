package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class GroupJSONDTO {
    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String name;

    @Min(1)
    private int courseNumber;

    private String specialityName;

    private String code;

    public GroupJSONDTO() {
    }

    public GroupJSONDTO(String name, int courseNumber, String specialityName, String code) {
        this.name = name;
        this.courseNumber = courseNumber;
        this.specialityName = specialityName;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
