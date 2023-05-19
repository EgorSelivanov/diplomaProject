const adminId = document.querySelector('#admin-id').getAttribute("value");

const sidebarItems = document.querySelectorAll('.sidebar li');
const mainContent = document.getElementById('mainContent');

// Получаем ссылки на элементы списка в боковой панели
var personalLink = document.getElementById('personal');
var studentsLink = document.getElementById('students');
var teachersLink = document.getElementById('teachers');
var groupsLink = document.getElementById('groups');
var specialtiesLink = document.getElementById('specialties');
var workloadLink = document.getElementById('workload');
var scheduleLink = document.getElementById('schedule');

// Обработчики событий для каждой категории
personalLink.addEventListener('click', function() {
    setMainText(personalLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "teacher/newschedule"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/personal`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            adminContent.innerHTML =newHtml;
            getAdminList("");

            var buttonSearchAdmins = document.getElementById('search-admin-button');

            buttonSearchAdmins.addEventListener('click', function () {
                var inputSearch = document.getElementById('search-admin-input');
                getAdminList(inputSearch.value);
            });

        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

function setMainText(item) {
    const categoryName = item.textContent;
    const image = item.querySelector('i');
    const icon = document.createElement('i');
    // Получаем список классов исходного объекта
    const classes = Array.from(image.classList);
    classes.forEach(className => icon.classList.add(className));
    icon.classList.remove('icon');

    // Remove 'active' class from all sidebar items
    sidebarItems.forEach(item => {
        item.classList.remove('active');
    });

    // Add 'active' class to the clicked sidebar item
    item.classList.add('active');

    mainContent.innerHTML = `<h2>${icon.outerHTML}${categoryName}</h2>`;
    const divContainer = document.createElement('div');
    divContainer.classList.add('container');
    mainContent.appendChild(divContainer);
}

function getAdminList(search) {
    fetch(`${adminId}/adminList?search=` + search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (adminListHtml) {
            var divList = document.getElementById('admin-list');
            divList.innerHTML = '';
            divList.innerHTML = adminListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}


document.addEventListener('DOMContentLoaded', function() {
    personalLink.click();

});
