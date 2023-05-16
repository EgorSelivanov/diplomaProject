//Добавление нового расписания
const teacherId = document.querySelector('#teacher-id').getAttribute("value");

// Найти кнопку "Добавить занятие"
var addScheduleButton = document.getElementById('addScheduleButton');

// Добавить обработчик события клика на кнопку
addScheduleButton.addEventListener('click', function () {
    // Отправить GET-запрос к серверу по адресу "teacher/newschedule"
    // и обработать полученную модальную страницу
    fetch('/teacher/new-schedule')
        .then(function (response) {
            return response.text();
        })
        .then(function (modalHtml) {
            // Создать модальное окно и поместить в него полученную страницу
            var modal = document.getElementById('modal-div');
            modal.innerHTML = modalHtml;

            // Отобразить модальное окно
            modal.style.display = 'block';

            // Закрытие модального окна при клике на крестик
            var closeBtn = modal.querySelector('.close');
            closeBtn.addEventListener('click', function () {
                modal.style.display = 'none';
            });

            initFlatpickr();
            fetchSubjects();
            fetchGroups();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});



//Модальная страница
// Инициализация flatpickr для полей времени начала и окончания
function initFlatpickr() {
    var selectors = document.querySelectorAll('.timepicker');
    console.log(selectors.length);
    selectors.forEach(function (selector) {
        console.log(selector);
        flatpickr(selector, {
            enableTime: true,
            noCalendar: true,
            dateFormat: 'H:i',
            time_24hr: true,
        });
    })
}

// Функция для получения данных для выпадающего списка дисциплин
function fetchSubjects() {
    fetch(`${teacherId}/subjects`)
        .then(response => response.json())
        .then(data => {
            const subjectSelect = document.getElementById('subject');
            if (!subjectSelect)
                return;
            data.forEach(subject => {
                const option = document.createElement('option');
                option.value = subject.subjectId;
                option.textContent = subject.name;
                subjectSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Ошибка при получении данных для выпадающего списка дисциплин:', error);
        });
}

// Функция для получения данных для выпадающего списка групп
function fetchGroups() {
    fetch(`${teacherId}/groups`)
        .then(response => response.json())
        .then(data => {
            const groupSelect = document.getElementById('group');
            if (!groupSelect)
                return;
            data.forEach(group => {
                const option = document.createElement('option');
                option.value = group.groupId;
                option.textContent = group.name;
                groupSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Ошибка при получении данных для выпадающего списка групп:', error);
        });
}



//отображение расписания
// функция, которая отправляет GET запрос на сервер и получает данные для таблицы расписания
function getScheduleDataByDate(date) {
    // отправляем GET запрос на сервер с параметром discipline
    fetch(`${teacherId}/schedule?date=${date}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            // получаем элемент table с id schedule-table
            const scheduleTable = document.getElementById('scheduleTable');

            // создаем таблицу и заголовок таблицы
            var tbody = scheduleTable.querySelector('tbody');
            if (tbody === undefined || tbody === null) {
                tbody = document.createElement('tbody');
                scheduleTable.appendChild(tbody);
            }

            //очищаем
            tbody.innerHTML = '';

            // если полученных данных нет, оставляем таблицу пустой
            if (Array.isArray(data) && data.length === 0) {
                return;
            }

            // заполняем таблицу данными
            data.forEach(item => {
                const row = document.createElement('tr');
                const cells = [item.dayOfWeek, item.startTimeFormat, item.endTimeFormat, item.name, item.type, item.audience,
                    item.groupName, item.courseNumber];
                cells.forEach(cell => {
                    const td = document.createElement('td');
                    td.textContent = cell;
                    row.appendChild(td);
                });
                tbody.appendChild(row);
            });
        })
        .catch(error => console.error(error));
}

//Обработчик изменяемых ячеек таблицы посещений
function editableAttendances() {
    // Получаем все редактируемые ячейки таблицы
    const editableCells = document.querySelectorAll('.editable-attendance');

    // Добавляем обработчик события input к каждой редактируемой ячейке
    editableCells.forEach(cell => {
        cell.addEventListener('input', handleInput);
    });

    // Обработчик события input
    function handleInput(event) {
        const input = event.target; // Текущая ячейка, в которой произошло изменение
        const val = input.value;

        const cell = input.parentNode;
        const attendanceId = cell.getAttribute('attendance-id');

        const key = `${attendanceId}`;
        dictionaryAttendance.set(key, val);
    }
}

// Словари для контроля за изменением ячеек
var dictionaryGrade = new Map();
var dictionaryAssignment = new Map();
var dictionaryAttendance = new Map();

//Сохраняем изменения посещений на сервере
function saveChangesOfAttendance(selectedDiscipline, selectedGroup, selectedType, text) {
    // Преобразование словаря в объект JSON
    const jsonData = {};
    for (let [key, value] of dictionaryAttendance) {
        jsonData[key] = value;
    }

    if (jsonData.length === 0)
        return;

    const url = '/attendance';
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
            getAttendances(selectedDiscipline, selectedGroup, selectedType, text);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert(error.message);
        });
}

//Получаем журнал посещений по группе и дисциплине
function getAttendances(selectedDiscipline, selectedGroup, selectedType, text) {
    fetch(`${teacherId}/journal?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            // Получаем элемент, в который будет добавлена таблица
            const journalTable = document.querySelector('.journal-table');
            //Очищаем содержимое
            journalTable.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица посещаемости по дисциплине: " + text;
            journalTable.appendChild(h2Content);

            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                journalTable.appendChild(pContent);
                return;
            }

            // Создаем таблицу
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

            data[0].dateList.forEach(date => {
                const dateHeader = document.createElement('th');
                dateHeader.textContent = date;
                dateHeader.title = date;
                headerRow.appendChild(dateHeader);
            });

            const summaryHeader = document.createElement('th');
            summaryHeader.textContent = 'Сумма';
            summaryHeader.title = 'Сумма';
            headerRow.appendChild(summaryHeader);

            thead.appendChild(headerRow);
            table.appendChild(thead);

            // Создаем тело таблицы
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
                nameCell.title = student.fullName;
                row.appendChild(nameCell);

                var summ = 0;
                for (var i = 0; i < student.attendanceIdList.length; i++) {
                    const numberCell = document.createElement('td');
                    //делаем таблицу изменяемой
                    numberCell.classList.add('editable-cell');
                    numberCell.classList.add('editable-attendance');
                    numberCell.setAttribute('attendance-id', student.attendanceIdList[i]);
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.value = student.presentList[i];
                    summ += student.presentList[i];
                    numberCell.appendChild(input);
                    row.appendChild(numberCell);
                }
                const sumCell = document.createElement('td');
                sumCell.textContent = summ;
                sumCell.title = summ;
                row.appendChild(sumCell);

                tbody.appendChild(row);
            });

            table.appendChild(tbody);

            // Добавляем таблицу
            journalTable.appendChild(table);

            //Добавляем кнопку сохранения
            const journalSaveBtn = document.querySelector('.journal-save');
            journalSaveBtn.innerHTML = '';
            const buttonSaveAttendance = document.createElement('button');
            buttonSaveAttendance.setAttribute('id', 'saveChangesOfStudentsButton');
            buttonSaveAttendance.classList.add('btn');
            buttonSaveAttendance.classList.add('btn-primary');
            buttonSaveAttendance.classList.add('btn-save-changes')
            buttonSaveAttendance.textContent = 'Сохранить изменения';
            journalSaveBtn.appendChild(buttonSaveAttendance);

            dictionaryAttendance = new Map();
            editableAttendances();

            buttonSaveAttendance.addEventListener('click', function() {
                saveChangesOfAttendance(selectedDiscipline, selectedGroup, selectedType, text);
            });

        })
        .catch(error => console.error(error));
}

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
        console.log(`Изменено значение ячейки: ${val}, студент: ${studentId}, назначение: ${assignmentId}, index: ${index}`);
    }
}

