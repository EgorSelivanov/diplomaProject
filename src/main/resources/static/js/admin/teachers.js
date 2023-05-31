var editableTeachers = [];
var isCallTeacher = false;
//Получение списка специальностей
function getTeachersAdminList(search) {
    fetch(`${adminId}/teacherList?search=${search}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (teacherListHtml) {
            var divList = document.getElementById('teacher-list');
            divList.innerHTML = '';
            divList.innerHTML = teacherListHtml;
            changeTypeOfUsing();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function addEventListenerToButtonEdit() {
    isCallTeacher = true;
    var btnReadEdit = document.getElementById('btn-check-read-edit');
    btnReadEdit.addEventListener('click', function () {
        var dataForServer = [];
        editableTeachers.forEach(idName => {
            var row = document.getElementById(idName);
            var secondName = row.querySelector('#teacher-secondName').value;
            var firstName = row.querySelector('#teacher-firstName').value;
            var patronymic = row.querySelector('#teacher-patronymic').value;
            var username = row.querySelector('#teacher-username').value;
            var email = row.querySelector('#teacher-email').value;
            var department = row.querySelector('#teacher-department').value;
            var position = row.querySelector('#teacher-position').value;

            // Преобразование в объект JSON
            const jsonData = {};

            jsonData['teacherId'] = idName.replace('row-teacher', '');
            jsonData['secondName'] = secondName;
            jsonData['firstName'] = firstName;
            jsonData['patronymic'] = patronymic;
            jsonData['username'] = username;
            jsonData['email'] = email;
            jsonData['department'] = department;
            jsonData['position'] = position;
            dataForServer.push(jsonData);
        });
        fetchEditTeacher(dataForServer);
        editableTeachers = [];
    });
}

//Просмотр - Редактирование
function changeTypeOfUsing() {
    var checkBox = document.getElementById('check-read-edit');
    checkBox.addEventListener('change', function (event) {
        var btnReadEdit = document.getElementById('btn-check-read-edit');
        if (event.target.checked) {
            btnReadEdit.style.display = 'block';
            btnReadEdit.style.marginTop = '15px';
            btnReadEdit.style.marginLeft = '-40px';
            editableTeachers = [];
            if (!isCallTeacher)
                addEventListenerToButtonEdit();
        }
        else {
            btnReadEdit.style.display = 'none';
        }
        var editCells = document.querySelectorAll('.editable-cell');
        editCells.forEach(cell => {
            const input = cell.querySelector('input');
            input.readOnly = !event.target.checked;
            input.addEventListener('input', function () {
               var row = cell.parentNode;
               editableTeachers.push(row.id);
            });
        });
    });

    if (checkBox.checked) {
        var editCells = document.querySelectorAll('.editable-cell');
        editCells.forEach(cell => {
            const input = cell.querySelector('input');
            input.readOnly = !checkBox.checked;
            input.addEventListener('input', function () {
                var row = cell.parentNode;
                editableTeachers.push(row.id);
            });
        });
    }
}

//Получение списка специальностей
function getTeachersAdminListByDepartment(search, department) {
    fetch(`${adminId}/teacherList?search=${search}&department=${department}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (teacherListHtml) {
            var divList = document.getElementById('teacher-list');
            divList.innerHTML = '';
            divList.innerHTML = teacherListHtml;
            changeTypeOfUsing();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Подтверждение удаления группы
function confirmDeletionTeacher(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить преподавателя?');
    var modal = document.getElementById('modal-div');
    const csrfToken = document.getElementById("csrfToken").value;
    // Обработчики клика по кнопкам в окне подтверждения
    document.querySelector(".confirmModalYes").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
        fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => {
                return response.text();
            })
            .then(data => {
                customAlert(data);
                var inputDepartment = document.getElementById('teacher-department');
                var checkboxRead = document.getElementById('checkbox-department');
                if (checkboxRead.checked) {
                    getTeachersAdminListByDepartment(globalSearch, inputDepartment.value);
                }
                else {
                    getTeachersAdminList(globalSearch);
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    });

    document.querySelector(".confirmModalNo").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
    });
}


//Отправка запроса
function fetchEditTeacher(dataToSend) {
    const csrfToken = document.getElementById("csrfToken").value;
    fetch(`${adminId}/edit-teachers`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(dataToSend),
    })
        .then(response => {
            return response.text();
        })
        .then(data => {
            customAlert(data);
            var inputDepartment = document.getElementById('teacher-department');
            var checkboxDepartment = document.getElementById('checkbox-department');
            if (checkboxDepartment.checked) {
                getTeachersAdminListByDepartment(globalSearch, inputDepartment.value);
            }
            else {
                getTeachersAdminList(globalSearch);
            }
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function getPageNewTeacher(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-teacher-form');
    var url = form.getAttribute('action');
    var method = form.getAttribute('method');

    fetch(url, {
        method: method
    })
        .then(function (response) {
            return response.text();
        })
        .then(function (modalHtml) {
            // Создать модальное окно и поместить в него полученную страницу
            var modal = document.getElementById('modal-div');

            showModal(modal, modalHtml);

            fetchNewTeacher();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewTeacher() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newTeacherForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-teacher-form');
        var url = form.getAttribute('action');

        var username = document.getElementById('username').value;
        var password = document.getElementById('password').value;
        var email = document.getElementById('email').value;
        var secondName = document.getElementById('secondNameTeacher').value;
        var firstName = document.getElementById('firstNameTeacher').value;
        var patronymic = document.getElementById('patronymicTeacher').value;
        var department = document.getElementById('department').value;
        var position = document.getElementById('position').value;

        const jsonData = {};
        jsonData['username'] = username;
        jsonData['password'] = password;
        jsonData['email'] = email;
        jsonData['secondName'] = secondName;
        jsonData['firstName'] = firstName;
        jsonData['patronymic'] = patronymic;
        jsonData['department'] = department;
        jsonData['position'] = position;

        const csrfToken = document.getElementById("csrfToken").value;

        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(jsonData),
        })
            .then(function (response) {
                return response.text();
            })
            .then(function (modalHtml) {
                showModal(modal, modalHtml);

                if (modalHtml === '')
                    getTeachersAdminList(globalSearch);
                else
                    fetchNewTeacher();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}