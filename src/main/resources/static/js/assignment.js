var dictionaryAssignment = new Map();

function createNewAssignment(selectedDiscipline, selectedGroup, selectedType) {
    // Отправить GET-запрос к серверу по адресу "assignment/newschedule"
    // и обработать полученную модальную страницу
    fetch('/assignment/new-assignment')
        .then(function (response) {
            return response.text();
        })
        .then(function (modalHtml) {
            // Создать модальное окно и поместить в него полученную страницу
            var modal = document.getElementById('modal-div');
            modal.innerHTML = modalHtml;

            // Отобразить модальное окно
            modal.style.display = 'block';

            // Закрытие модального окна при клике на крестик
            var closeBtn = modal.querySelector('.close');
            closeBtn.addEventListener('click', function () {
                modal.style.display = 'none';
                modal.innerHTML = '';
            });

            var submit = modal.querySelector('#submit');
            submit.addEventListener('click', event => {
                var form = document.getElementById('newAssignmentForm');
                event.preventDefault();
                if (form.checkValidity() === false) {
                    event.stopPropagation();
                }
                else {
                    fetchNewAssignment(modal, selectedDiscipline, selectedGroup, selectedType);
                }
                form.classList.add('was-validated');
                return false;
            });
        })
        .catch(function (error) {
            console.error('Ошибка получения', error);
        });

    function fetchNewAssignment(modal, selectedDiscipline, selectedGroup, selectedType) {
        var typeInput = modal.querySelector('#type');
        var type = typeInput.value;
        var descrInput = modal.querySelector('#description');
        var description = descrInput.value;
        var maxPointsInput = modal.querySelector('#max-points');
        var maxPoints = maxPointsInput.value;
        var dateInput = modal.querySelector('#date');
        var date = dateInput.value;

        // Преобразование в объект JSON
        const jsonData = {};
        jsonData['type'] = type;
        jsonData['description'] = description;
        jsonData['maxPoints'] = maxPoints;
        jsonData['date'] = date;
        jsonData['subjectId'] = selectedDiscipline;
        jsonData['groupId'] = selectedGroup;
        jsonData['workloadType'] = selectedType;
        jsonData['teacherId'] = teacherId;

        const url = '/assignment/new-assignment';
        const csrfToken = document.getElementById("csrfToken").value;
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(jsonData),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Ошибка сохранения! Проверьте правильность введенных данных!");
                }
                return response.text();
            })
            .then(data => {
                customAlert(data);
                if (data === "Сохранение успешно!") {
                    getGrades(selectedDiscipline, selectedGroup, selectedType);
                    getAssignments(selectedDiscipline, selectedGroup, selectedType);
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    }
}

//Сохраняем изменения назначений на сервере
function saveChangesOfAssignments(selectedDiscipline, selectedGroup, selectedType) {
    // Преобразование словаря в объект JSON
    const jsonData = {};
    for (let [key, value] of dictionaryAssignment) {
        jsonData[key] = value;
    }

    if (jsonData.length === 0)
        return;

    const url = '/assignment';
    const csrfToken = document.getElementById("csrfToken").value;
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': csrfToken
        },
        body: JSON.stringify(jsonData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Ошибка сохранения! Проверьте правильность введенных данных!");
            }
            return response.text();
        })
        .then(data => {
            customAlert("Сохранение успешно!");
            getGrades(selectedDiscipline, selectedGroup, selectedType);
            getAssignments(selectedDiscipline, selectedGroup, selectedType);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });

}

//Обработчик изменяемых ячеек таблицы посещений
function editableAssignment() {
    // Получаем все редактируемые ячейки таблицы
    const editableCells = document.querySelectorAll('.editable-assignment');

    // Добавляем обработчик события input к каждой редактируемой ячейке
    editableCells.forEach(cell => {
        cell.addEventListener('input', handleInput);
    });

    // Обработчик события input
    function handleInput(event) {
        const inputTarget = event.target;

        const cell = inputTarget.parentNode;
        const row = cell.parentNode;
        const assignmentId = row.getAttribute('data-assignment-id');

        const cells = row.querySelectorAll('td'); // Выбираем все ячейки в строке

        var arrayOfInput = [];
        cells.forEach(cell => {
            const input = cell.querySelector('input');
            arrayOfInput.push(input);
        });

        var type = arrayOfInput[0].value;
        var description = arrayOfInput[1].value;
        var maxPoints = arrayOfInput[2].value;
        var date = arrayOfInput[3].value;

        const key = `${assignmentId}`;
        const value = [ type, description, maxPoints, date ];
        dictionaryAssignment.set(key, value);
    }
}

