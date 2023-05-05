package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.GroupsRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GroupService {
    private final GroupsRepository groupsRepository;

    @Autowired
    public GroupService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }

    public Optional<Group> findByName(String name) {
        return groupsRepository.findByName(name);
    }

    public List<Group> findBySpeciality(Speciality speciality) {
        return groupsRepository.findBySpecialityLike(speciality);
    }

    public List<Group> findByCourseNumber(int courseNumber) {
        return groupsRepository.findByCourseNumber(courseNumber);
    }

    @Transactional
    public boolean updateGroup(int id, Group updatedGroup) {
        Optional<Group> groupById = groupsRepository.findById(id);

        if (groupById.isEmpty())
            return false;

        Group groupToBeUpdated = groupById.get();

        Hibernate.initialize(groupToBeUpdated.getStudents());
        Hibernate.initialize(groupToBeUpdated.getWorkloadList());
        updatedGroup.setGroupId(id);
        updatedGroup.setSpeciality(groupToBeUpdated.getSpeciality());
        updatedGroup.setStudents(groupToBeUpdated.getStudents());
        updatedGroup.setWorkloadList(groupToBeUpdated.getWorkloadList());
        groupsRepository.save(updatedGroup);
        return true;
    }

    @Transactional
    public void deleteGroup(int id) {
        groupsRepository.deleteById(id);
    }

    public Speciality getSpecialityByGroup(int id) {
        return groupsRepository.findById(id).map(Group::getSpeciality).orElse(null);
    }

    @Transactional
    public void assignSpeciality(int id, Speciality selectedSpeciality) {
        groupsRepository.findById(id).ifPresent(
                group -> {
                    group.setSpeciality(selectedSpeciality);
                }
        );
    }

    public List<Student> getStudentsByGroup(int id) {
        Optional<Group> groupOptional = groupsRepository.findById(id);

        if (groupOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(groupOptional.get().getStudents());

        return groupOptional.get().getStudents();
    }

    public List<Workload> getWorkloadsByGroup(int id) {
        Optional<Group> groupOptional = groupsRepository.findById(id);

        if (groupOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(groupOptional.get().getWorkloadList());

        return groupOptional.get().getWorkloadList();
    }
}
