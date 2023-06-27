package ru.selivanov.springproject.diplomaProject.services;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.dto.GroupJSONDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.GroupsRepository;
import ru.selivanov.springproject.diplomaProject.repositories.SpecialitiesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class GroupService {
    private final GroupsRepository groupsRepository;
    private final SpecialitiesRepository specialitiesRepository;

    @Autowired
    public GroupService(GroupsRepository groupsRepository, SpecialitiesRepository specialitiesRepository) {
        this.groupsRepository = groupsRepository;
        this.specialitiesRepository = specialitiesRepository;
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

    public List<Group> getGroups() {
        return groupsRepository.findAllByOrderByCourseNumberAscNameAsc();
    }

    public List<Group> getGroups(int courseNumber) {
        return groupsRepository.findAllByCourseNumberOrderByCourseNumberAscNameAsc(courseNumber);
    }

    public List<Group> getGroupsSearch(String search) {
        search = "%" + search + "%";
        List<Speciality> specialityList = new ArrayList<>();
        specialityList.addAll(specialitiesRepository.findAllBySpecialityNameLikeIgnoreCaseOrderByCode(search));
        specialityList.addAll(specialitiesRepository.findAllByCodeLikeIgnoreCaseOrderByCode(search));

        List<Group> list = new ArrayList<>(groupsRepository.findAllByNameLikeIgnoreCase(search));
        for (Speciality speciality : specialityList) {
            list.addAll(groupsRepository.findAllBySpeciality(speciality));
        }
        return list;
    }

    public List<Group> getGroupsSearch(String search, Integer courseNumber) {
        search = "%" + search + "%";
        List<Speciality> specialityList = new ArrayList<>();
        specialityList.addAll(specialitiesRepository.findAllBySpecialityNameLikeIgnoreCaseOrderByCode(search));
        specialityList.addAll(specialitiesRepository.findAllByCodeLikeIgnoreCaseOrderByCode(search));

        List<Group> list = new ArrayList<>(groupsRepository.
                findAllByCourseNumberAndNameLikeIgnoreCaseOrderByCourseNumberAscNameAsc(courseNumber, search));

        for (Speciality speciality : specialityList) {
            list.addAll(groupsRepository.findAllByCourseNumberAndSpeciality(courseNumber, speciality));
        }
        return list;
    }

    public Group getGroupById(int id) {
        return groupsRepository.findById(id).orElse(null);
    }

    public List<Speciality> getSpecialityList() {
        return specialitiesRepository.findAllByOrderByCode();
    }

    @Transactional
    public int createGroup(Group group) {
        if (group == null || group.getName() == null || group.getName().trim().equals("") ||
                group.getCourseNumber() < 1 || group.getCourseNumber() > 6)
            return -1;
        groupsRepository.save(group);
        return group.getGroupId();
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
        //updatedGroup.setSpeciality(groupToBeUpdated.getSpeciality());
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

    @Transactional
    public void updateDataByJSON(@Valid List<GroupJSONDTO> groupJSONDTOList) throws NoSuchFieldException {
        List<Group> groupList = new ArrayList<>();
        for (GroupJSONDTO groupJSONDTO : groupJSONDTOList) {
            Group group = groupsRepository.findByName(groupJSONDTO.getName()).orElse(null);
            Group groupToUpdate = new Group();
            if (group == null) {
                groupToUpdate.setName(groupJSONDTO.getName());
                groupToUpdate.setCourseNumber(groupJSONDTO.getCourseNumber());
                Speciality speciality = searchSpeciality(groupJSONDTO);
                groupToUpdate.setSpeciality(speciality);
                groupList.add(groupToUpdate);
                continue;
            }

            Hibernate.initialize(group.getWorkloadList());
            Hibernate.initialize(group.getStudents());

            groupToUpdate.setName(group.getName());
            groupToUpdate.setGroupId(group.getGroupId());
            groupToUpdate.setStudents(group.getStudents());
            groupToUpdate.setWorkloadList(group.getWorkloadList());

            Speciality speciality = searchSpeciality(groupJSONDTO);
            groupToUpdate.setSpeciality(speciality);
            groupToUpdate.setCourseNumber(groupJSONDTO.getCourseNumber());
            groupList.add(groupToUpdate);
        }

        groupsRepository.saveAll(groupList);
    }

    private Speciality searchSpeciality(GroupJSONDTO groupJSONDTO) throws NoSuchFieldException {
        if (groupJSONDTO.getSpecialityName() != null && !groupJSONDTO.getSpecialityName().trim().equals("")) {
            Speciality speciality = specialitiesRepository.findBySpecialityName(groupJSONDTO.getSpecialityName().trim()).orElse(null);
            if (speciality == null)
                throw new NoSuchFieldException("Не найдено такой специальности: " + groupJSONDTO.getSpecialityName() +
                        " для группы: " + groupJSONDTO.getName());
            return speciality;
        }
        if (groupJSONDTO.getCode() != null && !groupJSONDTO.getCode().trim().equals("")) {
            Speciality speciality = specialitiesRepository.findByCode(groupJSONDTO.getCode().trim()).orElse(null);
            if (speciality == null)
                throw new NoSuchFieldException("Не найдено такой специальности: " + groupJSONDTO.getCode() +
                        " для группы: " + groupJSONDTO.getName());
            return speciality;
        }

        throw new NoSuchFieldException("Не указано специальности для группы: " + groupJSONDTO.getName());
    }
}
