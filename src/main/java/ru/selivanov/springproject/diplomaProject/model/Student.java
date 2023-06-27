package ru.selivanov.springproject.diplomaProject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;


@Entity
@Table(name = "Student")
public class Student implements Comparable<Student>{

    @Id
    @Column(name = "student_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id")
    private Group group;

    @OneToMany(mappedBy = "student")
    private List<Grade> gradeList;

    @OneToMany(mappedBy = "student")
    private List<Attendance> attendanceList;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Notification> notificationList;

    public Student() {}

    public Student(User user, Group group) {
        this.user = user;
        this.group = group;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Grade> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<Grade> gradeList) {
        this.gradeList = gradeList;
    }

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public List<Notification> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return studentId == student.studentId && user.equals(student.user) && Objects.equals(group, student.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, user, group);
    }

    @Override
    public int compareTo(Student o) {
        String fioThis = this.user.getSecondName() + " " + this.user.getFirstName() + " " + this.user.getPatronymic();
        String fioObj = o.user.getSecondName() + " " + o.user.getFirstName() + " " + o.user.getPatronymic();
        return fioThis.compareTo(fioObj);
    }
}