//Сохраняем изменения оценок на сервере
function saveChangesOfGrades(selectedDiscipline, selectedGroup, selectedType, text) {
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
            getGrades(selectedDiscipline, selectedGroup, selectedType, text);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert(error.message);
        });
}

//Получаем журнал посещений по группе и дисциплине
function getGrades(selectedDiscipline, selectedGroup, selectedType, text) {
    fetch(`${teacherId}/grades?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            // Получаем элемент, в который будет добавлена таблица
            const gradesTable = document.querySelector('.grades-table');
            //Очищаем содержимое
            gradesTable.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица успеваемости по дисциплине: " + text;
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
                for (var i = 0; i < student.pointsList.length; i++) {
                    const numberCell = document.createElement('td');
                    //делаем таблицу изменяемой
                    numberCell.classList.add('editable-cell');
                    numberCell.classList.add('editable-grade');
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.pattern = '[0-9]*';
                    input.required = true;
                    input.value = student.pointsList[i];
                    input.min = 0;
                    input.max = student.maxPointsList[i];
                    numberCell.appendChild(input);

                    numberCell.title = student.pointsList[i] + "/" + student.maxPointsList[i];
                    numberCell.setAttribute('data-assignment-id', student.assignmentIdList[i]);
                    if (student.pointsList[i] !== "-")
                        summ += parseInt(student.pointsList[i]);
                    row.appendChild(numberCell);
                }

                const sumCell = document.createElement('td');
                sumCell.textContent = summ;
                sumCell.title = summ;
                row.appendChild(sumCell);

                tbody.appendChild(row);
            });

            table.appendChild(tbody);

            // Добавляем таблицу
            gradesTable.appendChild(table);

            //Добавляем кнопку сохранения
            const gradesSaveBtn = document.querySelector('.grades-save');
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
                saveChangesOfGrades(selectedDiscipline, selectedGroup, selectedType, text);
            });

        })
        .catch(error => console.error(error));
}

//Сохраняем изменения назначений на сервере
function saveChangesOfAssignments(selectedDiscipline, selectedGroup, selectedType, text) {
    // Преобразование словаря в объект JSON
    const jsonData = {};
    for (let [key, value] of dictionaryAssignment) {
        jsonData[key] = value;
    }

    if (jsonData.length === 0)
        return;

    const url = '/assignment';
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
            getGrades(selectedDiscipline, selectedGroup, selectedType, text);
            getAttendances(selectedDiscipline, selectedGroup, selectedType, text);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            alert(error.message);
        });

}

//Обработчик изменяемых ячеек таблицы посещений
function editableAssignment() {
    // Получаем все редактируемые ячейки таблицы
    const editableCells = document.querySelectorAll('.editable-assignment');

    // Добавляем обработчик события input к каждой редактируемой ячейке
    editableCells.forEach(cell => {
        cell.addEventListener('input', handleInput);
    });

    // Обработчик события input
    function handleInput(event) {
        const inputTarget = event.target;

        const cell = inputTarget.parentNode;
        const row = cell.parentNode;
        const assignmentId = row.getAttribute('data-assignment-id');

        const cells = row.querySelectorAll('td'); // Выбираем все ячейки в строке

        var arrayOfInput = [];
        cells.forEach(cell => {
            const input = cell.querySelector('input');
            arrayOfInput.push(input);
        });

        var type = arrayOfInput[0].value;
        var description = arrayOfInput[1].value;
        var maxPoints = arrayOfInput[2].value;
        var date = arrayOfInput[3].value;

        const key = `${assignmentId}`;
        const value = [ type, description, maxPoints, date ];
        dictionaryAssignment.set(key, value);
    }
}

//Отображение назначений
function getAssignments(selectedDiscipline, selectedGroup, selectedType, text) {
    fetch(`${teacherId}/assignments?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            //Получаем элемент для таблицы назначений
            const assignmentTable = document.querySelector('.assignment-table');
            assignmentTable.innerHTML = '';

            // создаем подпись таблицы
            const h2AssignmentContent = document.createElement('h2');
            h2AssignmentContent.textContent = "Проводимые работы по дисциплине: " + text;
            assignmentTable.appendChild(h2AssignmentContent);

            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                assignmentTable.appendChild(pContent);
                return;
            }

            // таблица для назначений
            const tableAssignment = document.createElement('table');
            tableAssignment.classList.add('scrollable-table');

            // Создаем заголовок таблицы
            const theadAssignment = document.createElement('thead');
            const headerRowAssignment = document.createElement('tr');

            // Добавляем столбцы в заголовок таблицы
            const typeHeaderAssignment = document.createElement('th');
            typeHeaderAssignment.textContent = 'Вид работы';
            typeHeaderAssignment.title = 'Вид работы';
            headerRowAssignment.appendChild(typeHeaderAssignment);
            const descHeaderAssignment = document.createElement('th');
            descHeaderAssignment.textContent = 'Описание';
            descHeaderAssignment.title = 'Описание';
            headerRowAssignment.appendChild(descHeaderAssignment);
            const pointsHeaderAssignment = document.createElement('th');
            pointsHeaderAssignment.textContent = 'Макс. баллы';
            pointsHeaderAssignment.title = 'Максимальное количество баллов';
            headerRowAssignment.appendChild(pointsHeaderAssignment);
            const dateHeaderAssignment = document.createElement('th');
            dateHeaderAssignment.textContent = 'Дата';
            dateHeaderAssignment.title = 'Дата';
            headerRowAssignment.appendChild(dateHeaderAssignment);

            theadAssignment.appendChild(headerRowAssignment);
            tableAssignment.appendChild(theadAssignment);
            // Создаем тело таблицы назначений
            const tbodyAssignment = document.createElement('tbody');

            //--- заголовки таблицы для назначений

            data.forEach(assignment => {
                //Строки для таблицы назначений
                const row = document.createElement('tr');
                row.setAttribute('data-assignment-id', assignment.assignmentId);

                const typeCell = document.createElement('td');
                typeCell.classList.add('editable-cell');
                typeCell.classList.add('editable-assignment');
                const inputType = document.createElement('input');
                inputType.type = 'text';
                inputType.required = true;
                inputType.value = assignment.type;
                typeCell.appendChild(inputType);
                row.appendChild(typeCell);

                const descrCell = document.createElement('td');
                descrCell.classList.add('editable-cell');
                descrCell.classList.add('editable-assignment');
                const inputDescr = document.createElement('input');
                inputDescr.type = 'text';
                inputDescr.required = false;
                inputDescr.value = assignment.description;
                descrCell.appendChild(inputDescr);
                row.appendChild(descrCell);

                const pointCell = document.createElement('td');
                pointCell.classList.add('editable-cell');
                pointCell.classList.add('editable-assignment');
                const inputPoint = document.createElement('input');
                inputPoint.type = 'number';
                inputPoint.required = true;
                inputPoint.min = 0;
                inputPoint.value = assignment.maxPoints;
                pointCell.appendChild(inputPoint);
                row.appendChild(pointCell);

                //преобразование даты
                const dateString = assignment.dateFormat;
                const parts = dateString.split('.');
                const day = parts[0];
                const month = parts[1];
                const year = parts[2];
                const dateObject = new Date(`${year}-${month}-${day}`);
                const formattedDate = dateObject.toISOString().split('T')[0];

                const dateCell = document.createElement('td');
                dateCell.classList.add('editable-cell');
                dateCell.classList.add('editable-assignment');
                const inputDate = document.createElement('input');
                inputDate.type = 'date';
                inputDate.required = true;
                inputDate.value = formattedDate;
                dateCell.appendChild(inputDate);
                row.appendChild(dateCell);

                tbodyAssignment.appendChild(row);
            });
            tableAssignment.appendChild(tbodyAssignment);
            assignmentTable.appendChild(tableAssignment);

            //Кнопка сохранения
            const assignmentSaveBtn = document.querySelector('.assignment-save');
            assignmentSaveBtn.innerHTML = '';
            const buttonSaveAssignment = document.createElement('button');
            buttonSaveAssignment.setAttribute('id', 'saveChangesOfStudentsButton');
            buttonSaveAssignment.classList.add('btn');
            buttonSaveAssignment.classList.add('btn-primary');
            buttonSaveAssignment.classList.add('btn-save-changes')
            buttonSaveAssignment.textContent = 'Сохранить изменения';
            assignmentSaveBtn.appendChild(buttonSaveAssignment);

            dictionaryAssignment = new Map();
            editableAssignment();

            buttonSaveAssignment.addEventListener('click', function() {
                saveChangesOfAssignments(selectedDiscipline, selectedGroup, selectedType, text);
            });
        })
        .catch(error => console.error(error));
}


