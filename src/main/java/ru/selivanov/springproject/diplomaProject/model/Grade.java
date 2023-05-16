package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.util.Objects;

@Entity
@Table(name = "Grade")
public class Grade {
    @Id
    @Column(name = "grade_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gradeId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id")
    private Assignment assignment;

    @Column(name = "points")
    @Min(0)
    private int points;

    public Grade () {}

    public Grade(Student student, Assignment assignment, int points) {
        this.student = student;
        this.assignment = assignment;
        this.points = points;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return points == grade.points && student.equals(grade.student) && assignment.equals(grade.assignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, assignment, points);
    }
}
