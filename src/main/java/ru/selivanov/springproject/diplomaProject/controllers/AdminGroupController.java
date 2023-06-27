package ru.selivanov.springproject.diplomaProject.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.selivanov.springproject.diplomaProject.dto.GroupEditDTO;
import ru.selivanov.springproject.diplomaProject.dto.GroupJSONDTO;
import ru.selivanov.springproject.diplomaProject.model.Group;
import ru.selivanov.springproject.diplomaProject.model.Speciality;
import ru.selivanov.springproject.diplomaProject.services.AdminService;
import ru.selivanov.springproject.diplomaProject.services.GroupService;
import ru.selivanov.springproject.diplomaProject.services.SpecialityService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminGroupController {
    private final AdminService adminService;
    private final GroupService groupService;
    private final SpecialityService specialityService;
    private final ObjectMapper objectMapper;
    @Autowired
    public AdminGroupController(AdminService adminService, GroupService groupService, SpecialityService specialityService, ObjectMapper objectMapper) {
        this.adminService = adminService;
        this.groupService = groupService;
        this.specialityService = specialityService;
        this.objectMapper = objectMapper;
    }

    // -----begin-----Вкладка Группы-----begin-----
    @GetMapping("/{id}/groups")
    public String getGroupsPage(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", adminService.getUserById(id));
        return "admin/groups/group";
    }

    @GetMapping("/{id}/groupList")
    public String getGroupList(@PathVariable("id") int id, Model model,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "course", required = false) Integer courseNumber) {
        model.addAttribute("user", adminService.getUserById(id));
        if (search == null || search.trim().equals(""))
            if (courseNumber == null)
                model.addAttribute("groups", groupService.getGroups());
            else
                model.addAttribute("groups", groupService.getGroups(courseNumber));
        else
            if (courseNumber == null)
                model.addAttribute("groups", groupService.getGroupsSearch(search));
            else
                model.addAttribute("groups", groupService.getGroupsSearch(search, courseNumber));

        model.addAttribute("specialityList", groupService.getSpecialityList());

        return "admin/groups/groupList";
    }

    @GetMapping("/new-group")
    public String getNewGroupPage(Model model) {
        model.addAttribute("errorGroup", "");
        model.addAttribute("specialityList", groupService.getSpecialityList());
        return "admin/groups/modalNewGroup";
    }

    @PostMapping("/new-group")
    public String saveNewGroup(@Valid @RequestBody GroupEditDTO groupEditDTO,
                                    Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorGroup", getAllErrors(bindingResult));
            model.addAttribute("specialityList", groupService.getSpecialityList());
            return "/admin/groups/modalNewGroup";
        }

        Group group = groupEditDTO.getGroup();
        group.setSpeciality(specialityService.getSpecialityById(groupEditDTO.getSpecialityId()));

        groupService.createGroup(group);
        return "admin/empty";
    }

    // -----end-----Вкладка Группы-----end-----

    @ResponseBody
    @PostMapping("/{id}/edit-group/{groupEditId}")
    public ResponseEntity<String> editGroup(@PathVariable("id") int id, @PathVariable("groupEditId") int groupEditId,
                                                 @RequestBody @Valid GroupEditDTO group, BindingResult bindingResult) {
        if (groupService.getGroupById(groupEditId) == null)
            bindingResult.rejectValue("groupId", "Doesn't exist", "Группы с таким id не существует!");

        if (bindingResult.hasErrors())
            return ResponseEntity.badRequest().body(getAllErrors(bindingResult));

        Group groupToEdit = group.getGroup();
        Speciality speciality = specialityService.getSpecialityById(group.getSpecialityId());
        groupToEdit.setSpeciality(speciality);

        groupService.updateGroup(groupEditId, groupToEdit);

        return ResponseEntity.ok("Редактирование успешно!");
    }

    @ResponseBody
    @DeleteMapping("/{id}/deleteGroup")
    public ResponseEntity<String> deleteGroupById(@PathVariable("id") int id) {
        Group group = groupService.getGroupById(id);
        if (group == null)
            return ResponseEntity.ofNullable("Данной группы не найдено!");
        groupService.deleteGroup(id);
        return ResponseEntity.ok("Удаление успешно!");
    }

    private String getAllErrors(BindingResult bindingResult) {
        List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        // Преобразуем ошибки в текстовое представление
        return String.join(", ", errors);
    }

    @ResponseBody
    @GetMapping("/{id}/groupListForTeacher")
    public List<Group> getGroupList(@PathVariable("id") int id,
                               @RequestParam("courseNumber") Integer courseNumber) {
        List<Group> list = groupService.getGroups(courseNumber);
        for (Group group:list) {
            System.out.println(group.getName());
        }
        return groupService.getGroups(courseNumber);
    }

    @PostMapping("/{id}/group/upload-json")
    public ResponseEntity<String> uploadJSONFile(@PathVariable("id") int adminId,
                                                  @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                List<GroupJSONDTO> groupJSONDTO = objectMapper.readValue(file.getInputStream(), new TypeReference<List<GroupJSONDTO>>() {});
                groupService.updateDataByJSON(groupJSONDTO);
            }
            catch (NoSuchFieldException e) {
                return new ResponseEntity<>("Ошибка: Файл содержит некорректные данные: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (IOException e) {
                return new ResponseEntity<>("Ошибка при попытке чтения данных: " + e.getMessage(), HttpStatus.BAD_REQUEST);
            } catch (ConstraintViolationException e) {
                return new ResponseEntity<>("Ошибка: Файл содержит пустые или некорректные данные: " +
                        e.getConstraintViolations().stream().iterator().next().getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Файл успешно загружен", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Ошибка: Файл не найден", HttpStatus.BAD_REQUEST);
        }
    }
}
