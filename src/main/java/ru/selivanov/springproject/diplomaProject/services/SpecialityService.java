package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.SpecialitiesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
