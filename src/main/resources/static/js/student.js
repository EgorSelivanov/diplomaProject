//отображение журнала
// функция, которая отправляет GET запрос на сервер и получает данные для таблицы журнала
function getJournalData(discipline, text) {
    const studentId = document.querySelector('#student-id').getAttribute("value");
    // отправляем GET запрос на сервер с параметром discipline
    fetch(`${studentId}/journal?discipline=${discipline}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            // получаем элемент div с классом journal-table
            const journalTable = document.querySelector('.journal-table');
            // очищаем его содержимое
            journalTable.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица успеваемости по дисциплине: " + text;
            journalTable.appendChild(h2Content);
            // если полученных данных нет, выводим сообщение об ошибке
            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                journalTable.appendChild(pContent);
                return;
            }
            // создаем таблицу и заголовок таблицы
            const table = document.createElement('table');
            const thead = document.createElement('thead');
            const tbody = document.createElement('tbody');
            const tr = document.createElement('tr');
            const headers = ['Вид работы', 'Описание', 'Дата', 'Количество баллов', 'Максимальное количество баллов'];
            headers.forEach(header => {
                const th = document.createElement('th');
                th.textContent = header;
                tr.appendChild(th);
            });
            thead.appendChild(tr);
            table.appendChild(thead);
            table.appendChild(tbody);

            // заполняем таблицу данными
            data.forEach(item => {
                const row = document.createElement('tr');
                const cells = [item.type, item.description, item.dateFormat, item.points, item.maxPoints];
                cells.forEach(cell => {
                    const td = document.createElement('td');
                    td.textContent = cell;
                    row.appendChild(td);
                });
                tbody.appendChild(row);
            });
            // добавляем таблицу в элемент div с классом journal-table и отображаем его
            journalTable.appendChild(table);
            journalTable.style.display = 'block';
        })
        .catch(error => console.error(error));
}

// функция, которая отправляет GET запрос на сервер и получает данные для таблицы посещаемости
function getAttendanceData(discipline, text) {
    const studentId = document.querySelector('#student-id').getAttribute("value");
    // отправляем GET запрос на сервер с параметром discipline
    fetch(`${studentId}/attendance?discipline=${discipline}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            // получаем элемент div с классом journal-table
            const attendanceTable = document.querySelector('.attendance-table');
            // очищаем его содержимое
            attendanceTable.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица посещаемости по дисциплине: " + text;
            attendanceTable.appendChild(h2Content);
            // если полученных данных нет, выводим сообщение об ошибке
            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                attendanceTable.appendChild(pContent);
                return;
            }
            // создаем таблицу и заголовок таблицы
            const table = document.createElement('table');
            const thead = document.createElement('thead');
            const tbody = document.createElement('tbody');
            const tr = document.createElement('tr');
            const headers = ['Дата', 'День недели', 'Вид занятия', 'Балл за посещение'];
            headers.forEach(header => {
                const th = document.createElement('th');
                th.textContent = header;
                tr.appendChild(th);
            });
            thead.appendChild(tr);
            table.appendChild(thead);
            table.appendChild(tbody);

            // заполняем таблицу данными
            data.forEach(item => {
                const row = document.createElement('tr');
                const cells = [item.dateFormat, item.dayOfWeek, item.type, item.present];
                cells.forEach(cell => {
                    const td = document.createElement('td');
                    td.textContent = cell;
                    row.appendChild(td);
                });
                tbody.appendChild(row);
            });
            // добавляем таблицу в элемент div с классом journal-table и отображаем его
            attendanceTable.appendChild(table);
            attendanceTable.style.display = 'block';
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
        getJournalData(selectedDiscipline, button.textContent); // вызываем функцию getJournalData с выбранной дисциплиной
        getAttendanceData(selectedDiscipline, button.textContent);
        return false;
    });
});


//отображение расписания
// функция, которая отправляет GET запрос на сервер и получает данные для таблицы расписания
function getScheduleDataByDate(date) {
    const studentId = document.querySelector('#student-id').getAttribute("value");
    // отправляем GET запрос на сервер с параметром discipline
    fetch(`${studentId}/schedule?date=${date}`, { method: 'GET' })
        .then(response => response.json()) // получаем ответ и преобразуем его в json
        .then(data => {
            // получаем элемент table с id schedule-table
            const scheduleTable = document.getElementById('schedule-table');

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
                    item.secondName + " " + item.firstName + " " + item.patronymic, item.department];
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