package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Teacher")
public class Teacher implements Comparable<Teacher>{
    @Id
    @Column(name = "teacher_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int teacherId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(name = "department")
    @NotEmpty(message = "Название кафедры не может быть пустым!")
    @Size(min = 2, max = 255, message = "Название кафедры должно содержать от 2 до 255 символов!")
    private String department;

    @Column(name = "position")
    @NotEmpty(message = "Должность не может быть пустой!")
    @Size(min = 2, max = 255, message = "Должность должна содержать от 2 до 255 символов!")
    private String position;

    @OneToMany(mappedBy = "teacher")
    private List<Workload> workloadList;

    public Teacher() {}

    public Teacher(User user, String department, String position) {
        this.user = user;
        this.department = department;
        this.position = position;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Workload> getWorkloadList() {
        return workloadList;
    }

    public void setWorkloadList(List<Workload> workloadList) {
        this.workloadList = workloadList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return teacherId == teacher.teacherId && user.equals(teacher.user) && department.equals(teacher.department) && position.equals(teacher.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teacherId, user, department, position);
    }

    @Override
    public int compareTo(Teacher o) {
        String fioThis = this.user.getSecondName() + " " + this.user.getFirstName() + " " + this.user.getPatronymic();
        String fioObj = o.user.getSecondName() + " " + o.user.getFirstName() + " " + o.user.getPatronymic();
        return fioThis.compareTo(fioObj);
    }
}
