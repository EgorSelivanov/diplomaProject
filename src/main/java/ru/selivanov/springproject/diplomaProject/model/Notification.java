package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Notification")
public class Notification {
    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    @Column(name = "message")
    private String message;

    @Column(name = "showed")
    private boolean showed;

    public Notification() {
    }

    public Notification(Student student, Attendance attendance, String message, boolean showed) {
        this.student = student;
        this.message = message;
        this.showed = showed;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShowed() {
        return showed;
    }

    public void setShowed(boolean showed) {
        this.showed = showed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return notificationId == that.notificationId && student.equals(that.student) &&  message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationId, student, message);
    }
}