//Удаление работы
function deleteAssignment(button, selectedDiscipline, selectedGroup, selectedType) {
    // Получаем родительскую строку (tr) для кнопки удаления
    const row = button.closest('tr');
    const firstTd = row.firstChild;
    const input = firstTd.querySelector('input');
    const assignmentId = row.getAttribute('data-assignment-id');
    customConfirm('Вы уверены, что хотите удалить работу ' + input.value + "?");
    var modal = document.getElementById('modal-div');
    // Обработчики клика по кнопкам в окне подтверждения
    document.querySelector(".confirmModalYes").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
        fetchDeleteAssignment();
    });

    document.querySelector(".confirmModalNo").addEventListener("click", function() {
        // Закрыть окно подтверждения
        modal.innerHTML = '';
    });

    function fetchDeleteAssignment() {
        //Запрос на удаление
        const url = '/assignment';
        const csrfToken = document.getElementById("csrfToken").value;
        fetch(url, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            },
            body: JSON.stringify(assignmentId),
        })
            .then(response => {
                return response.text();
            })
            .then(data => {
                customAlert(data);
                getGrades(selectedDiscipline, selectedGroup, selectedType);
                getAssignments(selectedDiscipline, selectedGroup, selectedType);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                customAlert(error.message);
            });
    }
}

function createButton(selectedDiscipline, selectedGroup, selectedType) {
    const assignmentSaveBtn = document.querySelector('.assignment-save');
    assignmentSaveBtn.innerHTML = '';
    const buttonCreateAssignment = document.createElement('button');
    buttonCreateAssignment.setAttribute('id', 'createNewAssignmentBtn');
    buttonCreateAssignment.classList.add('btn');
    buttonCreateAssignment.classList.add('btn-primary');
    buttonCreateAssignment.classList.add('btn-save-changes')
    buttonCreateAssignment.textContent = 'Создать новую работу';
    assignmentSaveBtn.appendChild(buttonCreateAssignment);

    buttonCreateAssignment.addEventListener('click', function () {
        createNewAssignment(selectedDiscipline, selectedGroup, selectedType);
    });
}

