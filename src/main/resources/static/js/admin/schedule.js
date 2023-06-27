var editableSchedule = [];
var isCallSchedule = false;

function returnToWorkload() {
    scheduleLink.click();
}

//Подтверждение удаления занятия
function confirmDeletionSchedule(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить занятие?');
    var modal = document.getElementById('modal-div');
    const csrfToken = document.getElementById("csrfToken").value;
    // Обработчики клика по кнопкам в окне подтверждения
    document.querySelector(".confirmModalYes").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
        console.log(url);
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
                getScheduleList(globalSearch);
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

function addEventListenerToButtonEditSchedule() {
    isCallSchedule = true;
    var btnReadEdit = document.getElementById('btn-check-read-edit');
    btnReadEdit.addEventListener('click', function () {
        var dataForServer = [];
        editableSchedule.forEach(idName => {
            var row = document.getElementById(idName);
            var audience = row.querySelector('#schedule-audience').value;
            var building = row.querySelector('#schedule-building').value;
            var dayOfWeek = row.querySelector('#schedule-day-of-week').value;
            var startTime = row.querySelector('#schedule-start-time').value;
            var endTime = row.querySelector('#schedule-end-time').value;
            var repeat = row.querySelector('#schedule-repeat').value;

            // Преобразование в объект JSON
            const jsonData = {};

            jsonData['scheduleId'] = idName.replace('row-schedule', '');
            jsonData['audience'] = audience;
            jsonData['building'] = building;
            jsonData['dayOfWeek'] = dayOfWeek;
            jsonData['startTime'] = startTime;
            jsonData['endTime'] = endTime;
            jsonData['repeat'] = repeat;
            dataForServer.push(jsonData);
        });
        fetchEditSchedule(dataForServer);
        editableSchedule = [];
    });
}

//Просмотр - Редактирование
function changeTypeOfUsingSchedule() {
    var checkBox = document.getElementById('check-read-edit');
    checkBox.addEventListener('change', function (event) {
        var btnReadEdit = document.getElementById('btn-check-read-edit');
        if (event.target.checked) {
            btnReadEdit.style.display = 'block';
            btnReadEdit.style.marginTop = '15px';
            btnReadEdit.style.marginLeft = '-40px';
            editableStudents = [];
            if (!isCallSchedule)
                addEventListenerToButtonEditSchedule();
        }
        else {
            btnReadEdit.style.display = 'none';
        }
        setEditCells();
    });

    if (checkBox.checked) {
        setEditCells();
    }

    function setEditCells() {
        var editCells = document.querySelectorAll('.editable-cell');
        editCells.forEach(cell => {
            const input = cell.querySelector('input');
            if (input === null)
                return;
            input.readOnly = !checkBox.checked;
            input.addEventListener('input', function () {
                var row = cell.parentNode;
                editableSchedule.push(row.id);
            });
        });

        var editSelect = document.querySelectorAll('.editable-cell-select');
        editSelect.forEach(cell => {
            const select = cell.querySelector('select');
            select.disabled = !checkBox.checked;
            select.addEventListener('change', function () {
                var row = cell.parentNode;
                editableSchedule.push(row.id);
            })
        });
    }
}

//Отправка запроса
function fetchEditSchedule(dataToSend) {
    const csrfToken = document.getElementById("csrfToken").value;
    fetch(`${adminId}/edit-schedule`, {
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
            getScheduleList(globalSearch);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Получение списка специальностей
function getScheduleWorkloadList(search) {
    fetch(`${adminId}/scheduleList?search=` + search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (scheduleListHtml) {
            var divList = document.getElementById('schedule-list');
            divList.innerHTML = '';
            divList.innerHTML = scheduleListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function getPageNewSchedule(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-schedule-form');
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

            fetchNewSchedule();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewSchedule() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newScheduleForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-schedule-form');
        var url = form.getAttribute('action');

        var dayOfWeek = document.getElementById('weekday').value;
        var startTime = document.getElementById('startTime').value;
        var endTime = document.getElementById('endTime').value;
        var repeat = document.getElementById('lessonRepeat').value;
        var audience = document.getElementById('classroom').value;
        var building = document.getElementById('building').value;

        const jsonData = {};
        jsonData['workloadId'] = document.getElementById('workload-id').value;
        jsonData['dayOfWeek'] = dayOfWeek;
        jsonData['startTime'] = startTime;
        jsonData['endTime'] = endTime;
        jsonData['repeat'] = repeat;
        jsonData['audience'] = audience;
        jsonData['building'] = building;
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
                    getScheduleList(globalSearch);
                else
                    fetchNewSchedule();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}

function addJsonFunctionalityToSchedule() {
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
            "audience: аудитория; " +
            "dayOfWeek: день недели; " +
            "startTime: время начала (в формате HH:mm); " +
            "endTime: время окончания (в формате HH:mm);" +
            "building: здание; " +
            "repeat: повторяемость (необязательный); " +
            "username: имя пользователя преподавателя; " +
            "subjectName: название дисциплины; " +
            "type: вид занятия; " +
            "groupName: название группы.");
        buttonJSON.disabled = inputJSON.files.length <= 0;
    });
    buttonJSON.addEventListener('click', function() {
        uploadJSONToSchedule(inputJSON.files[0]);
    });
}

function uploadJSONToSchedule(file) {
    const csrfToken = document.getElementById("csrfToken").value;
    var formData = new FormData();
    formData.append('file', file);
    fetch(`${adminId}/schedule/upload-json`,
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
            getScheduleWorkloadList(globalSearch);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}


function getSchedulePage(event) {
    event.preventDefault();
    //globalSearch = '';

    const adminContent = document.querySelector('.container');

    var url = event.currentTarget.getAttribute('href');

    fetch(url, { method: 'GET' })
        .then(function (response) {
            return response.text();
        })
        .then(function (newHtml) {
            adminContent.innerHTML = newHtml;

            addJsonFunctionalityToSchedule();

            getScheduleList(globalSearch);
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function getScheduleList(search) {
    const idOfWorkload = document.getElementById('workload-id').value;
    fetch(`${adminId}/scheduleList/${idOfWorkload}?search=${search}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (scheduleListHtml) {
            var divList = document.getElementById('schedule-list');
            divList.innerHTML = '';
            divList.innerHTML = scheduleListHtml;
            changeTypeOfUsingSchedule();
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}