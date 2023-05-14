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
            var tbody = scheduleTable.getElementsByTagName("tbody")[0];
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

//Получаем журнал успеваемости по группе и дисциплине
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
                row.appendChild(nameCell);

                student.presentList.forEach(number => {
                    const numberCell = document.createElement('td');
                    numberCell.textContent = number;
                    row.appendChild(numberCell);
                });

                tbody.appendChild(row);
            });

            table.appendChild(tbody);

            // Добавляем таблицу
            journalTable.appendChild(table);

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
            });

            // Обработчик события изменения значения в выпадающем списке типов
            selectType.addEventListener('change', function() {
                const selectedType = selectType.value; // Получаем выбранное значение типа
                const selectedGroup = select.value; // Получаем выбранное значение группы

                // Отправляем GET-запрос
                getAttendances(selectedDiscipline, selectedGroup, selectedType, text);
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
