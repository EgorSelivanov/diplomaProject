package ru.selivanov.springproject.diplomaProject.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.selivanov.springproject.diplomaProject.dto.GradesOfStudentsToShowDTO;
import ru.selivanov.springproject.diplomaProject.model.*;
import ru.selivanov.springproject.diplomaProject.repositories.*;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    private final WorkloadsRepository workloadsRepository;
    private final AssignmentsRepository assignmentsRepository;
    private final GradesRepository gradesRepository;
    private final TeachersRepository teachersRepository;
    private final TeacherService teacherService;
    private final SubjectsRepository subjectsRepository;
    private final GroupsRepository groupsRepository;
    private final StudentsRepository studentsRepository;
    private Workbook workbook;
    private  CellStyle cellStyle;

    @Autowired
    public ExcelService(WorkloadsRepository workloadsRepository, AssignmentsRepository assignmentsRepository, GradesRepository gradesRepository, TeachersRepository teachersRepository, TeacherService teacherService, SubjectsRepository subjectsRepository, GroupsRepository groupsRepository, StudentsRepository studentsRepository) {
        this.workloadsRepository = workloadsRepository;
        this.assignmentsRepository = assignmentsRepository;
        this.gradesRepository = gradesRepository;
        this.teachersRepository = teachersRepository;
        this.teacherService = teacherService;
        this.subjectsRepository = subjectsRepository;
        this.groupsRepository = groupsRepository;
        this.studentsRepository = studentsRepository;
    }

    public void uploadExcelFile(MultipartFile file, int teacherId, int subjectId, int groupId, String type) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());

        // Предполагается, что у вас есть только один лист в книге
        Sheet sheet = workbook.getSheetAt(0);

        List<String> assignmentList = new ArrayList<>();
        Map<Integer, GradesOfStudentsToShowDTO> map = new HashMap<>();
        int lastCellNum = 0;

        // Перебираем все строки в листе
        Iterator<Row> rowIterator = sheet.iterator();
        int rowNum = 0;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            int indexStudent = 0;
            // Перебираем все ячейки в строке
            int cellNum = 0;
            if (rowNum == 0) {
                assignmentList = processHeaderRow(row);
                lastCellNum = assignmentList.size() + 2;
                rowNum++;
                continue;
            }
            Iterator<Cell> cellIterator = row.iterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                // Получаем значение ячейки в зависимости от её типа
                switch (cell.getCellType()) {
                    case STRING:
                        if (cellNum == 1) {
                            map.get(indexStudent).setFullName(cell.getStringCellValue());
                            break;
                        }
                        if (cell.getStringCellValue().trim().equals(""))
                            map.get(indexStudent).addPoints("");
                        else
                            throw new IOException("Данные баллов содержат строковый тип! Ячейка: " + ++rowNum + ":" + cellNum);
                        break;
                    case NUMERIC:
                        if (cellNum == 0) {
                            indexStudent = (int) cell.getNumericCellValue();
                            map.put(indexStudent, new GradesOfStudentsToShowDTO());
                            break;
                        }
                        if (cellNum == lastCellNum)
                            break;
                        map.get(indexStudent).addPoints(String.valueOf(cell.getNumericCellValue()).replace(".0", ""));
                        break;
                    case BLANK:
                        if (cellNum == 0 || cellNum == 1 || cellNum >= lastCellNum)
                            throw new IOException("Пустые данные в данных студента! Ячейка: " + ++rowNum + ":" + cellNum);
                        map.get(indexStudent).addPoints("");
                        break;
                    case FORMULA:
                        break;
                    default:
                        throw new IOException("Неверный формат данных! Ячейка: " + ++rowNum + ":" + cellNum);
                }
                cellNum++;
            }
            rowNum++;
        }

        workbook.close();
        updateData(assignmentList, map, teacherId, subjectId, groupId, type);
    }

    private List<String> processHeaderRow(Row row) throws IOException {
        List<String> assignmentList = new ArrayList<>();
        Iterator<Cell> cellIterator = row.iterator();
        int cellNum = 0;
        int lastCellNum = 0;
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            // Получаем значение ячейки в зависимости от её типа
            switch (cell.getCellType()) {
                case STRING:
                    if (cellNum == 0 && !cell.getStringCellValue().equals("№"))
                        throw new IOException("Первый столбец должен содержать номера студентов!");
                    if (cellNum == 1 && !cell.getStringCellValue().equals("ФИО"))
                        throw new IOException("Второй столбец должен содержать ФИО студентов!");
                    if (cell.getStringCellValue().equals("Сумма"))
                        lastCellNum = cellNum;
                    if (lastCellNum != 0 && cellNum > lastCellNum)
                        throw new IOException("Данные должны быть до столбца суммы!");
                    if (cellNum >= 2 && !cell.getStringCellValue().equals("Сумма"))
                        assignmentList.add(cell.getStringCellValue());
                    break;
                default:
                    throw new IOException();
            }
            cellNum++;
        }
        return assignmentList;
    }

    private void updateData(List<String> assignmentList, Map<Integer, GradesOfStudentsToShowDTO> map,
                            int teacherId, int subjectId, int groupId, String type) throws IOException {
        Teacher teacher = teachersRepository.findById(teacherId).orElse(null);
        Subject subject = subjectsRepository.findById(subjectId).orElse(null);
        Group group = groupsRepository.findById(groupId).orElse(null);
        Workload workload = workloadsRepository.findByTeacherAndSubjectAndGroupAndType(teacher, subject, group, type).orElse(null);
        if (workload == null)
            throw new IOException("Не удалось найти такого назначения!");

        List<GradesOfStudentsToShowDTO> list = teacherService.getGradesList(teacherId, subjectId, groupId, type);
        if (list.get(0).getTypeOfAssignmentList().size() != assignmentList.size())
            throw new IOException("Не совпадает количество проведенных работ!");

        List<Grade> gradeList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GradesOfStudentsToShowDTO studentsToShowDTO = list.get(i);
            GradesOfStudentsToShowDTO updateDTO = map.get(i + 1);
            if (updateDTO == null || !studentsToShowDTO.getFullName().equals(updateDTO.getFullName()))
                throw new IOException("Не совпадает список студентов!");

            for (int j = 0; j < studentsToShowDTO.getAssignmentIdList().size(); j++) {
                String nameOfAssignment = studentsToShowDTO.getTypeOfAssignmentList().get(j);
                String[] excelAssignment = assignmentList.get(j).split("\n");
                String nameOfAssignmentExcel = excelAssignment[0];

                String dateOfAssignment = studentsToShowDTO.getDateList().get(j);
                String dateOfAssignmentExcel = excelAssignment[1].replace("Дата: ", "");

                Integer maxPoints = studentsToShowDTO.getMaxPointsList().get(j);
                Integer maxPointsExcel = Integer.valueOf(excelAssignment[2].replace("Макс. баллы: ", ""));

                if (!nameOfAssignment.equals(nameOfAssignmentExcel) || !dateOfAssignment.equals(dateOfAssignmentExcel) ||
                        !maxPointsExcel.equals(maxPoints))
                    throw new IOException("Не совпадают данные проведенных работ!");

                Integer updatedPoints = null;
                if (!updateDTO.getPointsList().get(j).equals(""))
                    updatedPoints = Integer.parseInt(updateDTO.getPointsList().get(j));
                else {
                    if (studentsToShowDTO.getPointsList().get(j).equals(""))
                        continue;
                    updatedPoints = 0;
                }
                if (studentsToShowDTO.getMaxPointsList().get(j) < updatedPoints)
                    throw new IOException("Для работы " + studentsToShowDTO.getTypeOfAssignmentList().get(j) +
                            " превышен балл у ученика " + studentsToShowDTO.getFullName() + ". Максимальный балл для работы: " +
                            studentsToShowDTO.getMaxPointsList().get(j));

                Grade grade = gradesRepository.findByStudentAndAssignment(studentsRepository.findById(studentsToShowDTO.getStudentId()).orElse(null),
                        assignmentsRepository.findById(studentsToShowDTO.getAssignmentIdList().get(j)).orElse(null)).orElse(null);

                if (grade == null) {
                    grade = new Grade();
                    grade.setStudent(studentsRepository.findById(studentsToShowDTO.getStudentId()).orElse(null));
                    grade.setAssignment(assignmentsRepository.findById(studentsToShowDTO.getAssignmentIdList().get(j)).orElse(null));
                }
                grade.setPoints(updatedPoints);
                gradeList.add(grade);
            }
        }

        gradesRepository.saveAll(gradeList);
    }

    public Workbook createExcelFile(int teacherId, int subjectId, int groupId, String type) throws NoSuchFieldException {
        // Создаем новую рабочую книгу Excel
        this.workbook = new XSSFWorkbook();
        cellStyle = createStyle();

        // Создаем лист в книге
        Sheet sheet = workbook.createSheet("Лист 1");

        // счетчик для строк
        int rowNum = 0;

        Teacher teacher = teachersRepository.findById(teacherId).orElse(null);
        Subject subject = subjectsRepository.findById(subjectId).orElse(null);
        Group group = groupsRepository.findById(groupId).orElse(null);
        Workload workload = workloadsRepository.findByTeacherAndSubjectAndGroupAndType(teacher, subject, group, type).orElse(null);
        if (workload == null)
            throw new NoSuchFieldException("Не удалось найти такого назначения!");


        List<GradesOfStudentsToShowDTO> list = teacherService.getGradesList(teacherId, subjectId, groupId, type);

        createHeaderRow(sheet, list.get(0), rowNum);

        for (GradesOfStudentsToShowDTO studentsToShowDTO : list) {
            createRow(sheet, studentsToShowDTO, ++rowNum);
        }

        return workbook;
    }

    private void createHeaderRow(Sheet sheet, GradesOfStudentsToShowDTO studentsToShowDTO, int rowNum) {
        CellStyle style = createStyleBold(workbook);
        // Создаем строки и ячейки с данными
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue("№");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("ФИО");
        cell.setCellStyle(style);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        int cellNum = 1;
        for (int i = 0; i < studentsToShowDTO.getTypeOfAssignmentList().size(); i++) {
            cell = row.createCell(++cellNum);
            cell.setCellValue(studentsToShowDTO.getTypeOfAssignmentList().get(i) + "\nДата: " +
                    studentsToShowDTO.getDateList().get(i) + "\nМакс. баллы: " + studentsToShowDTO.getMaxPointsList().get(i));
            cell.setCellStyle(style);
            sheet.autoSizeColumn(cellNum);
        }
        /*for (String name : studentsToShowDTO.getTypeOfAssignmentList()) {
            cell = row.createCell(++cellNum);
            cell.setCellValue(name);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(cellNum);
        }*/

        cell = row.createCell(++cellNum);
        cell.setCellValue("Сумма");
        cell.setCellStyle(style);
        sheet.autoSizeColumn(cellNum);
    }

    private CellStyle createStyleBold(Workbook workbook) {
        // Создание шрифта
        XSSFFont font = (XSSFFont) workbook.createFont();

        font.setFontHeightInPoints((short)14);
        font.setFontName("Times New Roman");
        font.setBold(true);

        // Создание стиля с определением в нем шрифта
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);

        return style;
    }

    private void createRow(Sheet sheet, GradesOfStudentsToShowDTO studentsToShowDTO, int rowNum) {
        Row row = sheet.createRow(rowNum);

        createCell(sheet, row, 0, rowNum);
        createCell(sheet, row, 1, studentsToShowDTO.getFullName());

        int cellNum = 1;
        for (String grade : studentsToShowDTO.getPointsList()) {
            Integer value;
            try {
                value = Integer.valueOf(grade);
                createCell(sheet, row, ++cellNum, value);
            }
            catch (Exception e) {
                createCell(sheet, row, ++cellNum, grade);
            }
        }

        int number = cellNum;  // Число, для которого нужно получить букву
        char letter = (char) ('A' + number);  // Получение буквы по числу
        String formula = String.format("SUM(C%d:%s%d)", rowNum + 1, letter, rowNum + 1);

        Cell cell = row.createCell(++cellNum);
        cell.setCellFormula(formula);
        sheet.autoSizeColumn(cellNum);

        cell.setCellStyle(this.cellStyle);
    }

    private void createCell(Sheet sheet, Row row, int cellNum, int value) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        sheet.autoSizeColumn(cellNum);

        cell.setCellStyle(this.cellStyle);
    }

    private void createCell(Sheet sheet, Row row, int cellNum, String value) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        sheet.autoSizeColumn(cellNum);

        cell.setCellStyle(this.cellStyle);
    }

    private CellStyle createStyle() {
        // Определение стиля
        CellStyle cellStyle = workbook.createCellStyle();
        // Настройка выравнивания стиля
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Создание шрифта
        XSSFFont font = (XSSFFont) workbook.createFont();

        font.setFontHeightInPoints((short)14);
        font.setFontName("Times New Roman");
        cellStyle.setFont(font);
        return cellStyle;
    }
}
