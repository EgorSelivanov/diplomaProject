//Подтверждение удаления специальности
function confirmDeletionSubject(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить дисциплину?');
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
                getSubjectList(globalSearch);
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
function fetchEditSubject(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var subjectId = event.currentTarget.getAttribute('href');

    var form = document.getElementById('subject-form' + subjectId);
    event.preventDefault();
    if (form.checkValidity() === false) {
        event.stopPropagation();
    }
    else {
        var url = form.getAttribute("action");
        var method = form.getAttribute("method");
        const csrfToken = document.getElementById("csrfToken").value;

        var inputName = document.getElementById('subject-name' + subjectId);
        var inputDescr = document.getElementById('subject-description' + subjectId);

        // Преобразование в объект JSON
        const jsonData = {};
        jsonData['subjectId'] = subjectId;
        jsonData['name'] = inputName.value;
        jsonData['description'] = inputDescr.value;

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
                inputDescr.readOnly = true;
                inputName.required = false;
                inputDescr.required = false;

                var buttonSubmit = document.getElementById('submit-subject' + subjectId);
                buttonSubmit.style.display = 'none';

                var buttonEdit = document.getElementById('button-edit-subject' + subjectId);
                buttonEdit.style.display = 'block';

                var buttonDelete = document.getElementById('button-delete-subject' + subjectId);
                buttonDelete.style.display = 'block';

                getSubjectList(globalSearch);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    }
    form.classList.add('was-validated');
}

//Редактирование данных специальности
function editSubject(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var subjectId = event.currentTarget.getAttribute('href');

    var inputName = document.getElementById('subject-name' + subjectId);
    var inputDescr = document.getElementById('subject-description' + subjectId);
    inputName.readOnly = false;
    inputDescr.readOnly = false;
    inputName.required = true;

    var buttonSubmit = document.getElementById('submit-subject' + subjectId);
    buttonSubmit.style.display = 'block';

    var buttonEdit = document.getElementById('button-edit-subject' + subjectId);
    buttonEdit.style.display = 'none';

    var buttonDelete = document.getElementById('button-delete-subject' + subjectId);
    buttonDelete.style.display = 'none';
}

//Получение списка специальностей
function getSubjectList(search) {
    fetch(`${adminId}/subjectList?search=` + search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (specialityListHtml) {
            var divList = document.getElementById('subject-list');
            divList.innerHTML = '';
            divList.innerHTML = specialityListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function getPageNewSubject(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-subject-form');
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

            fetchNewSubject();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewSubject() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newSubjectForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-subject-form');
        var url = form.getAttribute('action');

        var subjectName = document.getElementById('name-subject').value;
        var subjectDescr = document.getElementById('description-subject').value;

        const jsonData = {};
        jsonData['name'] = subjectName;
        jsonData['description'] = subjectDescr;
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
                    getSubjectList(globalSearch);
                else
                    fetchNewSubject();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}