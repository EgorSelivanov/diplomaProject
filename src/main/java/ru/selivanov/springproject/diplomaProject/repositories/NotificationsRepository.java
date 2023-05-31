package ru.selivanov.springproject.diplomaProject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.selivanov.springproject.diplomaProject.model.Notification;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByStudent_StudentIdAndShowedOrderByNotificationIdDesc(int studentId, boolean showed);
    List<Notification> findAllByStudent_StudentIdOrderByNotificationIdDesc(int studentId);

    @Modifying
    @Query("UPDATE Notification n SET n.showed = true WHERE n IN :notifications")
    void markAsShowed(@Param("notifications") List<Notification> notifications);
}
