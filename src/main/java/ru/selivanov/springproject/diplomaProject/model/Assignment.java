package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Assignment_")
public class Assignment {

    @Id
    @Column(name = "assignment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int assignment_id;

    @Column(name = "type")
    @NotEmpty(message = "Тип оценки не может быть пустым!")
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "max_points")
    @NotEmpty(message = "Количество максимальных баллов не указано!")
    @Min(0)
    private int max_points;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "workload_id", referencedColumnName = "workload_id")
    private Workload workload;

    @OneToMany(mappedBy = "assignment")
    private List<Grade> gradeList;

    public Assignment() {}

    public Assignment(String type, String description, int max_points, Date date, Workload workload) {
        this.type = type;
        this.description = description;
        this.max_points = max_points;
        this.date = date;
        this.workload = workload;
    }

    public Assignment(String type, String description, int max_points, Date date) {
        this.type = type;
        this.description = description;
        this.max_points = max_points;
        this.date = date;
    }

    public int getAssignment_id() {
        return assignment_id;
    }

    public void setAssignment_id(int assignment_id) {
        this.assignment_id = assignment_id;
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

    public int getMax_points() {
        return max_points;
    }

    public void setMax_points(int max_points) {
        this.max_points = max_points;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Workload getWorkload() {
        return workload;
    }

    public void setWorkload(Workload workload) {
        this.workload = workload;
    }

    public List<Grade> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<Grade> gradeList) {
        this.gradeList = gradeList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return assignment_id == that.assignment_id && max_points == that.max_points && type.equals(that.type) && Objects.equals(description, that.description) && date.equals(that.date) && workload.equals(that.workload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignment_id, type, description, max_points, date, workload);
    }
}
