//Подтверждение удаления специальности
function confirmDeletionSpeciality(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить специальность?');
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
                getSpecialityList(globalSearch);
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
function fetchEdit(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var specialityId = event.currentTarget.getAttribute('href');

    var form = document.getElementById('speciality-form' + specialityId);
    event.preventDefault();
    if (form.checkValidity() === false) {
        event.stopPropagation();
    }
    else {
        var url = form.getAttribute("action");
        var method = form.getAttribute("method");
        const csrfToken = document.getElementById("csrfToken").value;

        var inputName = document.getElementById('speciality-name' + specialityId);
        var inputCode = document.getElementById('speciality-code' + specialityId);

        // Преобразование в объект JSON
        const jsonData = {};
        jsonData['specialityId'] = specialityId;
        jsonData['specialityName'] = inputName.value;
        jsonData['code'] = inputCode.value;

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
                var inputName = document.getElementById('speciality-name' + specialityId);
                var inputCode = document.getElementById('speciality-code' + specialityId);
                inputName.readOnly = true;
                inputCode.readOnly = true;
                inputName.required = false;
                inputCode.required = false;

                var buttonSubmit = document.getElementById('submit-speciality' + specialityId);
                buttonSubmit.style.display = 'none';

                var buttonEdit = document.getElementById('button-edit-speciality' + specialityId);
                buttonEdit.style.display = 'block';

                var buttonDelete = document.getElementById('button-delete-speciality' + specialityId);
                buttonDelete.style.display = 'block';

                getSpecialityList(globalSearch);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    }
    form.classList.add('was-validated');
}

//Редактирование данных специальности
function editSpeciality(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var specialityId = event.currentTarget.getAttribute('href');

    var inputName = document.getElementById('speciality-name' + specialityId);
    var inputCode = document.getElementById('speciality-code' + specialityId);
    inputName.readOnly = false;
    inputCode.readOnly = false;
    inputName.required = true;
    inputCode.required = true;

    var buttonSubmit = document.getElementById('submit-speciality' + specialityId);
    buttonSubmit.style.display = 'block';

    var buttonEdit = document.getElementById('button-edit-speciality' + specialityId);
    buttonEdit.style.display = 'none';

    var buttonDelete = document.getElementById('button-delete-speciality' + specialityId);
    buttonDelete.style.display = 'none';
}

//Получение списка специальностей
function getSpecialityList(search) {
    fetch(`${adminId}/specialityList?search=` + search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (specialityListHtml) {
            var divList = document.getElementById('speciality-list');
            divList.innerHTML = '';
            divList.innerHTML = specialityListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

function getPageNewSpeciality(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var form = document.getElementById('add-speciality-form');
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

            fetchNewSpeciality();
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });
}

function fetchNewSpeciality() {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-add');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('newSpecialityForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var form = document.getElementById('add-speciality-form');
        var url = form.getAttribute('action');

        var specialityName = document.getElementById('name-speciality').value;
        var specialityCode = document.getElementById('code-speciality').value;

        const jsonData = {};
        jsonData['specialityId'] = 0;
        jsonData['specialityName'] = specialityName;
        jsonData['code'] = specialityCode;
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
                    getSpecialityList(globalSearch);
                else
                    fetchNewSpeciality();
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}