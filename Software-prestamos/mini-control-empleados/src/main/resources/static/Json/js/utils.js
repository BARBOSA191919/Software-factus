/* ------------------Glassmorphism Sidebar------------------ */
const GlassmorphismSidenav = document.getElementById("GlassmorphismSidenav");
const GlassmorphismPname = document.getElementById("GlassmorphismPname");
const GlassmorphismTogglericon = document.getElementById("GlassmorphismTogglericon");
const GlassmorphismMenuText = document.querySelectorAll(".GlassmorphismMenuText");
const themeText = document.getElementById("theme-text");
const themeIcon = document.getElementById("theme-icon");

let GlassmorphismisCollapsed = false;
let isDarkMode = false;

function GlassmorphismToggle() {
    GlassmorphismisCollapsed = !GlassmorphismisCollapsed;
    GlassmorphismSidenav.style.width = GlassmorphismisCollapsed ? "100px" : "200px";
    GlassmorphismPname.style.display = GlassmorphismisCollapsed ? "none" : "block";

    GlassmorphismMenuText.forEach(text => {
        text.style.display = GlassmorphismisCollapsed ? "none" : "block";
    });

    // Rotate toggle icon when collapsed
    if (GlassmorphismisCollapsed) {
        GlassmorphismTogglericon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M13.5 4.5L21 12m0 0l-7.5 7.5M21 12H3" />
            </svg>`;
    } else {
        GlassmorphismTogglericon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-5">
                <path stroke-linecap="round" stroke-linejoin="round" d="m5.25 4.5 7.5 7.5-7.5 7.5m6-15 7.5 7.5-7.5 7.5" />
            </svg>`;
    }
}

// Dark mode toggle function
function toggleDarkMode() {
    isDarkMode = !isDarkMode;
    document.documentElement.classList.toggle('dark');

    if (isDarkMode) {
        themeText.textContent = "Dark Mode";
        themeIcon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6 text-white">
                <path stroke-linecap="round" stroke-linejoin="round" d="M21.752 15.002A9.718 9.718 0 0118 15.75c-5.385 0-9.75-4.365-9.75-9.75 0-1.33.266-2.597.748-3.752A9.753 9.753 0 003 11.25C3 16.635 7.365 21 12.75 21a9.753 9.753 0 009.002-5.998z" />
            </svg>`;
    } else {
        themeText.textContent = "Light Mode";
        themeIcon.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" class="size-6 text-gray-800">
                <path stroke-linecap="round" stroke-linejoin="round" d="M12 3v1m0 16v1m8.485-8.485h1M3.515 12H2.5m15.364-6.364l.707.707M5.636 18.364l.707.707m12.02 0l-.707.707M5.636 5.636l-.707.707M12 8a4 4 0 1 1 0 8 4 4 0 0 1 0-8z" />
            </svg>`;
    }
}

// Confetti function
function launchConfetti() {
    confetti({
        particleCount: 100,
        spread: 70,
        origin: {y: 0.6}
    });
}

// Ajustar posición de dropdowns en tablas
document.addEventListener('shown.bs.dropdown', function (event) {
    const dropdown = event.target;

    if (dropdown.closest('.table')) {
        const menu = dropdown.querySelector('.dropdown-menu');
        const buttonRect = dropdown.getBoundingClientRect();

        const left = buttonRect.left;
        const top = buttonRect.bottom + 3; // Siempre debajo del botón

        menu.style.position = 'fixed';
        menu.style.top = `${top}px`;
        menu.style.left = `${left}px`;
        menu.style.zIndex = '1050';
        menu.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';

        // Asegurar que el menú sea visible incluso si la tabla tiene overflow
        const tableContainer = dropdown.closest('.table-responsive') || dropdown.closest('table');
        if (tableContainer) {
            tableContainer.style.overflow = 'visible';
        }
    }
});

// Cerrar dropdowns al hacer clic fuera
document.addEventListener('click', (e) => {
    if (!e.target.closest('.dropdown')) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.remove('show');
        });
    }
});

// Hide all content sections except dashboard by default
$('.content-section').hide();
$('#dashboard-content').show();

// Dashboard link navigation
$('#dashboard-link').click(function (e) {
    e.preventDefault();
    $('.content-section').hide();
    $('#dashboard-content').show();
});

$('.clickable-card[data-section="dashboard"]').click(function () {
    $('.content-section').hide();
    $('#dashboard-content').show();
});

// Confirmar logout
window.confirmLogout = function () {
    Swal.fire({
        title: '¿Estás seguro?',
        text: "¿Quieres cerrar sesión?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, cerrar sesión',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            document.getElementById("logoutForm").submit();
        }
    });
};