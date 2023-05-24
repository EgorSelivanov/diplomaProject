package ru.selivanov.springproject.diplomaProject.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.selivanov.springproject.diplomaProject.model.Speciality;
import ru.selivanov.springproject.diplomaProject.services.SpecialityService;

import java.util.Optional;

@Component
public class SpecialityValidator implements Validator {
    private final SpecialityService specialityService;

    public SpecialityValidator(SpecialityService specialityService) {
        this.specialityService = specialityService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return Speciality.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Speciality speciality = (Speciality) target;

        if (speciality.getCode().length() != 8 && speciality.getCode().length() != 10)
            errors.rejectValue("code", "Length", "Код специальности должен содержать 8 или 10 символов");

        if (speciality.getSpecialityName() == null || speciality.getSpecialityName().trim().equals("") ||
                speciality.getCode() == null || speciality.getCode().trim().equals(""))
            errors.rejectValue("code", "Null", "Вы заполнили не все поля!");

        Optional<Speciality> specialityOptional = specialityService.findByCode(speciality.getCode());
        if (specialityOptional.isPresent() && speciality.getSpecialityId() != specialityOptional.get().getSpecialityId()) {
            errors.rejectValue("code", "Already exists", "Специальность с таким кодом уже существует!");
        }
    }
}
