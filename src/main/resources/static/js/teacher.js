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

            var i = 0;
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
                console.log("odd: " + i);
                if (i % 2 === 0)
                    row.classList.add('even-row');
                else
                    row.classList.add('odd-row')
                i += 1;
                tbody.appendChild(row);
            });
        })
        .catch(error => console.error(error));
}

//Отображение групп и журнала после выбора дисциплины
function getGroupsList(selectedDiscipline, selectedType) {
    fetch(`${teacherId}/groups?discipline=${selectedDiscipline}&type=${selectedType}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            const groupsList = document.querySelector('.groups-list');

            groupsList.innerHTML = '';

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
                const selectedGroup = this.value; // Получаем выбранное значение группы

                // Отправляем GET-запрос
                getAttendances(selectedDiscipline, selectedGroup, selectedType);
                getGrades(selectedDiscipline, selectedGroup, selectedType);
                getAssignments(selectedDiscipline, selectedGroup, selectedType);
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

        const typeList = document.querySelector('.type-list');
        typeList.innerHTML = '';
        const nameOfSubject = document.createElement('h2');
        nameOfSubject.id = 'nameOfSubject';
        nameOfSubject.textContent = button.textContent + ": ";
        typeList.appendChild(nameOfSubject);
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
        typeList.appendChild(selectType);

        getGroupsList(selectedDiscipline, selectType.value);

        // Обработчик события изменения значения в выпадающем списке типов
        selectType.addEventListener('change', function() {
            const selectedType = selectType.value; // Получаем выбранное значение типа

            // Отправляем GET-запрос
            getGroupsList(selectedDiscipline, selectedType);
        });
        return false;
    });
});