//Отображение групп и журнала после выбора дисциплины
function getGroupsList(selectedDiscipline, text) {
    fetch(`${teacherId}/groups?discipline=${selectedDiscipline}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            const groupsList = document.querySelector('.groups-list');

            // Очищаем список перед добавлением новых элементов
            groupsList.innerHTML = '';

            // Создаем выпадающий список типов и добавляем его в .groups-list
            const selectType = document.createElement('select');
            // Создаем опции для выпадающего списка
            const options = ['лекция', 'практика', 'л.р.'];
            // Добавляем опции в выпадающий список
            options.forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option;
                optionElement.textContent = option;
                selectType.appendChild(optionElement);
            });

            // Устанавливаем значение "лекция" по умолчанию
            selectType.value = 'лекция';
            groupsList.appendChild(selectType);

            // Создаем выпадающий список и добавляем его в .groups-list
            const select = document.createElement('select');

            // Добавляем опцию по умолчанию
            const defaultOption = document.createElement('option');
            defaultOption.text = 'Выберите группу';
            defaultOption.disabled = true;
            defaultOption.selected = true;
            select.appendChild(defaultOption);

            // Добавляем опции с данными из ответа сервера
            data.forEach(group => {
                const option = document.createElement('option');
                option.value = group.groupId;
                option.text = group.name;
                select.appendChild(option);
            });

            // Добавляем выпадающий список в .groups-list
            groupsList.appendChild(select);

            // Обработчик события изменения значения в выпадающем списке групп
            select.addEventListener('change', function() {
                const selectedType = selectType.value; // Получаем выбранное значение типа
                const selectedGroup = this.value; // Получаем выбранное значение группы

                // Отправляем GET-запрос
                getAttendances(selectedDiscipline, selectedGroup, selectedType, text);
                getGrades(selectedDiscipline, selectedGroup, selectedType, text);
                getAssignments(selectedDiscipline, selectedGroup, selectedType, text);
            });

            // Обработчик события изменения значения в выпадающем списке типов
            selectType.addEventListener('change', function() {
                const selectedType = selectType.value; // Получаем выбранное значение типа
                const selectedGroup = select.value; // Получаем выбранное значение группы

                // Отправляем GET-запрос
                getAttendances(selectedDiscipline, selectedGroup, selectedType, text);
                getGrades(selectedDiscipline, selectedGroup, selectedType, text);
                getAssignments(selectedDiscipline, selectedGroup, selectedType, text)
            });


        })
        .catch(error => console.error(error));
}

// получаем форму, которая находится в div с классом disciplines-list
const form = document.querySelector('.disciplines-list form');
const submitButtons = document.querySelectorAll('#choose-discipline-btn');

// при отправке формы вызываем функцию getJournalData с выбранной дисциплиной
submitButtons.forEach(button => {
    button.addEventListener('click', event => {
        event.preventDefault(); // отменяем стандартное поведение формы
        const selectedDiscipline = button.value; // получаем выбранную дисциплину из формы
        getGroupsList(selectedDiscipline, button.textContent); // вызываем функцию getJournalData с выбранной дисциплиной
        return false;
    });
});
