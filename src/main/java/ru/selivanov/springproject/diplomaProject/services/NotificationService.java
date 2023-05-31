package ru.selivanov.springproject.diplomaProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.Notification;
import ru.selivanov.springproject.diplomaProject.repositories.NotificationsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationsRepository notificationsRepository;

    @Autowired
    public NotificationService(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    public Notification getNotificationById(int id) {
        return notificationsRepository.findById(id).orElse(null);
    }

    public List<Notification> getNotificationListByStudentId(int studentId) {
        return notificationsRepository.findAllByStudent_StudentIdOrderByNotificationIdDesc(studentId);
    }

    public List<Notification> getNotificationListShowedByStudentId(int studentId) {
        return notificationsRepository.findAllByStudent_StudentIdAndShowedOrderByNotificationIdDesc(studentId, true);
    }

    public List<Notification> getNotificationListNotShowedByStudentId(int studentId) {
        return notificationsRepository.findAllByStudent_StudentIdAndShowedOrderByNotificationIdDesc(studentId, false);
    }

    @Transactional
    public int createNotification(Notification notification) {
        notificationsRepository.save(notification);
        return notification.getNotificationId();
    }

    @Transactional
    public void createListNotification(List<Notification> notificationList) {
        notificationsRepository.saveAll(notificationList);
    }

    @Transactional
    public void updateNotification(int id, Notification notificationToUpdate) {
        Optional<Notification> notification = notificationsRepository.findById(id);
        if (notification.isEmpty())
            return;

        notificationToUpdate.setNotificationId(notification.get().getNotificationId());
        notificationsRepository.save(notificationToUpdate);
    }

    @Transactional
    public void setShowedNotificationByStudent(int studentId) {
        List<Notification> list = notificationsRepository.findAllByStudent_StudentIdAndShowedOrderByNotificationIdDesc(studentId, false);
        notificationsRepository.markAsShowed(list);
    }

    @Transactional
    public void deleteNotification(int id) {
        if (notificationsRepository.findById(id).isEmpty())
            return;
        notificationsRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllNotificationsByStudent(int studentId) {
        List<Notification> list = notificationsRepository.findAllByStudent_StudentIdOrderByNotificationIdDesc(studentId);
        notificationsRepository.deleteAll(list);
    }
}
