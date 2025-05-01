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

document.addEventListener('DOMContentLoaded', function () {
    // Función para animar los contadores
    function animateCounter(elementId, finalValue, duration = 2000) {
        const element = document.getElementById(elementId);
        if (!element) return;

        let startValue = 0;
        const increment = finalValue / (duration / 16);
        const timer = setInterval(() => {
            startValue += increment;
            if (startValue >= finalValue) {
                clearInterval(timer);
                element.textContent = finalValue;
            } else {
                element.textContent = Math.floor(startValue);
            }
        }, 16);
    }

    // Agregar efecto hover a las tarjetas
    const cards = document.querySelectorAll('.clickable-card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function () {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 10px 20px rgba(0,0,0,0.1)';
            this.style.transition = 'all 0.3s ease';
        });
        card.addEventListener('mouseleave', function () {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 0.125rem 0.25rem rgba(0,0,0,0.075)';
            this.style.transition = 'all 0.3s ease';
        });
    });

    // Cargar datos de resumen (clientes, productos y facturas)
    function loadDashboardSummary() {
        // Contador de clientes
        $.ajax({
            url: '/api/clientes/count',
            method: 'GET',
            success: function (totalClientes) {
                setTimeout(() => {
                    animateCounter('total-clientes', totalClientes || 0);
                }, 500);
            },
            error: function (xhr) {
                console.error('Error loading client count:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudo cargar el conteo de clientes'
                });
            }
        });

        // Contador de productos
        $.ajax({
            url: '/api/productos/count',
            method: 'GET',
            success: function (totalProductos) {
                setTimeout(() => {
                    animateCounter('total-productos', totalProductos || 0);
                }, 500);
            },
            error: function (xhr) {
                console.error('Error loading product count:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudo cargar el conteo de productos'
                });
            }
        });

        // Contador de facturas
        $.ajax({
            url: '/api/facturas/count',
            method: 'GET',
            success: function (totalFacturas) {
                setTimeout(() => {
                    animateCounter('total-facturas', totalFacturas || 0);
                }, 500);
            },
            error: function (xhr) {
                console.error('Error loading invoice count:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudo cargar el conteo de facturas'
                });
            }
        });
    }

    // Variables para la paginación de clientes
    let currentClientPage = 1;
    const clientsPerPage = 8;
    let allClients = [];

    // Variables para la paginación de productos
    let currentProductPage = 1;
    const productsPerPage = 8;
    let allProducts = [];

    // Variables para la paginación de facturas
    let currentFacturaPage = 1;
    const facturasPerPage = 8;
    let allFacturas = [];

    // Función para renderizar clientes con paginación
    function renderClients(clients, page) {
        const tbody = $('#ultimos-clientes tbody');
        tbody.empty();

        if (clients.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="4" class="text-center">No hay clientes recientes</td>
                </tr>
            `);
            updatePaginationControls('client', clients.length, page);
            return;
        }

        const start = (page - 1) * clientsPerPage;
        const end = start + clientsPerPage;
        const paginatedClients = clients.slice(start, end);

        paginatedClients.forEach(cliente => {
            const facturaCount = cliente.facturaCount || 0;
            tbody.append(`
                <tr>
                    <td>${cliente.nombre}</td>
                    <td>${cliente.correo || 'N/A'}</td>
                    <td>${facturaCount}</td>
                    <td>
                        <div class="dropdown">
                          
                        </div>
                    </td>
                </tr>
            `);
        });

        updatePaginationControls('client', clients.length, page);

        $('.edit-cliente').click(function () {
            const id = $(this).data('id');
            editCliente(id);
        });
        $('.delete-cliente').click(function () {
            const id = $(this).data('id');
            openDeleteModal('cliente', id);
        });
    }

    // Función para renderizar productos con paginación
    function renderProducts(products, page) {
        const tbody = $('#productos-mas-vendidos tbody');
        tbody.empty();

        if (products.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="4" class="text-center">No hay productos disponibles</td>
                </tr>
            `);
            updatePaginationControls('product', products.length, page);
            return;
        }

        const start = (page - 1) * productsPerPage;
        const end = start + productsPerPage;
        const paginatedProducts = products.slice(start, end);

        paginatedProducts.forEach(producto => {
            tbody.append(`
                <tr>
                    <td>${producto.name}</td>
                    <td>$${parseFloat(producto.price).toFixed(2)}</td>
                    <td>${producto.salesCount || 0}</td>
                    <td><span class="badge bg-${producto.excluded ? 'success' : 'danger'}">${producto.excluded ? 'Activo' : 'Inactivo'}</span></td>
                </tr>
            `);
        });

        updatePaginationControls('product', products.length, page);
    }

    // Función para renderizar facturas con paginación
    function renderFacturas(facturas, page) {
        const tbody = $('#facturas-recientes tbody');
        tbody.empty();

        if (facturas.length === 0) {
            tbody.append(`
                <tr>
                    <td colspan="4" class="text-center">No hay facturas recientes</td>
                </tr>
            `);
            updatePaginationControls('factura', facturas.length, page);
            return;
        }

        const start = (page - 1) * facturasPerPage;
        const end = start + facturasPerPage;
        const paginatedFacturas = facturas.slice(start, end);

        paginatedFacturas.forEach(factura => {
            const clienteNombre = factura.cliente ? factura.cliente.nombre : 'N/A';
            const clienteCorreo = factura.cliente ? factura.cliente.correo || 'N/A' : 'N/A';
            tbody.append(`
                <tr>
                    <td>${clienteNombre}</td>
                    <td>${clienteCorreo}</td>
                    <td>${factura.numero}</td>
                    <td>
                        <div class="dropdown">
                            <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                Acciones
                            </button>
                            <ul class="dropdown-menu">
                                <li><a class="dropdown-item" href="/api/facturas/descargar/${factura.id}" target="_blank">Descargar PDF</a></li>
                                <li><a class="dropdown-item" href="/api/facturas/descargar-xml/${factura.numero}" target="_blank">Descargar XML</a></li>
                            </ul>
                        </div>
                    </td>
                </tr>
            `);
        });

        updatePaginationControls('factura', facturas.length, page);
    }

    // Función para crear controles de paginación
    function updatePaginationControls(type, totalItems, currentPage) {
        const totalPages = Math.ceil(totalItems / (type === 'client' ? clientsPerPage : type === 'product' ? productsPerPage : facturasPerPage));
        const paginationContainer = $(`#${type}-pagination`);
        paginationContainer.empty();

        if (totalPages <= 1) return;

        let pageLinks = '';
        for (let i = 1; i <= totalPages; i++) {
            pageLinks += `
                <li class="page-item ${currentPage === i ? 'active' : ''}">
                    <a class="page-link" href="#" data-type="${type}" data-page="${i}">${i}</a>
                </li>
            `;
        }

        paginationContainer.append(`
            <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                        <a class="page-link" href="#" data-type="${type}" data-page="${currentPage - 1}">Anterior</a>
                    </li>
                    ${pageLinks}
                    <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="#" data-type="${type}" data-page="${currentPage + 1}">Siguiente</a>
                    </li>
                </ul>
            </nav>
        `);

        paginationContainer.find('.page-link').click(function (e) {
            e.preventDefault();
            const page = parseInt($(this).data('page'));
            const linkType = $(this).data('type');

            if (linkType === 'client' && page > 0 && page <= totalPages) {
                currentClientPage = page;
                renderClients(allClients, currentClientPage);
            } else if (linkType === 'product' && page > 0 && page <= totalPages) {
                currentProductPage = page;
                renderProducts(allProducts, currentProductPage);
            } else if (linkType === 'factura' && page > 0 && page <= totalPages) {
                currentFacturaPage = page;
                renderFacturas(allFacturas, currentFacturaPage);
            }
        });
    }

    // Cargar últimos clientes
    function loadRecentClients() {
        $.ajax({
            url: '/api/clientes?recent=true',
            method: 'GET',
            success: function (data) {
                allClients = data.map(cliente => ({
                    ...cliente,
                    facturaCount: cliente.facturaCount || 0
                }));
                renderClients(allClients, currentClientPage);
            },
            error: function (xhr) {
                console.error('Error loading recent clients:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudieron cargar los últimos clientes'
                });
            }
        });
    }

    // Cargar productos más vendidos
    function loadTopProducts() {
        $.ajax({
            url: '/api/productos/top',
            method: 'GET',
            success: function (data) {
                allProducts = data;
                renderProducts(allProducts, currentProductPage);
            },
            error: function (xhr) {
                console.error('Error loading top products:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudieron cargar los productos más vendidos'
                });
            }
        });
    }

    // Cargar facturas recientes
    function loadRecentFacturas() {
        $.ajax({
            url: '/api/facturas?recent=true',
            method: 'GET',
            success: function (data) {
                allFacturas = data;
                renderFacturas(allFacturas, currentFacturaPage);
            },
            error: function (xhr) {
                console.error('Error loading recent invoices:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseJSON?.error || 'No se pudieron cargar las facturas recientes'
                });
            }
        });
    }

    // Llamar las funciones al cargar el dashboard
    loadDashboardSummary();
    loadRecentClients();
    loadTopProducts();
    loadRecentFacturas();
});