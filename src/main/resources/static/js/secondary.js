function customAlert(message) {
    // Создать модальное окно и поместить в него полученную страницу
    var modal = document.getElementById('modal-div');

    var alertContainer = document.createElement('div');
    alertContainer.classList.add('modal');

    var alertContent = document.createElement('div');
    alertContent.classList.add('modal-content');
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