//Отображение назначений
function getAssignments(selectedDiscipline, selectedGroup, selectedType) {
    fetch(`${teacherId}/assignments?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            //Получаем элемент для таблицы назначений
            const assignmentTable = document.querySelector('.assignment-table');
            assignmentTable.innerHTML = '';

            // создаем подпись таблицы
            const h2AssignmentContent = document.createElement('h2');
            h2AssignmentContent.textContent = "Проводимые работы: ";
            assignmentTable.appendChild(h2AssignmentContent);
            createButton(selectedDiscipline, selectedGroup, selectedType);

            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                assignmentTable.appendChild(pContent);
                return;
            }

            // таблица для назначений
            const tableAssignment = document.createElement('table');
            tableAssignment.classList.add('scrollable-table');

            // Создаем заголовок таблицы
            const theadAssignment = document.createElement('thead');
            const headerRowAssignment = document.createElement('tr');

            // Добавляем столбцы в заголовок таблицы
            const typeHeaderAssignment = document.createElement('th');
            typeHeaderAssignment.textContent = 'Вид работы';
            typeHeaderAssignment.title = 'Вид работы';
            headerRowAssignment.appendChild(typeHeaderAssignment);
            const descHeaderAssignment = document.createElement('th');
            descHeaderAssignment.textContent = 'Описание';
            descHeaderAssignment.title = 'Описание';
            headerRowAssignment.appendChild(descHeaderAssignment);
            const pointsHeaderAssignment = document.createElement('th');
            pointsHeaderAssignment.textContent = 'Макс. баллы';
            pointsHeaderAssignment.title = 'Максимальное количество баллов';
            headerRowAssignment.appendChild(pointsHeaderAssignment);
            const dateHeaderAssignment = document.createElement('th');
            dateHeaderAssignment.textContent = 'Дата';
            dateHeaderAssignment.title = 'Дата';
            headerRowAssignment.appendChild(dateHeaderAssignment);
            const removing = document.createElement('th');
            removing.innerHTML = '&times;';
            headerRowAssignment.appendChild(removing);

            theadAssignment.appendChild(headerRowAssignment);
            tableAssignment.appendChild(theadAssignment);
            // Создаем тело таблицы назначений
            const tbodyAssignment = document.createElement('tbody');

            //--- заголовки таблицы для назначений

            data.forEach(assignment => {
                //Строки для таблицы назначений
                const row = document.createElement('tr');
                row.setAttribute('data-assignment-id', assignment.assignmentId);

                const typeCell = document.createElement('td');
                typeCell.classList.add('editable-cell');
                typeCell.classList.add('editable-assignment');
                const inputType = document.createElement('input');
                inputType.type = 'text';
                inputType.required = true;
                inputType.value = assignment.type;
                typeCell.appendChild(inputType);
                row.appendChild(typeCell);

                const descrCell = document.createElement('td');
                descrCell.classList.add('editable-cell');
                descrCell.classList.add('editable-assignment');
                const inputDescr = document.createElement('input');
                inputDescr.type = 'text';
                inputDescr.required = false;
                inputDescr.value = assignment.description;
                descrCell.appendChild(inputDescr);
                row.appendChild(descrCell);

                const pointCell = document.createElement('td');
                pointCell.classList.add('editable-cell');
                pointCell.classList.add('editable-assignment');
                const inputPoint = document.createElement('input');
                inputPoint.type = 'number';
                inputPoint.required = true;
                inputPoint.min = 0;
                inputPoint.value = assignment.maxPoints;
                pointCell.appendChild(inputPoint);
                row.appendChild(pointCell);

                //преобразование даты
                const dateString = assignment.dateFormat;
                const parts = dateString.split('.');
                const day = parts[0];
                const month = parts[1];
                const year = parts[2];
                const dateObject = new Date(`${year}-${month}-${day}`);
                const formattedDate = dateObject.toISOString().split('T')[0];

                const dateCell = document.createElement('td');
                dateCell.classList.add('editable-cell');
                dateCell.classList.add('editable-assignment');
                const inputDate = document.createElement('input');
                inputDate.type = 'date';
                inputDate.required = true;
                inputDate.value = formattedDate;
                dateCell.appendChild(inputDate);
                row.appendChild(dateCell);

                const deleteCell = document.createElement('td');
                const deleteButton = document.createElement('button');
                deleteButton.classList.add('delete-assignment-btn');
                deleteButton.innerHTML = '&times;';
                deleteCell.appendChild(deleteButton);
                row.appendChild(deleteCell);

                deleteButton.addEventListener('click', () => {
                    deleteAssignment(deleteButton, selectedDiscipline, selectedGroup, selectedType);
                });

                tbodyAssignment.appendChild(row);
            });
            tableAssignment.appendChild(tbodyAssignment);
            assignmentTable.appendChild(tableAssignment);

            //Кнопка сохранения
            const assignmentSaveBtn = document.querySelector('.assignment-save');
            const buttonSaveAssignment = document.createElement('button');
            buttonSaveAssignment.setAttribute('id', 'saveChangesOfStudentsButton');
            buttonSaveAssignment.classList.add('btn');
            buttonSaveAssignment.classList.add('btn-primary');
            buttonSaveAssignment.classList.add('btn-save-changes')
            buttonSaveAssignment.textContent = 'Сохранить изменения';
            assignmentSaveBtn.appendChild(buttonSaveAssignment);

            dictionaryAssignment = new Map();
            editableAssignment();

            buttonSaveAssignment.addEventListener('click', function() {
                saveChangesOfAssignments(selectedDiscipline, selectedGroup, selectedType);
            });
        })
        .catch(error => console.error(error));
}