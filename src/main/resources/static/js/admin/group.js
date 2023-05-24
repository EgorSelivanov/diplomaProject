//Получение списка специальностей
function getGroupsAdminList(search) {
    fetch(`${adminId}/groupList?search=${search}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (groupListHtml) {
            var divList = document.getElementById('group-list');
            divList.innerHTML = '';
            divList.innerHTML = groupListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Получение списка специальностей
function getGroupsAdminListByCourse(search, courseNumber) {
    fetch(`${adminId}/groupList?search=${search}&course=${courseNumber}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (groupListHtml) {
            var divList = document.getElementById('group-list');
            divList.innerHTML = '';
            divList.innerHTML = groupListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Подтверждение удаления группы
function confirmDeletionGroup(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить группу?');
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
                var inputCourse = document.getElementById('inp-number-course');
                var checkboxCourse = document.getElementById('checkbox-course');
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
function fetchEditGroup(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var groupId = event.currentTarget.getAttribute('href');

    var form = document.getElementById('group-form' + groupId);
    event.preventDefault();
    if (form.checkValidity() === false) {
        event.stopPropagation();
    }
    else {
        var url = form.getAttribute("action");
        var method = form.getAttribute("method");
        const csrfToken = document.getElementById("csrfToken").value;

        var inputName = document.getElementById('group-name' + groupId);
        var inputCourse = document.getElementById('group-course' + groupId);

        var selectCode = document.getElementById('group-speciality-code' + groupId);
        var selectName = document.getElementById('group-speciality-name' + groupId);

        // Преобразование в объект JSON
        const jsonData = {};

        jsonData['name'] = inputName.value;
        jsonData['courseNumber'] = inputCourse.value;
        jsonData['specialityId'] = selectCode.value;

        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(jsonData),
        })
            .then(response => {
                return response.text();
            })
            .then(data => {
                customAlert(data);

                inputName.readOnly = true;
                inputCourse.readOnly = true;
                inputName.required = false;
                inputCourse.required = false;
                selectCode.disabled = true;
                selectName.disabled = true;

                var buttonSubmit = document.getElementById('submit-group' + groupId);
                buttonSubmit.style.display = 'none';

                var buttonEdit = document.getElementById('button-edit-group' + groupId);
                buttonEdit.style.display = 'block';

                var buttonDelete = document.getElementById('button-delete-group' + groupId);
                buttonDelete.style.display = 'block';

                getGroupsAdminList(globalSearch);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    }
    form.classList.add('was-validated');
}

//Редактирование данных специальности
function editGroup(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var groupId = event.currentTarget.getAttribute('href');

    var inputName = document.getElementById('group-name' + groupId);
    var inputCourse = document.getElementById('group-course' + groupId);
    inputName.readOnly = false;
    inputCourse.readOnly = false;
    inputName.required = true;
    inputCourse.required = true;

    var selectCode = document.getElementById('group-speciality-code' + groupId);
    selectCode.disabled = false;

    var selectName = document.getElementById('group-speciality-name' + groupId);
    selectName.disabled = false;

    selectCode.addEventListener('change', function (event) {
        selectName.value = selectCode.value;
    });
    selectName.addEventListener('change', function (event){
        selectCode.value = selectName.value;
    });

    var buttonSubmit = document.getElementById('submit-group' + groupId);
    buttonSubmit.style.display = 'block';

    var buttonEdit = document.getElementById('button-edit-group' + groupId);
    buttonEdit.style.display = 'none';

    var buttonDelete = document.getElementById('button-delete-group' + groupId);
    buttonDelete.style.display = 'none';
}

function getPageNewGroup(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-group-form');
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

            var selectCode = document.getElementById('select-course');
            var selectName = document.getElementById('select-name');
            selectCode.addEventListener('change', function (event) {
                selectName.value = selectCode.value;
            });
            selectName.addEventListener('change', function (event){
                selectCode.value = selectName.value;
            });

            fetchNewGroup();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewGroup() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newGroupForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-group-form');
        var url = form.getAttribute('action');

        var groupName = document.getElementById('name-group').value;
        var groupCourse = document.getElementById('course-group').value;
        var selectCode = document.getElementById('select-course');
        var selectName = document.getElementById('select-name');

        const jsonData = {};
        jsonData['specialityId'] = selectCode.value;
        jsonData['name'] = groupName;
        jsonData['courseNumber'] = groupCourse;
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
                    getGroupsAdminList(globalSearch);
                else
                    fetchNewGroup();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}