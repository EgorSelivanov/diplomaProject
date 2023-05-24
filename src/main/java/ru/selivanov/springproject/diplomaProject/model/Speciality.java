package ru.selivanov.springproject.diplomaProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Speciality")
public class Speciality {
    @Id
    @Column(name = "speciality_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int specialityId;

    @Column(name = "speciality_name")
    @NotEmpty(message = "Название специальности не должно быть пустым!")
    private String specialityName;

    @Column(name = "code")
    @NotEmpty(message = "Код специальности не должен быть пустым!")
    @Size(min = 8, max = 10, message = "Код специальности должен содержать 8 или 10 символов")
    private String code;

    @OneToMany(mappedBy = "speciality")
    private List<Group> groupList;

    public Speciality() {}

    public Speciality(String specialityName) {
        this.specialityName = specialityName;
    }

    public int getSpecialityId() {
        return specialityId;
    }

    public void setSpecialityId(int specialityId) {
        this.specialityId = specialityId;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String name) {
        this.specialityName = name;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speciality that = (Speciality) o;
        return specialityName.equals(that.specialityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialityName);
    }
}
