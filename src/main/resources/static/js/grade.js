var dictionaryGrade = new Map();
//Обработчик изменяемых ячеек таблицы оценок
function editableGrades() {
    // Получаем все редактируемые ячейки таблицы
    const editableCells = document.querySelectorAll('.editable-grade');

    // Добавляем обработчик события input к каждой редактируемой ячейке
    editableCells.forEach(cell => {
        cell.addEventListener('input', handleInput);
    });

    // Обработчик события input
    function handleInput(event) {
        const input = event.target; // Текущая ячейка, в которой произошло изменение
        const val = input.value;

        const cell = input.parentNode;
        const studentId = cell.parentNode.getAttribute('data-student-id'); // Уникальный идентификатор студента, если необходимо
        const assignmentId = cell.getAttribute('data-assignment-id');

        const rowIndex = cell.parentNode.rowIndex;
        const cellIndex = cell.cellIndex;

        const key = `${rowIndex}-${cellIndex}`;
        const value = [ val, studentId, assignmentId ];
        dictionaryGrade.set(key, value);
    }
}

//Сохраняем изменения оценок на сервере
function saveChangesOfGrades(selectedDiscipline, selectedGroup, selectedType) {
    // Преобразование словаря в объект JSON
    const jsonData = {};
    for (let [key, value] of dictionaryGrade) {
        jsonData[key] = value;
    }

    if (jsonData.length === 0)
        return;

    const url = '/grade';
    const csrfToken = document.getElementById("csrfToken").value;
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка сохранения! Проверьте правильность введенных данных!");
            }
            return response.text();
        })
        .then(data => {
            customAlert("Сохранение успешно!");
            getGrades(selectedDiscipline, selectedGroup, selectedType);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Получаем журнал посещений по группе и дисциплине
function getGrades(selectedDiscipline, selectedGroup, selectedType) {
    fetch(`${teacherId}/grades?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            // Получаем элемент, в который будет добавлена таблица
            const gradesTable = document.querySelector('.grades-table');
            const gradesSaveBtn = document.querySelector('.grades-save');
            //Очищаем содержимое
            gradesTable.innerHTML = '';
            gradesSaveBtn.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица успеваемости: ";
            gradesTable.appendChild(h2Content);

            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                gradesTable.appendChild(pContent);
                return;
            }

            // Создаем таблицу для оценок
            const table = document.createElement('table');
            table.classList.add('scrollable-table');

            // Создаем заголовок таблицы
            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');

            // Добавляем столбцы в заголовок таблицы
            const numberHeader = document.createElement('th');
            numberHeader.textContent = '№';
            numberHeader.title = '№';
            headerRow.appendChild(numberHeader);
            const nameHeader = document.createElement('th');
            nameHeader.textContent = 'ФИО';
            nameHeader.title = 'ФИО';
            headerRow.appendChild(nameHeader);

            for (var i = 0; i < data[0].typeOfAssignmentList.length; i++) {
                const dateAndTypeHeader = document.createElement('th');
                dateAndTypeHeader.textContent = data[0].typeOfAssignmentList[i];
                dateAndTypeHeader.title = data[0].typeOfAssignmentList[i] + "/" + data[0].dateList[i] + "/" +
                    data[0].descriptionList[i];
                headerRow.appendChild(dateAndTypeHeader);
            }
            const summaryHeader = document.createElement('th');
            summaryHeader.textContent = 'Сумма';
            summaryHeader.title = 'Сумма';
            headerRow.appendChild(summaryHeader);

            thead.appendChild(headerRow);
            table.appendChild(thead);

            // Создаем тело таблицы оценок
            const tbody = document.createElement('tbody');
            var number = 1;

            data.forEach(student => {
                const row = document.createElement('tr');

                const numberOfStudentCell = document.createElement('td');
                numberOfStudentCell.textContent = number;
                number += 1;
                row.appendChild(numberOfStudentCell);

                const nameCell = document.createElement('td');
                nameCell.textContent = student.fullName;
                row.setAttribute('data-student-id', student.studentId);
                row.appendChild(nameCell);

                // в атрибутах содержатся id студента и назначения, нужно как-то изменить таблицу на редактируемую и отслеживать изменения баллов!!!

                var summ = 0;
                for (var i = 0; i < student.assignmentIdList.length; i++) {
                    const numberCell = document.createElement('td');
                    //делаем таблицу изменяемой
                    numberCell.classList.add('editable-cell');
                    numberCell.classList.add('editable-grade');
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.pattern = '[0-9]*';
                    input.required = true;
                    input.value = student.pointsList[i];
                    numberCell.title = student.pointsList[i] + "/" + student.maxPointsList[i];
                    input.min = 0;
                    input.max = student.maxPointsList[i];
                    numberCell.appendChild(input);

                    numberCell.setAttribute('data-assignment-id', student.assignmentIdList[i]);
                    if (student.pointsList[i] !== "")
                        summ += parseInt(student.pointsList[i]);
                    row.appendChild(numberCell);
                }

                const sumCell = document.createElement('td');
                sumCell.textContent = summ;
                sumCell.title = summ;
                sumCell.classList.add('sumCellGrade');
                row.appendChild(sumCell);

                tbody.appendChild(row);
            });

            // создаем label и input для коэффициента
            const label = document.createElement('label');
            label.textContent = 'Коэффициент для суммы: ';
            const inputK = document.createElement('input');
            inputK.classList.add('koeff');
            inputK.type = 'number';
            inputK.step = "0.1";
            inputK.value = '1';
            inputK.id = 'inputKgrade';
            label.setAttribute("for", "inputKgrade");
            gradesTable.appendChild(label);
            gradesTable.appendChild(inputK);
            let previousValue = "1";
            inputK.addEventListener("input", function(event) {
                const cells = document.querySelectorAll('.sumCellGrade');
                var k = parseFloat(event.target.value);
                if (isNaN(k))
                    return;
                cells.forEach(cell => {
                    var value = parseFloat(cell.textContent) * k / parseFloat(previousValue);
                    cell.textContent = value;
                    cell.textContent = value.toFixed(2);
                    cell.title = cell.textContent;
                })
                previousValue = event.target.value;
            });

            table.appendChild(tbody);

            // Добавляем таблицу
            gradesTable.appendChild(table);

            //Добавляем кнопку сохранения
            gradesSaveBtn.innerHTML = '';
            const buttonSaveGrade = document.createElement('button');
            buttonSaveGrade.setAttribute('id', 'saveChangesOfStudentsButton');
            buttonSaveGrade.classList.add('btn');
            buttonSaveGrade.classList.add('btn-primary');
            buttonSaveGrade.classList.add('btn-save-changes')
            buttonSaveGrade.textContent = 'Сохранить изменения';
            gradesSaveBtn.appendChild(buttonSaveGrade);

            dictionaryGrade = new Map();
            editableGrades();

            buttonSaveGrade.addEventListener('click', function() {
                saveChangesOfGrades(selectedDiscipline, selectedGroup, selectedType);
            });

            //кнопка скачивания Excel
            var buttonSaveExcelTable = document.createElement('button');
            buttonSaveExcelTable.setAttribute('id', 'saveExcelButton');
            buttonSaveExcelTable.classList.add('btn', 'btn-primary', 'btn-save-changes');
            buttonSaveExcelTable.textContent = 'Скачать в Excel';
            gradesSaveBtn.appendChild(buttonSaveExcelTable);
            buttonSaveExcelTable.addEventListener('click', function() {
                createExcel(selectedDiscipline, selectedGroup, selectedType);
            });

            //Загрузить Excel
            var divUploadExcel = document.createElement('div');
            divUploadExcel.classList.add('btn-save-changes');
            gradesSaveBtn.appendChild(divUploadExcel);

            var inputUploadExcel = document.createElement('input');
            inputUploadExcel.setAttribute('id', 'uploadExcelInput');
            inputUploadExcel.classList.add('btn-save-changes');
            inputUploadExcel.classList.add('btn');
            inputUploadExcel.type = 'file';
            inputUploadExcel.accept = '.xlsx';
            divUploadExcel.appendChild(inputUploadExcel);

            var buttonUploadExcel = document.createElement('button');
            buttonUploadExcel.setAttribute('id', 'uploadExcelButton');
            buttonUploadExcel.classList.add('btn', 'btn-primary', 'btn-save-changes');
            buttonUploadExcel.textContent = 'Загрузить из Excel';
            buttonUploadExcel.disabled = true;
            divUploadExcel.appendChild(buttonUploadExcel);
            buttonUploadExcel.addEventListener('click', function() {
                uploadExcel(selectedDiscipline, selectedGroup, selectedType, inputUploadExcel.files[0]);
            });

            inputUploadExcel.addEventListener('change', function() {
                if (inputUploadExcel.files.length > 1) {
                    inputUploadExcel.value = '';
                    customAlert("Выберите только 1 файл!");
                    return;
                }
                if (inputUploadExcel.files.length === 1 && !inputUploadExcel.files[0].name.endsWith('.xlsx')) {
                    inputUploadExcel.value = '';
                    customAlert("Выберите файл Excel!");
                    return;
                }
                customAlert("Обратите внимание, что будут обновлены только данные о количестве баллов студентов.\n" +
                    "Для изменения данных о работах воспользуйтесь соответствующей таблицей.\n" +
                    "Изменение в данных студентов приведет к ошибке обновления.");
                buttonUploadExcel.disabled = inputUploadExcel.files.length <= 0;
            });
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}


function createExcel(selectedDiscipline, selectedGroup, selectedType){
    fetch(`${teacherId}/download-excel?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => {
        if (response.ok) {
            return response.blob(); // Получение данных в виде Blob объекта
        } else {
            throw new Error('Ошибка при загрузке файла');
        }
    })
        .then(blobData => {
            const url = window.URL.createObjectURL(blobData); // Создание URL для скачивания файла
            const a = document.createElement('a');
            a.href = url;

            var nameOfSubject = document.getElementById('nameOfSubject').textContent;
            var groupName = document.getElementById('select-group-to-show');
            var selectedOption = groupName.options[groupName.selectedIndex];
            var selectedText = selectedOption.text;

            a.download = nameOfSubject + "_" + selectedType + "_" + selectedText + '.xlsx';
            document.body.appendChild(a);
            a.click(); // Имитация клика для скачивания файла
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url); // Освобождение ресурсов
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function uploadExcel(selectedDiscipline, selectedGroup, selectedType, file) {
    const csrfToken = document.getElementById("csrfToken").value;
    var formData = new FormData();
    formData.append('file', file);
    fetch(`${teacherId}/upload-excel?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            body: formData })
        .then(response => response.text())
        .then(data => {
            // Обработка ответа от сервера
            customAlert(data);
            var inputUploadExcel = document.getElementById('uploadExcelInput');
            var buttonUploadExcel = document.getElementById('uploadExcelButton');

            inputUploadExcel.value = '';
            buttonUploadExcel.disabled = true;
            getGrades(selectedDiscipline, selectedGroup, selectedType);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}