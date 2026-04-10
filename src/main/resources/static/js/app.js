document.addEventListener("DOMContentLoaded", function () {
    var root = document.documentElement;
    var toggleButton = document.querySelector("[data-theme-toggle]");
    var toggleIcon = document.querySelector("[data-theme-icon]");

    function applyTheme(theme) {
        root.setAttribute("data-bs-theme", theme);
        localStorage.setItem("app-theme", theme);
        if (toggleIcon) {
            toggleIcon.className = theme === "dark" ? "bi bi-sun-fill" : "bi bi-moon-stars-fill";
        }
        if (toggleButton) {
            toggleButton.setAttribute("aria-label", theme === "dark" ? "Switch to light theme" : "Switch to dark theme");
        }
    }

    if (toggleButton) {
        toggleButton.addEventListener("click", function () {
            var currentTheme = root.getAttribute("data-bs-theme") || "light";
            applyTheme(currentTheme === "dark" ? "light" : "dark");
        });
    }

    applyTheme(root.getAttribute("data-bs-theme") || "light");

    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            var message = form.getAttribute("data-confirm") || "Are you sure?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
