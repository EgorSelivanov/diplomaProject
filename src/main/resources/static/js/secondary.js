function customAlert(message) {
    // Создать модальное окно и поместить в него полученную страницу
    var modal = document.getElementById('modal-div');

    var alertContainer = document.createElement('div');
    alertContainer.classList.add('modall');

    var alertContent = document.createElement('div');
    alertContent.classList.add('modall-content');
    var spanClose = document.createElement('span');
    spanClose.classList.add('close');
    spanClose.innerHTML = '&times;';
    alertContent.appendChild(spanClose);

    alertContainer.appendChild(alertContent);

    var alertMessage = document.createElement('p');
    alertMessage.textContent = message;

    alertContent.appendChild(alertMessage);

    modal.innerHTML = '';
    modal.appendChild(alertContainer);

    // Отобразить модальное окно
    modal.style.display = 'block';

    // Закрытие модального окна при клике на крестик
    var closeBtn = modal.querySelector('.close');
    closeBtn.addEventListener('click', function () {
        modal.style.display = 'none';
        modal.innerHTML = '';
    });
}

function customConfirm(message) {
    // Создать модальное окно и поместить в него полученную страницу
    var modal = document.getElementById('modal-div');

    var confirmContainer = document.createElement('div');
    confirmContainer.classList.add('confirmModal');

    var confirmContent = document.createElement('div');
    confirmContent.classList.add('confirmModal-content');

    confirmContainer.appendChild(confirmContent);

    var confirmMessage = document.createElement('p');
    confirmMessage.classList.add('confirmModal-message');
    confirmMessage.textContent = message;
    confirmContent.appendChild(confirmMessage);

    var confirmButtons = document.createElement('div');
    confirmButtons.classList.add('confirmModal-buttons');
    confirmContent.appendChild(confirmButtons);

    var buttonYes = document.createElement('button');
    buttonYes.classList.add('confirmModalYes');
    buttonYes.textContent = 'Да';
    var buttonNo = document.createElement('button');
    buttonNo.classList.add('confirmModalNo');
    buttonNo.textContent = 'Отмена';
    confirmButtons.appendChild(buttonYes);
    confirmButtons.appendChild(buttonNo);

    modal.innerHTML = '';
    modal.appendChild(confirmContainer);

    // Отобразить модальное окно
    modal.style.display = 'block';
}

function showModal(modal, modalHtml) {
    modal.innerHTML = modalHtml;

    // Отобразить модальное окно
    modal.style.display = 'block';

    // Закрытие модального окна при клике на крестик
    var closeBtn = modal.querySelector('.close');

    if (closeBtn === null)
        return;

    closeBtn.addEventListener('click', function () {
        modal.style.display = 'none';
        modal.innerHTML = '';
    });
}

function confirmChangePassword(event, message) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');
    var userId = event.currentTarget.id.replace('button-change-password', '');

    customConfirm(message);
    var modal = document.getElementById('modal-div');

    document.querySelector(".confirmModalYes").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
        fetch(url, {
            method: 'GET'
        })
            .then(response => {
                return response.text();
            })
            .then(function (modalHtml) {
                // Создать модальное окно и поместить в него полученную страницу
                var modal = document.getElementById('modal-div');

                showModal(modal, modalHtml);

                fetchNewPassword(userId);
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

function fetchNewPassword(userId) {
    var modal = document.getElementById('modal-div');
    var submit = modal.querySelector('#submit-change');
    submit.addEventListener('click', event => {
        var formNew = document.getElementById('passwordForm');
        event.preventDefault();
        if (formNew.checkValidity() === false) {
            event.stopPropagation();
            formNew.classList.add('was-validated');
            return;
        }

        var password = document.getElementById('new-password').value;
        var confirmPassword = document.getElementById('confirm-password').value;

        if (password !== confirmPassword) {
            customAlert('Пароли не свопадают!');
            return;
        }

        const csrfToken = document.getElementById("csrfToken").value;

        fetch(`${adminId}/change-password/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(password),
        })
            .then(function (response) {
                return response.text();
            })
            .then(function (modalHtml) {
                showModal(modal, modalHtml);

                if (modalHtml === '')
                    customAlert('Пароль успешно сброшен!');
                else
                    fetchNewPassword(userId);
            })
            .catch(function (error) {
                console.error('Ошибка получения', error);
            });
        formNew.classList.add('was-validated');
    });
}