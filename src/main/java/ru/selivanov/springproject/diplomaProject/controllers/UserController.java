package ru.selivanov.springproject.diplomaProject.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.selivanov.springproject.diplomaProject.dto.UserDTO;
import ru.selivanov.springproject.diplomaProject.model.User;
import ru.selivanov.springproject.diplomaProject.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<User> index() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User show(@PathVariable("id") int id) {
        if (userService.findById(id).isPresent())
            return userService.findById(id).get();
        else
            return null;
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid User user,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);

        userService.addUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid UserDTO userDTO, @PathVariable("id") int id,
                                             BindingResult bindingResult) {
        User user = convertToUser(userDTO);
        if (bindingResult.hasErrors()) {
            System.out.println(bindingResult.getErrorCount());
            return ResponseEntity.ok(HttpStatus.BAD_REQUEST);
        }

        userService.updateUser(id, user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private User convertToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO convertToMeasurementDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
