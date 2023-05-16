package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


import java.util.ArrayList;
import java.util.List;

public class AttendanceToShowDTO {
    @NotEmpty(message = "ФИО пользователя не может быть пустым!")
    @Size(min = 2, max = 255, message = "Имя пользователя должно быть от 2 до 255 символов!")
    private String fullName;

    private List<Integer> attendanceIdList;
    private List<String> dateList;

    private List<Integer> presentList;

    public AttendanceToShowDTO() {
        this.dateList = new ArrayList<>();
        this.presentList = new ArrayList<>();
        this.attendanceIdList = new ArrayList<>();
    }

    public AttendanceToShowDTO(String fullName, List<Integer> attendanceIdList, List<String> dateList, List<Integer> presentList) {
        this.fullName = fullName;
        this.dateList = dateList;
        this.presentList = presentList;
        this.attendanceIdList = attendanceIdList;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<Integer> getAttendanceIdList() {
        return attendanceIdList;
    }

    public void setAttendanceIdList(List<Integer> attendanceIdList) {
        this.attendanceIdList = attendanceIdList;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<Integer> getPresentList() {
        return presentList;
    }

    public void setPresentList(List<Integer> presentList) {
        this.presentList = presentList;
    }

    public void addDate(String date) {
        this.dateList.add(date);
    }

    public void addPresent(Integer present) {
        this.presentList.add(present);
    }

    public void addAttendanceId(Integer id) { this.attendanceIdList.add(id);}
}
