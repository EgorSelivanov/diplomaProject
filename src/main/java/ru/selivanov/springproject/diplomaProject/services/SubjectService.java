package ru.selivanov.springproject.diplomaProject.services;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.SubjectsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SubjectService {
    private final SubjectsRepository subjectsRepository;
    @Autowired
    public SubjectService(SubjectsRepository subjectsRepository) {
        this.subjectsRepository = subjectsRepository;
    }

    public List<Subject> findByNameLike(String name) {
        return subjectsRepository.findByNameLike(name);
    }

    public List<Subject> findByDescriptionLike(String description) {
        return subjectsRepository.findByDescriptionLike(description);
    }

    public Subject getSubjectById(int id) {
        return subjectsRepository.findById(id).orElse(null);
    }

    public List<Subject> getSubjects() {
        return subjectsRepository.findAllByOrderByName();
    }

    public List<Subject> getSubjectsSearch(String search) {
        search = "%" + search + "%";
        List<Subject> list = new ArrayList<>();
        list.addAll(subjectsRepository.findAllByNameLikeIgnoreCaseOrderByName(search));
        list.addAll(subjectsRepository.findAllByDescriptionLikeIgnoreCaseOrderByName(search));
        return list;
    }

    @Transactional
    public int createSubject(Subject subject) {
        subjectsRepository.save(subject);
        return subject.getSubjectId();
    }

    @Transactional
    public boolean updateSubject(int id, Subject updatedSubject) {
        Optional<Subject> subjectOptional = subjectsRepository.findById(id);

        if (subjectOptional.isEmpty())
            return false;

        Subject subjectToBeUpdated = subjectOptional.get();

        subjectToBeUpdated.setDescription(updatedSubject.getDescription());
        subjectToBeUpdated.setName(updatedSubject.getName());

        subjectsRepository.save(subjectToBeUpdated);
        return true;
    }

    @Transactional
    public void deleteSubject(int id) {
        subjectsRepository.deleteById(id);
    }

    public List<Workload> getWorkloadListBySubject(int id) {
        Optional<Subject> subjectOptional = subjectsRepository.findById(id);

        if (subjectOptional.isEmpty())
            //Изменить на кастомные
            throw new RuntimeException();

        Hibernate.initialize(subjectOptional.get().getWorkloadList());

        return subjectOptional.get().getWorkloadList();
    }
}
