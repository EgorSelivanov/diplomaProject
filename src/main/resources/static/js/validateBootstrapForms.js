(function() {
    'use strict';
    window.addEventListener('load', checkValid, false);
})();

function checkValid() {
    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    var forms = document.getElementsByClassName('needs-validation');

    // Loop over them and prevent submission
    var validation = Array.prototype.filter.call(forms, function(form) {
        form.addEventListener('submit', function(event) {
            if (form.checkValidity() === false) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });
}

// Создание экземпляра Mutation Observer
var observer = new MutationObserver(checkValid);

// Настройка параметров Mutation Observer
var config = {
    childList: true, // Отслеживать изменения в потомках элемента
    subtree: true // Отслеживать изменения во всех вложенных элементах
};

// Начать отслеживание изменений в целевом элементе
var targetElement = document.body; // Здесь можно указать другой элемент, если нужно
observer.observe(targetElement, config);