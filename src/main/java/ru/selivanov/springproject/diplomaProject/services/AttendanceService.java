package ru.selivanov.springproject.diplomaProject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.Attendance;
import ru.selivanov.springproject.diplomaProject.model.Schedule;
import ru.selivanov.springproject.diplomaProject.model.Student;
import ru.selivanov.springproject.diplomaProject.repositories.AttendancesRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AttendanceService {
    private final AttendancesRepository attendancesRepository;

    @Autowired
    public AttendanceService(AttendancesRepository attendancesRepository) {
        this.attendancesRepository = attendancesRepository;
    }

    public Optional<Attendance> getAttendanceById(int id) { return attendancesRepository.findById(id); }

    public List<Attendance> getAttendancesByDate(Date date) {
        return attendancesRepository.findByDate(date);
    }

    @Transactional
    public void saveAttendance(Attendance attendance) {
        attendancesRepository.save(attendance);
    }

    @Transactional
    public void saveAttendanceList(List<Attendance> attendanceList) {
        attendancesRepository.saveAll(attendanceList);
    }

    @Transactional
    public boolean updateAttendance(int id, Attendance updatedAttendance) {
        Optional<Attendance> attendanceById = attendancesRepository.findById(id);

        if (attendanceById.isEmpty())
            //Заменить на ошибку
            return false;

        Attendance attendanceToBeUpdated = attendanceById.get();

        updatedAttendance.setAttendanceId(id);
        updatedAttendance.setStudent(attendanceToBeUpdated.getStudent());
        updatedAttendance.setSchedule(attendanceToBeUpdated.getSchedule());
        attendancesRepository.save(updatedAttendance);

        return true;
    }

    @Transactional
    public void deleteAttendance(int id) {
        attendancesRepository.deleteById(id);
    }

    public Student getStudentByAttendance(int id) {
        return attendancesRepository.findById(id).map(Attendance::getStudent).orElse(null);
    }

    public Schedule getScheduleByAttendance(int id) {
        return attendancesRepository.findById(id).map(Attendance::getSchedule).orElse(null);
    }

    @Transactional
    public void assignStudent(int id, Student selectedStudent) {
        attendancesRepository.findById(id).ifPresent(
                attendance -> {
                    attendance.setStudent(selectedStudent);
                }
        );
    }

    @Transactional
    public void assignSchedule(int id, Schedule selectedSchedule) {
        attendancesRepository.findById(id).ifPresent(
                attendance -> {
                    attendance.setSchedule(selectedSchedule);
                }
        );
    }
}
