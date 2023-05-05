package ru.selivanov.springproject.diplomaProject.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PasswordDTO {
    @NotEmpty(message = "Пароль не может быть пустым!")
    @Size(min = 6, max = 255, message = "Пароль должен быть длиной минимум 6 символов!")
    private String password;

    @NotEmpty(message = "Пароль не может быть пустым!")
    @Size(min = 6, max = 255, message = "Пароль должен быть длиной минимум 6 символов!")
    private String newPassword;

    @NotEmpty(message = "Пароль не может быть пустым!")
    @Size(min = 6, max = 255, message = "Пароль должен быть длиной минимум 6 символов!")
    private String newPasswordConfirmation;

    public PasswordDTO(String password, String newPassword, String newPasswordConfirmation) {
        this.password = password;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
