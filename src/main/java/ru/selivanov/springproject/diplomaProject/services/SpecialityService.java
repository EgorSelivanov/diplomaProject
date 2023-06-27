package ru.selivanov.springproject.diplomaProject.services;

import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.SpecialitiesRepository;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class SpecialityService {
    private final SpecialitiesRepository specialitiesRepository;

    @Autowired
    public SpecialityService(SpecialitiesRepository specialitiesRepository) {
        this.specialitiesRepository = specialitiesRepository;
    }

    public Speciality getSpecialityById(int id) {
        return specialitiesRepository.findById(id).orElse(null);
    }

    public List<Speciality> getSpecialities() {
        return specialitiesRepository.findAllByOrderByCode();
    }

    public List<Speciality> getSpecialitiesSearch(String search) {
        search = "%" + search + "%";
        List<Speciality> list = new ArrayList<>();
        list.addAll(specialitiesRepository.findAllBySpecialityNameLikeIgnoreCaseOrderByCode(search));
        list.addAll(specialitiesRepository.findAllByCodeLikeIgnoreCaseOrderByCode(search));
        return list;
    }

    public Optional<Speciality> findByName(String name) {
        return specialitiesRepository.findBySpecialityName(name);
    }

    public Optional<Speciality> findByCode(String code) {
        return specialitiesRepository.findByCode(code);
    }

    @Transactional
    public int createSpeciality(Speciality speciality) {
        if (speciality == null || speciality.getSpecialityName() == null || speciality.getSpecialityName().trim().equals("") ||
        speciality.getCode() == null || speciality.getCode().trim().equals(""))
            return -1;
        specialitiesRepository.save(speciality);
        return speciality.getSpecialityId();
    }

    @Transactional
    public boolean updateSpeciality(int id, Speciality updatedSpeciality) {
        Optional<Speciality> specialityOptional = specialitiesRepository.findById(id);

        if (specialityOptional.isEmpty())
            return false;

        Speciality specialityToBeUpdated = specialityOptional.get();

        Hibernate.initialize(specialityToBeUpdated.getGroupList());
        updatedSpeciality.setSpecialityId(id);
        updatedSpeciality.setGroupList(specialityToBeUpdated.getGroupList());

        specialitiesRepository.save(updatedSpeciality);
        return true;
    }

    @Transactional
    public void deleteSpeciality(int id) {
        specialitiesRepository.deleteById(id);
    }

    public List<Group> getGroupsByAssignment(int id) {
        Optional<Speciality> specialityOptional = specialitiesRepository.findById(id);

        if (specialityOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(specialityOptional.get().getGroupList());

        return specialityOptional.get().getGroupList();
    }

    @Transactional
    public void updateDataByJSON(@Valid List<Speciality> specialityList) throws NoSuchFieldException {
        List<Speciality> specialityListToUpdate = new ArrayList<>();
        Set<String> setCodes = new HashSet<>();
        for (Speciality speciality : specialityList) {
            speciality.setSpecialityName(speciality.getSpecialityName().trim());
            speciality.setCode(speciality.getCode().trim());
            if (setCodes.contains(speciality.getCode()))
                throw new NoSuchFieldException("Встречено повторение кода специальности: " + speciality.getCode());
            Speciality specialityToUpdate = specialitiesRepository.findByCode(speciality.getCode()).orElse(null);
            if (specialityToUpdate == null) {
                specialityToUpdate = specialitiesRepository.findBySpecialityName(speciality.getSpecialityName()).orElse(null);
                if (specialityToUpdate == null)
                    specialityToUpdate = new Speciality();
                specialityToUpdate.setSpecialityName(speciality.getSpecialityName());
                specialityToUpdate.setCode(speciality.getCode());
                setCodes.add(speciality.getCode());
                specialityListToUpdate.add(specialityToUpdate);
                continue;
            }

            specialityToUpdate.setSpecialityName(speciality.getSpecialityName());
            setCodes.add(speciality.getCode());
            specialityListToUpdate.add(specialityToUpdate);
        }

        specialitiesRepository.saveAll(specialityListToUpdate);
    }
}
