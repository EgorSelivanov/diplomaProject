package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.SpecialitiesRepository;

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

    public Optional<Speciality> findByNameLike(String name) {
        return specialitiesRepository.findByNameLike(name);
    }

    public Optional<Speciality> findByCodeLike(String code) {
        return specialitiesRepository.findByCodeLike(code);
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
