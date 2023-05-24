//Подтверждение удаления админа
function confirmDeletion(event) {
    event.preventDefault(); // Предотвращаем переход по ссылке по умолчанию

    var url = event.currentTarget.getAttribute('href');

    customConfirm('Вы уверены, что хотите удалить администратора?');
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
                window.location.href = `/admin/${adminId}`;
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

//Получение списка админов
function getAdminList(search) {
    fetch(`${adminId}/adminList?search=` + search, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        }
    })
        .then(response => {
            return response.text();
        })
        .then(function (adminListHtml) {
            var divList = document.getElementById('admin-list');
            divList.innerHTML = '';
            divList.innerHTML = adminListHtml;
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}