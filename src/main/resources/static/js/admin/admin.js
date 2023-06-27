const adminId = document.querySelector('#admin-id').getAttribute("value");
var globalSearch = '';

const sidebarItems = document.querySelectorAll('.sidebar li');
const mainContent = document.getElementById('mainContent');

// Получаем ссылки на элементы списка в боковой панели
var personalLink = document.getElementById('personal');
var studentsLink = document.getElementById('students');
var teachersLink = document.getElementById('teachers');
var groupsLink = document.getElementById('groups');
var specialtiesLink = document.getElementById('specialties');
var subjectsLink = document.getElementById('subject');
var scheduleLink = document.getElementById('schedule');

// Обработчик события для категории Личный кабинет
personalLink.addEventListener('click', function() {
    globalSearch = '';
    setMainText(personalLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/personal`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            showContent(adminContent, newHtml, getAdminList);
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Преподаватели
teachersLink.addEventListener('click', function() {
    globalSearch = '';
    setMainText(teachersLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/teachers`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            adminContent.innerHTML =newHtml;

            var inputSearch = document.getElementById('search-input');
            inputSearch.value = globalSearch;
            getTeachersAdminList(globalSearch);

            var selectDepartment = document.getElementById('teacher-department-select');
            var checkboxDepartment = document.getElementById('checkbox-department');
            checkboxDepartment.addEventListener('change', function (event) {
                selectDepartment.disabled = !event.target.checked;
                if (!event.target.checked)
                    getTeachersAdminList(globalSearch);
                else
                    getTeachersAdminListByDepartment(globalSearch, selectDepartment.value);
            });

            selectDepartment.addEventListener('change', function (ev) {
                getTeachersAdminListByDepartment(globalSearch, selectDepartment.value);
            });

            var buttonSearch = document.getElementById('search-button');

            buttonSearch.addEventListener('click', function () {
                globalSearch = inputSearch.value;
                if (checkboxDepartment.checked) {
                    getTeachersAdminListByDepartment(globalSearch, selectDepartment.value);
                }
                else {
                    getTeachersAdminList(globalSearch);
                }
            });

            var buttonCloseSearch = document.getElementById('close-search-button');

            buttonCloseSearch.addEventListener('click', function () {
                inputSearch.value = '';
                globalSearch = '';
                if (checkboxDepartment.checked) {
                    getTeachersAdminListByDepartment(globalSearch, selectDepartment.value);
                }
                else {
                    getTeachersAdminList(globalSearch);
                }
            });

            addJsonFunctionalityToTeacher();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Студенты
studentsLink.addEventListener('click', function() {
    globalSearch = '';
    setMainText(studentsLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/students`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            adminContent.innerHTML =newHtml;

            var inputSearch = document.getElementById('search-input');
            inputSearch.value = globalSearch;

            var divSelect = document.getElementById('div-student-group-select');
            var selectGroup = document.getElementById('student-group-select');

            var selectCourse = document.getElementById('student-course-select');
            selectCourse.addEventListener('change', function (event) {
               getGroupListByCourse(selectCourse.value, divSelect, selectGroup);
            });
            getGroupListByCourse(selectCourse.value, divSelect, selectGroup);
            addJsonFunctionalityToStudent();

            var buttonSearch = document.getElementById('search-button');

            var checkboxStudentGroup = document.getElementById('checkbox-student-group');

            selectGroup.addEventListener('change', function () {
                getStudentsAdminListByGroup(globalSearch, selectGroup.value);
            });

            buttonSearch.addEventListener('click', function () {
                globalSearch = inputSearch.value;
                if (globalSearch.trim() === "") {
                    customAlert("Вы не ввели данные для поиска студента!");
                    return;
                }
                if (checkboxStudentGroup.checked)
                    getStudentsAdminListByGroup(globalSearch, selectGroup.value);
                else
                    getStudentsAdminList(globalSearch);
            });

            var buttonCloseSearch = document.getElementById('close-search-button');

            buttonCloseSearch.addEventListener('click', function () {
                inputSearch.value = '';
                globalSearch = '';
                getStudentsAdminListByGroup(globalSearch, selectGroup.value);
            });
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Специальности
groupsLink.addEventListener('click', function() {
    globalSearch = '';
    setMainText(groupsLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/groups`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            adminContent.innerHTML =newHtml;

            var inputSearch = document.getElementById('search-input');
            inputSearch.value = globalSearch;
            getGroupsAdminList(globalSearch);
            addJsonFunctionalityToGroup();

            var inputCourse = document.getElementById('inp-number-course');
            var checkboxCourse = document.getElementById('checkbox-course');
            checkboxCourse.addEventListener('change', function (event) {
                inputCourse.readOnly = !event.target.checked;
                if (!event.target.checked)
                    getGroupsAdminList(globalSearch);
            });

            var buttonSearch = document.getElementById('search-button');

            buttonSearch.addEventListener('click', function () {
                globalSearch = inputSearch.value;
                if (checkboxCourse.checked) {
                    var courseNumber = parseInt(inputCourse.value);
                    if (Number.isInteger(courseNumber))
                        getGroupsAdminListByCourse(globalSearch, courseNumber);
                    else
                        getGroupsAdminList(globalSearch);
                }
                else {
                    getGroupsAdminList(globalSearch);
                }
            });

            var buttonCloseSearch = document.getElementById('close-search-button');

            buttonCloseSearch.addEventListener('click', function () {
                inputSearch.value = '';
                globalSearch = '';
                if (checkboxCourse.checked) {
                    getGroupsAdminListByCourse(globalSearch, inputCourse.value);
                }
                else {
                    getGroupsAdminList(globalSearch);
                }
            });
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Специальности
subjectsLink.addEventListener('click', function() {
    setMainText(subjectsLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/subjects`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            showContent(adminContent, newHtml, getSubjectList);
            addJsonFunctionalityToSubject();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Специальности
specialtiesLink.addEventListener('click', function() {
    setMainText(specialtiesLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/specialities`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            showContent(adminContent, newHtml, getSpecialityList);
            addJsonFunctionalityToSpeciality();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
});

// Обработчик события для категории Расписание
scheduleLink.addEventListener('click', function() {
    setMainText(scheduleLink);
    const adminContent = document.querySelector('.container');
    // Отправить GET-запрос к серверу по адресу "admin/{id}/personal"
    // и обработать полученную модальную страницу
    fetch(`${adminId}/schedules`)
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            showContent(adminContent, newHtml, getScheduleWorkloadList);
            addJsonFunctionalityToSchedule();
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

document.addEventListener('DOMContentLoaded', function() {
    personalLink.click();
});


function showContent(adminContent, newHtml, functionToGetList) {
    adminContent.innerHTML =newHtml;

    var inputSearch = document.getElementById('search-input');
    inputSearch.value = globalSearch;
    functionToGetList(globalSearch);

    var buttonSearch = document.getElementById('search-button');

    buttonSearch.addEventListener('click', function () {
        globalSearch = inputSearch.value;
        functionToGetList(globalSearch);
    });

    var buttonCloseSearch = document.getElementById('close-search-button');

    buttonCloseSearch.addEventListener('click', function () {
        inputSearch.value = '';
        globalSearch = '';
        functionToGetList(globalSearch);
    });
}