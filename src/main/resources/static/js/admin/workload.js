//Подтверждение удаления группы
function confirmDeletionWorkload(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить нагрузку?');
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
                getTeacherWorkloadList(globalSearch);
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

function getPageNewWorkload(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-workload-form');
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
            selectCourse.addEventListener('change', function () {
                getGroupListByCourse(selectCourse.value, divSelect, selectGroup);
            });

            fetchNewWorkload();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewWorkload() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newWorkloadForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-workload-form');
        var url = form.getAttribute('action');

        var idOfTeacher = document.getElementById('workload-teacher-id').value;
        var type = document.getElementById('subject-type-select').value;
        var subjectId = document.getElementById('subject-select-modal').value;
        var groupId = document.getElementById('student-group-select-modal').value;

        const jsonData = {};
        jsonData['teacherId'] = idOfTeacher;
        jsonData['type'] = type;
        jsonData['subjectId'] = subjectId;
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

                if (modalHtml === '')
                    getTeacherWorkloadList(globalSearch);
                else
                    fetchNewWorkload();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}

function addJsonFunctionalityToWorkload() {
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
            "subjectName: название дисциплины; " +
            "type: вид занятия; " +
            "groupName: название группы.");
        buttonJSON.disabled = inputJSON.files.length <= 0;
    });
    buttonJSON.addEventListener('click', function() {
        uploadJSONToWorkload(inputJSON.files[0]);
    });
}

function uploadJSONToWorkload(file) {
    var idOfTeacher = document.getElementById('workload-teacher-id').value;
    const csrfToken = document.getElementById("csrfToken").value;
    var formData = new FormData();
    formData.append('file', file);
    fetch(`${adminId}/workload/${idOfTeacher}/upload-json`,
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

            getTeacherWorkloadList(globalSearch);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}