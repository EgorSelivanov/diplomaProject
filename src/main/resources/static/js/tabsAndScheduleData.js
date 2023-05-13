var currentDate = new Date();

setInfoTextDateOfSchedule();

function setInfoTextDateOfSchedule() {
    const dateH2text = document.getElementById('info-schedule');
    dateH2text.innerHTML = '';
// Находим день недели текущей даты (0 - воскресенье, 1 - понедельник, и т.д.)
    var currentDayOfWeek = currentDate.getDay();
// Определяем дату начала недели
    var startDate = new Date(currentDate);
    startDate.setDate(currentDate.getDate() - currentDayOfWeek + 1); // Начало недели - понедельник
// Определяем дату окончания недели
    var endDate = new Date(currentDate);
    endDate.setDate(currentDate.getDate() - currentDayOfWeek + 7); // Окончание недели - воскресенье
// Преобразуем даты в нужный формат (дд.мм.гггг)
    var startDateFormatted = startDate.toLocaleDateString('ru-RU');
    var endDateFormatted = endDate.toLocaleDateString('ru-RU');
// создаем подпись таблицы
    const dateH2Content = document.createElement('h2');
    dateH2Content.textContent = "Таблица расписания для недели: " + startDateFormatted + " - " + endDateFormatted;
    dateH2text.appendChild(dateH2Content);
}

const prevWeekButton = document.getElementById('prev-week-btn');
const nextWeekButton = document.getElementById('next-week-btn');

prevWeekButton.addEventListener('click', event => {
    currentDate = new Date(currentDate.getTime() - 7 * 24 * 60 * 60 * 1000);
    getScheduleDataByDate(currentDate.toISOString().split('T')[0]);
    setInfoTextDateOfSchedule();
    return false;
});

nextWeekButton.addEventListener('click', event => {
    currentDate = new Date(currentDate.getTime() + 7 * 24 * 60 * 60 * 1000);
    getScheduleDataByDate(currentDate.toISOString().split('T')[0]);
    setInfoTextDateOfSchedule();
    return false;
});


//Переключение вкладок
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