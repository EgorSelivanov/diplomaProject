var editableStudents = [];
var isCallStudent= false;

function showStudents() {
    var checkboxStudentGroup = document.getElementById('checkbox-student-group');
    var selectGroup = document.getElementById('student-group-select');
    if (checkboxStudentGroup.checked || globalSearch.trim() === "")
        if (selectGroup.value !== null && selectGroup.value !== undefined)
            getStudentsAdminListByGroup(globalSearch, selectGroup.value);
    else
        getStudentsAdminList(globalSearch);
}

//Получение групп по курсу
function getGroupListByCourse(courseNumber, divSelect, selectGroup) {
    fetch(`${adminId}/groupListForTeacher?courseNumber=${courseNumber}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.json();
        })
        .then(data =>{
            if (divSelect != null) {
                selectGroup.innerHTML = '';
                // Добавляем опцию по умолчанию
                const defaultOption = document.createElement('option');
                defaultOption.text = 'Выберите группу';
                defaultOption.value = '';
                defaultOption.disabled = true;
                defaultOption.selected = true;
                selectGroup.appendChild(defaultOption);
                divSelect.style.display = 'flex';
            }

            var valueOption = selectGroup.querySelector('option').value;

            data.forEach(group => {
                var optionGroup = document.createElement('option');
                optionGroup.value = group.groupId;
                optionGroup.text = group.name;

                if (optionGroup.value === valueOption)
                    return;

                selectGroup.appendChild(optionGroup);
            })

        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Получение списка специальностей
function getStudentsAdminList(search) {
    fetch(`${adminId}/studentList?search=${search}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (studentListHtml) {
            var divList = document.getElementById('student-list');
            divList.innerHTML = '';
            divList.innerHTML = studentListHtml;
            changeTypeOfUsingStudent();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function addEventListenerToButtonEditStudent() {
    isCallStudent = true;
    var btnReadEdit = document.getElementById('btn-check-read-edit');
    btnReadEdit.addEventListener('click', function () {
        var dataForServer = [];
        editableStudents.forEach(idName => {
            var row = document.getElementById(idName);
            var secondName = row.querySelector('#student-secondName').value;
            var firstName = row.querySelector('#student-firstName').value;
            var patronymic = row.querySelector('#student-patronymic').value;
            var username = row.querySelector('#student-username').value;
            var email = row.querySelector('#student-email').value;
            var groupId = row.querySelector('#student-group-select-edit').value;

            // Преобразование в объект JSON
            const jsonData = {};

            jsonData['studentId'] = idName.replace('row-student', '');
            jsonData['secondName'] = secondName;
            jsonData['firstName'] = firstName;
            jsonData['patronymic'] = patronymic;
            jsonData['username'] = username;
            jsonData['email'] = email;
            jsonData['groupId'] = groupId;
            dataForServer.push(jsonData);
        });
        fetchEditStudent(dataForServer);
        editableStudents = [];
    });
}

//Просмотр - Редактирование
function changeTypeOfUsingStudent() {
    var checkBox = document.getElementById('check-read-edit');
    checkBox.addEventListener('change', function (event) {
        var btnReadEdit = document.getElementById('btn-check-read-edit');
        if (event.target.checked) {
            btnReadEdit.style.display = 'block';
            btnReadEdit.style.marginTop = '15px';
            btnReadEdit.style.marginLeft = '-40px';
            editableStudents = [];
            if (!isCallStudent)
                addEventListenerToButtonEditStudent();
        }
        else {
            btnReadEdit.style.display = 'none';
        }
        setEditCells();
    });

    var selectCourse = document.querySelectorAll('#student-course-select-edit');
    var selectGroup = document.querySelectorAll('#student-group-select-edit');

    if (checkBox.checked) {
        setEditCells();
    }

    for (var i = 0; i < selectGroup.length; i++) {
        getGroupListByCourse(selectCourse[i].value, null, selectGroup[i])

        selectCourse[i].addEventListener('change', function (event) {
            var list = event.currentTarget.parentNode.parentNode.querySelector('.editable-cell-select');
            getGroupListByCourse(event.currentTarget.value, list, list.querySelector('#student-group-select-edit'))
        });
    }

    function setEditCells() {
        var editCells = document.querySelectorAll('.editable-cell');
        editCells.forEach(cell => {
            const input = cell.querySelector('input');
            input.readOnly = !checkBox.checked;
            input.addEventListener('input', function () {
                var row = cell.parentNode;
                editableStudents.push(row.id);
            });
        });

        var editSelect = document.querySelectorAll('.editable-cell-select');
        editSelect.forEach(cell => {
            const select = cell.querySelector('select');
            select.disabled = !checkBox.checked;
            select.addEventListener('change', function () {
                var row = cell.parentNode;
                editableStudents.push(row.id);
            })
        });
    }
}

//Получение списка специальностей
function getStudentsAdminListByGroup(search, groupId) {
    fetch(`${adminId}/studentList?search=${search}&group=${groupId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (studentListHtml) {
            var divList = document.getElementById('student-list');
            divList.innerHTML = '';
            divList.innerHTML = studentListHtml;
            changeTypeOfUsingStudent();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Подтверждение удаления группы
function confirmDeletionStudent(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить студента?');
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
                showStudents();
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
function fetchEditStudent(dataToSend) {
    const csrfToken = document.getElementById("csrfToken").value;
    fetch(`${adminId}/edit-students`, {
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
            showStudents();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function getPageNewStudent(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-student-form');
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

            var selectCourse = document.getElementById('student-course-select-modal');
            var divSelect = document.getElementById('div-student-group-select-modal');
            var selectGroup = document.getElementById('student-group-select-modal');
            getGroupListByCourse(selectCourse.value, divSelect, selectGroup)

            fetchNewStudent();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewStudent() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newStudentForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-student-form');
        var url = form.getAttribute('action');

        var username = document.getElementById('username').value;
        var password = document.getElementById('password').value;
        var email = document.getElementById('email').value;
        var secondName = document.getElementById('secondNameStudent').value;
        var firstName = document.getElementById('firstNameStudent').value;
        var patronymic = document.getElementById('patronymicStudent').value;
        var groupId = document.getElementById('student-group-select-modal').value;

        const jsonData = {};
        jsonData['username'] = username;
        jsonData['password'] = password;
        jsonData['email'] = email;
        jsonData['secondName'] = secondName;
        jsonData['firstName'] = firstName;
        jsonData['patronymic'] = patronymic;
        jsonData['groupId'] = groupId;

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

                if (modalHtml === '') {
                    customAlert("Студент успешно создан!");
                    showStudents();
                }
                else
                    fetchNewStudent();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}

function addJsonFunctionalityToStudent() {
    var inputJSON = document.getElementById('uploadJSONInput');
    var buttonJSON = document.getElementById('uploadJSONButton');
    inputJSON.addEventListener('change', function() {
        if (inputJSON.files.length > 1) {
            inputJSON.value = '';
            customAlert("Выберите только 1 файл!");
            return;
        }
        if (inputJSON.files.length === 1 && !inputJSON.files[0].name.endsWith('.json')) {
            inputJSON.value = '';
            customAlert("Выберите файл JSON!");
            return;
        }
        customAlert("Обратите внимание, что JSON должен содержать следующие поля:\n" +
            "username: имя пользователя; " +
            "password: пароль; " +
            "email: почта; " +
            "firstName: имя преподавателя; " +
            "secondName: фамилия преподавателя; " +
            "patronymic: отчество преподавателя; " +
            "groupName: название группы.");
        buttonJSON.disabled = inputJSON.files.length <= 0;
    });
    buttonJSON.addEventListener('click', function() {
        uploadJSONToStudent(inputJSON.files[0]);
    });
}

function uploadJSONToStudent(file) {
    const csrfToken = document.getElementById("csrfToken").value;
    var formData = new FormData();
    formData.append('file', file);
    fetch(`${adminId}/student/upload-json`,
        { method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken
            },
            body: formData })
        .then(response => response.text())
        .then(data => {
            // Обработка ответа от сервера
            customAlert(data);
            var inputJSON = document.getElementById('uploadJSONInput');
            var buttonJSON = document.getElementById('uploadJSONButton');

            inputJSON.value = '';
            buttonJSON.disabled = true;

            var checkboxStudentGroup = document.getElementById('checkbox-student-group');
            var selectGroup = document.getElementById('student-group-select');

            if (globalSearch.trim() === "")
                return;
            if (checkboxStudentGroup.checked)
                getStudentsAdminListByGroup(globalSearch, selectGroup.value);
            else
                getStudentsAdminList(globalSearch);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}