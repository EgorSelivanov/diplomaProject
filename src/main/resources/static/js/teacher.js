// Получаем все кнопки вкладок и все элементы содержимого вкладок
var tabButtons = document.querySelectorAll(".tab-button");
var tabContents = document.querySelectorAll(".tab-pane");


// При клике на кнопку вкладки
tabButtons.forEach(function(button) {
    button.addEventListener("click", function() {

        // Удаляем класс "active" у всех кнопок вкладок
        tabButtons.forEach(function(btn) {
            btn.classList.remove("active");
        });

        // Добавляем класс "active" только на выбранную кнопку
        button.classList.add("active");

        // Скрываем все элементы содержимого вкладок
        tabContents.forEach(function(content) {
            content.style.display = "none";
        });

        // Отображаем только содержимое выбранной вкладки
        var tabId = button.getAttribute("data-tab");
        var tabContent = document.querySelector("#" + tabId);
        tabContent.style.display = "block";
    });
});


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
