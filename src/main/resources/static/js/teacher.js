//Добавление нового расписания

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

            // Добавьте код для дальнейшей обработки модальной страницы здесь
            // Например, добавление обработчика отправки формы и обработка данных
            var scheduleForm = modal.querySelector('#scheduleForm');
            //scheduleForm.addEventListener('submit', function (event) {
              //  event.preventDefault();
                // Обработка данных формы
                // ...
            //});

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
    const teacherId = document.querySelector('#teacher-id').getAttribute("value");
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
    const teacherId = document.querySelector('#teacher-id').getAttribute("value");
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
    const teacherId = document.querySelector('#teacher-id').getAttribute("value");
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
