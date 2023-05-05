function togglePasswordVisibility() {
    const passwordInput = document.getElementById("password");
    const passwordToggle = document.getElementById("password-toggle");

    passwordToggle.addEventListener("mousedown", function() {
        passwordInput.type = "text";
    });

    passwordToggle.addEventListener("mouseup", function() {
        passwordInput.type = "password";
    });
}
