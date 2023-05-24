package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Group_")
public class Group {
    @Id
    @Column(name = "group_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;

    @Column(name = "name")
    @NotEmpty(message = "Не указано название группы!")
    @Size(min = 2, max = 255, message = "Название группы должно быть от 2 до 255 символов!")
    private String name;

    @ManyToOne
    @JoinColumn(name = "speciality_id", referencedColumnName = "speciality_id")
    private Speciality speciality;

    @Column(name = "course_number")
    @Min(1)
    private int courseNumber;

    @OneToMany(mappedBy = "group")
    private List<Student> students;

    @OneToMany(mappedBy = "group")
    private List<Workload> workloadList;

    public Group() {}

    public Group(String name, int courseNumber) {
        this.name = name;
        this.courseNumber = courseNumber;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
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
        Group group = (Group) o;
        return courseNumber == group.courseNumber && name.equals(group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, courseNumber);
    }
}
