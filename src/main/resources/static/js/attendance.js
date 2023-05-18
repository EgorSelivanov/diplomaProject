// Словари для контроля за изменением ячеек
var dictionaryAttendance = new Map();

//Обработчик изменяемых ячеек таблицы посещений
function editableAttendances() {
    // Получаем все редактируемые ячейки таблицы
    const editableCells = document.querySelectorAll('.editable-attendance');

    // Добавляем обработчик события input к каждой редактируемой ячейке
    editableCells.forEach(cell => {
        cell.addEventListener('input', handleInput);
    });

    // Обработчик события input
    function handleInput(event) {
        const input = event.target; // Текущая ячейка, в которой произошло изменение
        const val = input.value;

        const cell = input.parentNode;
        const attendanceId = cell.getAttribute('attendance-id');

        const key = `${attendanceId}`;
        dictionaryAttendance.set(key, val);
    }
}

//Сохраняем изменения посещений на сервере
function saveChangesOfAttendance(selectedDiscipline, selectedGroup, selectedType) {
    // Преобразование словаря в объект JSON
    const jsonData = {};
    for (let [key, value] of dictionaryAttendance) {
        jsonData[key] = value;
    }

    if (jsonData.length === 0)
        return;

    const url = '/attendance';
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
            getAttendances(selectedDiscipline, selectedGroup, selectedType);
        })
        .catch(error => {
            console.error('Ошибка:', error);
            customAlert(error.message);
        });
}

//Получаем журнал посещений по группе и дисциплине
function getAttendances(selectedDiscipline, selectedGroup, selectedType) {
    fetch(`${teacherId}/journal?discipline=${selectedDiscipline}&group=${selectedGroup}&type=${selectedType}`,
        { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            // Получаем элемент, в который будет добавлена таблица
            const journalTable = document.querySelector('.journal-table');
            const journalSaveBtn = document.querySelector('.journal-save');
            //Очищаем содержимое
            journalTable.innerHTML = '';
            journalSaveBtn.innerHTML = '';

            // создаем подпись таблицы
            const h2Content = document.createElement('h2');
            h2Content.textContent = "Таблица посещаемости: ";
            journalTable.appendChild(h2Content);

            if (Array.isArray(data) && data.length === 0) {
                const pContent = document.createElement('p');
                pContent.textContent = "Данных для отображения нет";
                journalTable.appendChild(pContent);
                return;
            }

            // Создаем таблицу
            const table = document.createElement('table');
            table.classList.add('scrollable-table');

            // Создаем заголовок таблицы
            const thead = document.createElement('thead');
            const headerRow = document.createElement('tr');

            // Добавляем столбцы в заголовок таблицы
            const numberHeader = document.createElement('th');
            numberHeader.textContent = '№';
            numberHeader.title = '№';
            headerRow.appendChild(numberHeader);
            const nameHeader = document.createElement('th');
            nameHeader.textContent = 'ФИО';
            nameHeader.title = 'ФИО';
            headerRow.appendChild(nameHeader);

            data[0].dateList.forEach(date => {
                const dateHeader = document.createElement('th');
                dateHeader.textContent = date;
                dateHeader.title = date;
                headerRow.appendChild(dateHeader);
            });

            const summaryHeader = document.createElement('th');
            summaryHeader.textContent = 'Сумма';
            summaryHeader.title = 'Сумма';
            headerRow.appendChild(summaryHeader);

            thead.appendChild(headerRow);
            table.appendChild(thead);

            // Создаем тело таблицы
            const tbody = document.createElement('tbody');

            var number = 1;

            data.forEach(student => {
                const row = document.createElement('tr');

                const numberOfStudentCell = document.createElement('td');
                numberOfStudentCell.textContent = number;
                number += 1;
                row.appendChild(numberOfStudentCell);

                const nameCell = document.createElement('td');
                nameCell.textContent = student.fullName;
                nameCell.title = student.fullName;
                row.appendChild(nameCell);

                var summ = 0;
                for (var i = 0; i < student.attendanceIdList.length; i++) {
                    const numberCell = document.createElement('td');
                    //делаем таблицу изменяемой
                    numberCell.classList.add('editable-cell');
                    numberCell.classList.add('editable-attendance');
                    numberCell.setAttribute('attendance-id', student.attendanceIdList[i]);
                    const input = document.createElement('input');
                    input.type = 'number';
                    input.value = student.presentList[i];
                    summ += student.presentList[i];
                    numberCell.appendChild(input);
                    row.appendChild(numberCell);
                }
                const sumCell = document.createElement('td');
                sumCell.classList.add('sumCell');
                sumCell.textContent = summ;
                sumCell.title = summ;
                row.appendChild(sumCell);

                tbody.appendChild(row);
            });

            // создаем label и input для коэффициента
            const label = document.createElement('label');
            label.textContent = 'Коэффициент для суммы: ';
            const inputK = document.createElement('input');
            inputK.classList.add('koeff');
            inputK.type = 'number';
            inputK.step = "0.1";
            inputK.value = '1';
            inputK.id = 'inputK';
            label.setAttribute("for", "inputK");
            journalTable.appendChild(label);
            journalTable.appendChild(inputK);
            let previousValue = "1";
            inputK.addEventListener("input", function(event) {
                const cells = document.querySelectorAll('.sumCell');
                var k = parseFloat(event.target.value);
                if (isNaN(k))
                    return;
                cells.forEach(cell => {
                    var value = parseFloat(cell.textContent) * k / parseFloat(previousValue);
                    cell.textContent = value;
                    cell.textContent = value.toFixed(2);
                    cell.title = cell.textContent;
                })
                previousValue = event.target.value;
            });

            table.appendChild(tbody);

            // Добавляем таблицу
            journalTable.appendChild(table);

            //Добавляем кнопку сохранения
            journalSaveBtn.innerHTML = '';
            const buttonSaveAttendance = document.createElement('button');
            buttonSaveAttendance.setAttribute('id', 'saveChangesOfStudentsButton');
            buttonSaveAttendance.classList.add('btn');
            buttonSaveAttendance.classList.add('btn-primary');
            buttonSaveAttendance.classList.add('btn-save-changes')
            buttonSaveAttendance.textContent = 'Сохранить изменения';
            journalSaveBtn.appendChild(buttonSaveAttendance);

            dictionaryAttendance = new Map();
            editableAttendances();

            buttonSaveAttendance.addEventListener('click', function() {
                saveChangesOfAttendance(selectedDiscipline, selectedGroup, selectedType);
            });

        })
        .catch(error => console.error(error));
}