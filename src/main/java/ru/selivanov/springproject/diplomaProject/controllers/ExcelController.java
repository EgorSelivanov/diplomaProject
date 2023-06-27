package ru.selivanov.springproject.diplomaProject.controllers;

import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.selivanov.springproject.diplomaProject.services.ExcelService;
import ru.selivanov.springproject.diplomaProject.services.GroupService;


import java.io.ByteArrayOutputStream;

@RestController
public class ExcelController {
    private final GroupService groupService;
    private final ExcelService excelService;

    @Autowired
    public ExcelController(GroupService groupService, ExcelService excelService) {
        this.groupService = groupService;
        this.excelService = excelService;
    }

    @GetMapping("/teacher/{teacherId}/download-excel")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable("teacherId") int teacherId,
                                                @RequestParam("discipline") int subjectId,
                                                @RequestParam("group") int groupId,
                                                @RequestParam("type") String type) {
        Workbook workbook;
        try {
            workbook = excelService.createExcelFile(teacherId, subjectId, groupId, type);
        } catch (Exception ex) {
            // Обработка ошибок, если не удалось создать Excel файл
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Преобразуем рабочую книгу в массив байтов
        byte[] excelBytes;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            excelBytes = outputStream.toByteArray();
        } catch (Exception e) {
            // Обработка ошибок, если не удалось создать Excel файл
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // Настраиваем HTTP заголовки для скачивания файла
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", groupService.getGroupById(groupId).getName() + ".xlsx");

        // Возвращаем массив байтов с Excel файлом и заголовками в ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @PostMapping("/teacher/{teacherId}/upload-excel")
    public ResponseEntity<String> uploadExcelFile(@PathVariable("teacherId") int teacherId,
                                                  @RequestParam("discipline") int subjectId,
                                                  @RequestParam("group") int groupId,
                                                  @RequestParam("type") String type,
                                                  @RequestPart("file") MultipartFile file) {
        // Обработка загруженного файла
        if (!file.isEmpty()) {
            try {
                excelService.uploadExcelFile(file, teacherId, subjectId, groupId, type);
            }
            catch (Exception ex) {
                return new ResponseEntity<>("Ошибка: Файл содержит некорректные данные: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Файл успешно загружен", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Ошибка: Файл не найден", HttpStatus.BAD_REQUEST);
        }
    }
}
