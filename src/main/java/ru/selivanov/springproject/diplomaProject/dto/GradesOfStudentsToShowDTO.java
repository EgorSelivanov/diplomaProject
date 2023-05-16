package ru.selivanov.springproject.diplomaProject.dto;

import java.util.ArrayList;
import java.util.List;

public class GradesOfStudentsToShowDTO {
    private int studentId;
    private List<Integer> assignmentIdList;
    private String fullName;

    private List<String> typeOfAssignmentList;

    private List<String> descriptionList;

    private List<Integer> maxPointsList;

    private List<String> dateList;

    private List<String> pointsList;

    public GradesOfStudentsToShowDTO() {
        this.typeOfAssignmentList = new ArrayList<>();
        this.descriptionList = new ArrayList<>();
        this.maxPointsList = new ArrayList<>();
        this.dateList = new ArrayList<>();
        this.pointsList = new ArrayList<>();
        this.assignmentIdList = new ArrayList<>();
    }

    public GradesOfStudentsToShowDTO(int studentId, List<Integer> assignmentIdList, String fullName, List<String> typeOfAssignmentList, List<String> descriptionList, List<Integer> maxPointsList, List<String> dateList, List<String> pointsList) {
        this.studentId = studentId;
        this.assignmentIdList = assignmentIdList;
        this.fullName = fullName;
        this.typeOfAssignmentList = typeOfAssignmentList;
        this.descriptionList = descriptionList;
        this.maxPointsList = maxPointsList;
        this.dateList = dateList;
        this.pointsList = pointsList;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getTypeOfAssignmentList() {
        return typeOfAssignmentList;
    }

    public void setTypeOfAssignmentList(List<String> typeOfAssignmentList) {
        this.typeOfAssignmentList = typeOfAssignmentList;
    }

    public List<String> getDescriptionList() {
        return descriptionList;
    }

    public void setDescriptionList(List<String> descriptionList) {
        this.descriptionList = descriptionList;
    }

    public List<Integer> getMaxPointsList() {
        return maxPointsList;
    }

    public void setMaxPointsList(List<Integer> maxPointsList) {
        this.maxPointsList = maxPointsList;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public List<String> getPointsList() {
        return pointsList;
    }

    public void setPointsList(List<String> pointsList) {
        this.pointsList = pointsList;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public List<Integer> getAssignmentIdList() {
        return assignmentIdList;
    }

    public void setAssignmentId(List<Integer> assignmentId) {
        this.assignmentIdList = assignmentId;
    }

    public void addType(String type) {
        this.typeOfAssignmentList.add(type);
    }

    public void addDescription(String description) {
        this.descriptionList.add(description);
    }

    public void addMaxPoints(Integer maxPoints) {
        this.maxPointsList.add(maxPoints);
    }

    public void addDate(String date) {
        this.dateList.add(date);
    }

    public void addPoints(String points) {
        this.pointsList.add(points);
    }

    public void addAssignmentId(Integer id) { this.assignmentIdList.add(id); }
}
