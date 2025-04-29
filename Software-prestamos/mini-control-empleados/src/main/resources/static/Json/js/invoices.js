$(document).ready(function () {
    let clientesList = [];


// Navigation: Show facturas section on link or card click
    $('#facturas-link').click(function (e) {
        e.preventDefault();
        $('.content-section').hide();
        $('#facturas-content').show();
        loadFacturas();
    });

    $('.clickable-card[data-section="facturas"]').click(function () {
        $('.content-section').hide();
        $('#facturas-content').show();
        loadFacturas();
    });

    // Invoice search
    $('#buscar-facturas').on('keyup', function () {
        loadFacturas(1);
    });

    // Load invoices
    function loadFacturas(page = 1, itemsPerPage = 9) {
        $.ajax({
            url: '/api/facturas/listar',
            method: 'GET',
            success: function (data) {
                data.sort((a, b) => {
                    if (a.createdAt && b.createdAt) {
                        return new Date(b.createdAt) - new Date(a.createdAt);
                    }
                    return b.id - a.id;
                });

                const tbody = $('#facturas-table-body');
                tbody.empty();

                const searchTerm = $('#buscar-facturas').val().toLowerCase();
                const filteredData = data.filter(factura =>
                    factura.numero?.toLowerCase().includes(searchTerm) ||
                    factura.cliente?.nombre.toLowerCase().includes(searchTerm)
                );

                const totalItems = filteredData.length;
                const totalPages = Math.ceil(totalItems / itemsPerPage);
                const startIndex = (page - 1) * itemsPerPage;
                const endIndex = startIndex + itemsPerPage;
                const currentPage = Math.min(Math.max(1, page), totalPages);
                const paginatedData = filteredData.slice(startIndex, endIndex);

                paginatedData.forEach(function (factura) {
                    const clienteNombre = factura.cliente ? factura.cliente.nombre : 'N/A';
                    const fecha = factura.createdAt ? new Date(factura.createdAt).toLocaleDateString() : 'N/A';
                    const hasCliente = factura.cliente && factura.cliente.nombre;

                    let clienteData;
                    try {
                        clienteData = factura.cliente ? JSON.stringify(factura.cliente).replace(/'/g, "\\'") : '{}';
                    } catch (error) {
                        console.error("Error al convertir cliente a JSON:", error);
                        clienteData = '{}';
                    }

                    const cufe = factura.cufe || '';
                    const dianUrl = `https://catalogo-vpfe-hab.dian.gov.co/User/SearchDocument?DocumentKey=${encodeURIComponent(cufe)}`;

                    tbody.append(`
                        <tr>
                            <td>${factura.numero || 'N/A'}</td>
                            <td>${clienteNombre}</td>
                            <td>${factura.identificacion || 'N/A'}</td>
                            <td>${factura.pagos || 'N/A'}</td>
                            <td>${factura.metodopago || 'N/A'}</td>
                            <td>${factura.createdAt || 'N/A'}</td>
                            <td>${factura.status || 'N/A'}</td>
                            <td>${fecha}</td>
                            <td>$${factura.total?.toFixed(2) || '0.00'}</td>
                            <td>
                                <div class="dropdown position-static">
                                    <button class="btn btn-primary dropdown-toggle custom-menu-btn" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                        Menú
                                        <span class="double-arrow">▼</span>
                                    </button>
                                    <ul class="dropdown-menu custom-dropdown-menu" data-bs-popper="static">
                                        <li>
                                            <a class="dropdown-item edit-factura" href="#" data-id="${factura.id}">
                                                <i class="fas fa-edit"></i> Editar
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item view-factura" href="#" data-id="${factura.id}" data-numero="${factura.numero || 'N/A'}" data-cliente='${clienteData}' ${hasCliente ? '' : 'style="pointer-events: none; opacity: 0.5;" title="No hay cliente asociado"'}>
                                                <i class="fas fa-eye"></i> Detalles
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item download-factura" href="#" data-id="${factura.numero}">
                                                <i class="fas fa-download"></i> Pdf
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item validate-factura-btn" href="#" data-id="${factura.numero}">
                                                <i class="bi bi-check-circle"></i> Validar
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item download-xml-btn" href="#" data-id="${factura.numero}">
                                                <i class="bi bi-file-earmark-code"></i> XML
                                            </a>
                                        </li>
                                        <li>
                                            <a class="dropdown-item dian-redirect" href="#" data-url="${dianUrl}">
                                                <i class="fas fa-globe"></i> DIAN
                                            </a>
                                        </li>
                                        <li class="dropdown-divider"></li>
                                        <li>
                                            <a class="dropdown-item delete-factura-btn text-danger" href="#" data-id="${factura.referenceCode || 'N/A'}">
                                                <i class="bi bi-trash"></i> Eliminar
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    `);
                });

                if (paginatedData.length === 0) {
                    tbody.append(`
                        <tr>
                            <td colspan="10" class="text-center">
                                <div class="empty-state">
                                    <i class="fas fa-file-invoice-dollar"></i>
                                    <h4>No hay facturas</h4>
                                    <p>No se encontraron facturas que coincidan con la búsqueda.</p>
                                </div>
                            </td>
                        </tr>
                    `);
                }

                renderPagination(totalPages, currentPage, 'facturas');

                $('.edit-factura').click(function (e) {
                    e.preventDefault();
                    const id = $(this).data('id');
                    editFactura(id);
                });

                $(document).on('click', '.view-factura', function (e) {
                    e.preventDefault();
                    const id = $(this).data('id');
                    const numero = $(this).data('numero');
                    let cliente = null;

                    const clienteRaw = $(this).attr('data-cliente');
                    if (clienteRaw && clienteRaw !== '{}') {
                        try {
                            cliente = JSON.parse(clienteRaw);
                        } catch (error) {
                            console.error("Error al parsear cliente:", error);
                            cliente = null;
                        }
                    }

                    viewFactura(id, numero, cliente);
                });

                $('.download-factura').click(function (e) {
                    e.preventDefault();
                    const number = $(this).data('id');
                    showDownloadAnimation('PDF', () => descargarFactura(number));
                });

                $('.validate-factura-btn').click(function (e) {
                    e.preventDefault();
                    const number = $(this).data('id');
                    validateFactura(number);
                });

                $('.download-xml-btn').click(function (e) {
                    e.preventDefault();
                    const number = $(this).data('id');
                    showDownloadAnimation('XML', () => descargarXml(number));
                });

                $('.delete-factura-btn').click(function (e) {
                    e.preventDefault();
                    const referenceCode = $(this).data('id');
                    eliminarFactura(referenceCode);
                });

                $('.dian-redirect').click(function (e) {
                    e.preventDefault();
                    const url = $(this).data('url');

                    Swal.fire({
                        title: 'Código QR para DIAN',
                        html: `
                            <div style="text-align: center;">
                                <p>Escanea el código QR para verificar en la DIAN</p>
                                <img src="https://i.ibb.co/5x1HysZ9/image.png" alt="Código QR" style="width: 200px; height: 200px; margin: 10px 0;" />
                            </div>
                        `,
                        showCancelButton: true,
                        confirmButtonText: 'Ir a la DIAN',
                        cancelButtonText: 'Cancelar',
                        confirmButtonColor: '#3085d6',
                        cancelButtonColor: '#d33',
                        width: '400px',
                        didRender: () => {
                            const qrImage = Swal.getHtmlContainer().querySelector('img');
                            qrImage.style.display = 'block';
                            qrImage.style.margin = '0 auto';
                        }
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.open(url, '_blank');
                        }
                    });
                });
            },
            error: function (xhr) {
                $('#facturas-table-body').html('<tr><td colspan="10" class="text-center text-danger">Error al cargar facturas</td></tr>');
            }
        });
    }

    // Pagination rendering
    function renderPagination(totalPages, currentPage, section) {
        const paginationUl = $(`#${section}-pagination`);
        paginationUl.empty();

        if (totalPages <= 1) return;

        const maxPagesToShow = 3;
        let startPage = Math.max(1, currentPage - 1);
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if (endPage - startPage < maxPagesToShow - 1) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        paginationUl.append(`
            <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">
                    <i class="fas fa-chevron-left"></i>
                </a>
            </li>
        `);

        if (startPage > 1) {
            paginationUl.append(`
                <li class="page-item ${currentPage === 1 ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="1">1</a>
                </li>
            `);
            if (startPage > 2) {
                paginationUl.append(`
                    <li class="page-item disabled">
                        <a class="page-link" href="#">...</a>
                    </li>
                `);
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            paginationUl.append(`
                <li class="page-item ${currentPage === i ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>
            `);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                paginationUl.append(`
                    <li class="page-item disabled">
                        <a class="page-link" href="#">...</a>
                    </li>
                `);
            }
            paginationUl.append(`
                <li class="page-item ${currentPage === totalPages ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${totalPages}">${totalPages}</a>
                </li>
            `);
        }

        paginationUl.append(`
            <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">
                    <i class="fas fa-chevron-right"></i>
                </a>
            </li>
        `);

        $(`#${section}-pagination .page-link`).click(function (e) {
            e.preventDefault();
            const page = $(this).data('page');
            if (page && !$(this).parent().hasClass('disabled') && !$(this).parent().hasClass('active')) {
                if (section === 'facturas') {
                    loadFacturas(page);
                } else if (section === 'producto') {
                    loadProductosSeleccion(page);
                }
            }
        });
    }

    // Download animation
    function showDownloadAnimation(type, callback) {
        Swal.fire({
            title: `Generando ${type}...`,
            html: `
                <div class="download-animation">
                    <svg class="parachute-svg-circle" width="280px" height="124px" viewBox="0 0 280 124">
                        <circle cx="140" cy="62" r="30" stroke="#00FFFF" stroke-width="3px" stroke-linecap="round" stroke-dasharray="188.5" stroke-dashoffset="188.5" fill="none" class="parachute-circle"/>
                    </svg>
                    <svg class="parachute-svg-lines" width="32px" height="32px" viewBox="0 0 32 32">
                        <path d="M16 16 L16 26 M16 16 L12 26 M16 16 L20 26" stroke="#00FFFF" stroke-width="3px" fill="none" class="parachute-lines"/>
                    </svg>
                    <span class="progress-text">0%</span>
                </div>
            `,
            showConfirmButton: false,
            showCancelButton: false,
            allowOutsideClick: false,
            didOpen: () => {
                const circle = Swal.getHtmlContainer().querySelector('.parachute-circle');
                const lines = Swal.getHtmlContainer().querySelector('.parachute-lines');
                const progressText = Swal.getHtmlContainer().querySelector('.progress-text');

                gsap.set(circle, {strokeDashoffset: 188.5});
                gsap.set(lines, {opacity: 0, y: 0});
                progressText.textContent = '0%';

                gsap.to(circle, {
                    strokeDashoffset: 0,
                    duration: 2,
                    ease: 'power2.inOut',
                    onUpdate: function () {
                        const progress = Math.round((1 - this.ratio) * 100);
                        progressText.textContent = `${progress}%`;
                    },
                    onComplete: () => {
                        Swal.close();
                        callback();
                    }
                });

                gsap.to(lines, {
                    opacity: 1,
                    y: 5,
                    duration: 1,
                    delay: 1,
                    ease: 'power2.out'
                });
            }
        });
    }

    // View invoice
    function viewFactura(id, facturaNumero, cliente) {
        Swal.fire({
            title: 'Cargando detalles',
            text: 'Espere por favor...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        $.ajax({
            url: `/api/facturas/ver/${id}`,
            method: 'GET',
            success: function (data) {
                Swal.close();

                if (data.error) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: data.error,
                        confirmButtonColor: '#3085d6'
                    });
                    return;
                }

                data.numero = facturaNumero !== 'N/A' ? facturaNumero : id;
                data.cliente = cliente && typeof cliente === 'object' && cliente.nombre ? cliente : null;

                let currentDate = new Date().toLocaleDateString('es-ES');
                let modalContent = `
                    <div class="invoice-container">
                        <div class="invoice-header">
                            <div class="row align-items-center mb-4">
                                <div class="col-md-6">
                                    <div class="company-logo">
                                        <h2 class="mb-1">REREFENCIA</h2>
                                        <div class="text-muted">N FROM ° ${data.referenceCode}</div>
                                    </div>
                                </div>
                                <div class="col-md-6 text-md-end">
                                    <div class="invoice-date">
                                        <div class="date-label">Fecha de emisión:</div>
                                        <div class="date-value">${data.fecha || currentDate}</div>
                                    </div>
                                </div>
                            </div>
                            <hr class="separator">
                        </div>
                        <div class="row invoice-info mb-4">
                            <div class="col-md-6">
                                <div class="card shadow-sm h-100">
                                    <div class="card-header bg-primary text-white">
                                        <h5 class="mb-0">Cliente</h5>
                                    </div>
                                    <div class="card-body">
                                        ${data.cliente ? `
                                            <div class="client-details">
                                                <p class="mb-1"><strong>${data.cliente.nombre || 'N/A'}</strong></p>
                                                <p class="mb-1"><i class="fas fa-id-card me-2"></i>${data.cliente.ids || data.cliente.identificacion || 'N/A'}</p>
                                                <p class="mb-1"><i class="fas fa-envelope me-2"></i>${data.cliente.email || data.cliente.correo || 'N/A'}</p>
                                                <p class="mb-0"><i class="fas fa-map-marker-alt me-2"></i>${data.cliente.address || data.cliente.direccion || 'N/A'}</p>
                                            </div>
                                        ` : 'No hay información del cliente disponible'}
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="card shadow-sm h-100">
                                    <div class="card-header bg-primary text-white">
                                        <h5 class="mb-0">Detalles de Factura</h5>
                                    </div>
                                    <div class="card-body">
                                        <p class="mb-1"><strong>Factura:</strong> #${data.numero}</p>
                                        <p class="mb-1"><strong>Fecha:</strong> ${data.createdAt || currentDate}</p>
                                        <p class="mb-1"><strong>Estado:</strong> <span class="badge bg-success">${data.status || 'Emitida'}</span></p>
                                        <p class="mb-0"><strong>Método de pago:</strong> ${data.metodoPago || 'N/A'}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="items-section mb-4">
                            <div class="card shadow-sm">
                                <div class="card-header bg-primary text-white">
                                    <h5 class="mb-0">Detalle de Productos</h5>
                                </div>
                                <div class="card-body p-0">
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover mb-0">
                                            <thead class="table-light">
                                                <tr>
                                                    <th class="text-start">Producto</th>
                                                    <th class="text-center">Cantidad</th>
                                                    <th class="text-end">Precio</th>
                                                    <th class="text-end">IVA</th>
                                                    <th class="text-end">Dto %</th>
                                                    <th class="text-end">Subtotal</th>
                                                    <th class="text-end">Total</th>
                                                </tr>
                                            </thead>
                                            <tbody>`;

                if (data.items && data.items.length > 0) {
                    data.items.forEach(item => {
                        modalContent += `
                            <tr>
                                <td class="text-start">${item.producto || 'N/A'}</td>
                                <td class="text-center">${item.cantidad || '0'}</td>
                                <td class="text-end">$${item.precio ? item.precio.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0'}</td>
                                <td class="text-end">$${item.iva ? item.iva.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0'}</td>
                                <td class="text-end">${item.porcentajeDescuento ? item.porcentajeDescuento.toFixed(1) : '0.0'}%</td>
                                <td class="text-end">$${item.subtotal ? item.subtotal.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0'}</td>
                                <td class="text-end">$${item.total ? item.total.toFixed(0).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0'}</td>
                            </tr>`;
                    });
                } else {
                    modalContent += `<tr><td colspan="7" class="text-center">No hay items disponibles</td></tr>`;
                }

                modalContent += `
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-5 ms-auto">
                                <div class="card shadow-sm">
                                    <div class="card-header bg-primary text-white">
                                        <h5 class="mb-0">Resumen</h5>
                                    </div>
                                    <div class="card-body p-0">
                                        <table class="table table-sm mb-0">
                                            <tbody>`;

                if (data.totals) {
                    modalContent += `
                                                <tr>
                                                    <td class="text-start"><strong>Subtotal</strong></td>
                                                    <td class="text-end">$${data.totals.grossTotal ? data.totals.grossTotal.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0.00'}</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-start"><strong>Descuentos</strong></td>
                                                    <td class="text-end">$${data.totals.discounts ? data.totals.discounts.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0.00'}</td>
                                                </tr>
                                                <tr>
                                                    <td class="text-start"><strong>Impuestos</strong></td>
                                                    <td class="text-end">$${data.totals.taxes ? data.totals.taxes.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0.00'}</td>
                                                </tr>
                                                <tr class="table-active">
                                                    <td class="text-start"><strong>Total</strong></td>
                                                    <td class="text-end"><strong>$${data.totals.total ? data.totals.total.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") : '0.00'}</strong></td>
                                                </tr>`;
                }

                modalContent += `
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="invoice-footer mt-4 text-center">
                            <div class="text-muted small">Este documento es una representación digital de su factura</div>
                        </div>
                    </div>`;

                Swal.fire({
                    title: `Factura #${data.numero}`,
                    html: modalContent,
                    width: '750px',
                    customClass: {
                        container: 'swal-invoice-modal'
                    },
                    showCloseButton: true,
                    showConfirmButton: true,
                    confirmButtonText: 'Cerrar',
                    confirmButtonColor: '#3085d6',
                    footer: `<button id="download-btn-modal" class="btn btn-secondary" data-id="${data.numero}"><i class="fas fa-download"></i> Descargar PDF</button>`,
                    didOpen: () => {
                        $('#download-btn-modal').off('click').on('click', function () {
                            const number = $(this).data('id');
                            descargarFactura(number);
                        });
                    }
                });
            },
            error: function (xhr) {
                Swal.close();
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudieron cargar los detalles de la factura',
                    confirmButtonColor: '#3085d6'
                });
            }
        });
    }

    // Download PDF
    function descargarFactura(number) {
        Swal.fire({
            title: 'Generando PDF',
            text: 'Espere por favor...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        fetch(`/api/facturas/factura-download/${number}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(errorData => {
                        throw new Error(errorData.error || 'Error al descargar el PDF');
                    });
                }
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `factura-${number}.pdf`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
                Swal.close();
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: error.message || 'No se pudo descargar el PDF. Intente nuevamente.',
                    confirmButtonColor: '#3085d6'
                });
            });
    }

    // Download XML
    function descargarXml(number) {
        if (number === 'N/A' || !number) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se encontró el número de la factura',
                confirmButtonColor: '#3085d6'
            });
            return;
        }

        Swal.fire({
            title: 'Cargando XML',
            text: 'Espere por favor...',
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            }
        });

        fetch(`/api/facturas/descargar-xml/${number}`, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Estado ${response.status}`);
                }
                return response.blob();
            })
            .then(blob => {
                return blob.text();
            })
            .then(xmlText => {
                Swal.close();
                Swal.fire({
                    title: `Vista Previa del XML - Factura ${number}`,
                    html: `<pre style="text-align: left; white-space: pre-wrap; word-wrap: break-word; max-height: 400px; overflow-y: auto;">${xmlText}</pre>`,
                    width: '800px',
                    showConfirmButton: true,
                    confirmButtonText: 'Cerrar',
                    confirmButtonColor: '#3085d6',
                    showCancelButton: true,
                    cancelButtonText: 'Descargar',
                    cancelButtonColor: '#28a745',
                    didOpen: () => {
                    }
                }).then((result) => {
                    if (result.dismiss === Swal.DismissReason.cancel) {
                        const blob = new Blob([xmlText], {type: 'application/xml'});
                        const url = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        a.href = url;
                        a.download = `factura-${number}.xml`;
                        document.body.appendChild(a);
                        a.click();
                        setTimeout(() => {
                            window.URL.revokeObjectURL(url);
                            document.body.removeChild(a);
                        }, 100);
                    }
                });
            })
            .catch(error => {
                Swal.close();
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo cargar el XML: ' + error.message,
                    confirmButtonColor: '#3085d6'
                });
            });
    }

    // Delete invoice
    function eliminarFactura(referenceCode) {
        if (referenceCode === 'N/A' || !referenceCode) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se encontró el referenceCode de la factura',
                confirmButtonColor: '#3085d6'
            });
            return;
        }

        Swal.fire({
            title: '¿Está seguro?',
            text: "Esta acción eliminará la factura permanentemente",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire({
                    title: 'Eliminando',
                    text: 'Espere por favor...',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                $.ajax({
                    url: `/api/facturas/eliminar/${referenceCode}`,
                    method: 'DELETE',
                    success: function () {
                        Swal.fire({
                            icon: 'success',
                            title: 'Éxito',
                            text: 'Factura eliminada correctamente',
                            confirmButtonColor: '#28a745'
                        }).then(() => {
                            loadFacturas();
                        });
                    },
                    error: function (xhr) {
                        Swal.close();
                        let errorMessage = xhr.responseJSON?.error || `Error ${xhr.status}: ${xhr.statusText}`;
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: errorMessage,
                            confirmButtonColor: '#3085d6'
                        });
                    }
                });
            }
        });
    }

    // Validate invoice
    function validateFactura(number) {
        if (number === 'N/A' || !number) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se encontró el número de la factura',
                confirmButtonColor: '#3085d6'
            });
            return;
        }

        Swal.fire({
            title: '¿Está seguro?',
            text: "Va a validar esta factura en Factus",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#28a745',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, validar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire({
                    title: 'Validando',
                    text: 'Espere por favor...',
                    allowOutsideClick: false,
                    didOpen: () => {
                        Swal.showLoading();
                    }
                });

                $.ajax({
                    url: `/api/facturas/validar-y-descargar/${number}`,
                    method: 'GET',
                    xhrFields: {
                        responseType: 'blob'
                    },
                    success: function (data, status, xhr) {
                        Swal.close();
                        const contentType = xhr.getResponseHeader('Content-Type');
                        if (contentType.includes('application/pdf')) {
                            const blob = new Blob([data], {type: 'application/pdf'});
                            const url = window.URL.createObjectURL(blob);
                            Swal.fire({
                                title: `Vista Previa del PDF - Factura ${number}`,
                                html: `
                                    <iframe src="${url}" width="100%" height="500px" style="border: none;"></iframe>
                                `,
                                width: '90%',
                                showConfirmButton: true,
                                confirmButtonText: 'Cerrar',
                                confirmButtonColor: '#3085d6',
                                showCancelButton: true,
                                cancelButtonText: 'Descargar',
                                cancelButtonColor: '#28a745',
                                didClose: () => {
                                    window.URL.revokeObjectURL(url);
                                }
                            }).then((result) => {
                                if (result.dismiss === Swal.DismissReason.cancel) {
                                    const a = document.createElement('a');
                                    a.style.display = 'none';
                                    a.href = url;
                                    a.download = `factura-${number}.pdf`;
                                    document.body.appendChild(a);
                                    a.click();
                                    document.body.removeChild(a);
                                }
                            });
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: 'Error',
                                text: 'Respuesta inesperada del servidor: Content-Type no es application/pdf',
                                confirmButtonColor: '#3085d6'
                            });
                        }
                    },
                    error: function (xhr) {
                        Swal.close();
                        let errorMessage = xhr.responseJSON?.error || `Error ${xhr.status}: ${xhr.statusText}`;
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: 'No se pudo validar la factura: ' + errorMessage,
                            confirmButtonColor: '#3085d6'
                        });
                    }
                });
            }
        });
    }

    // Load clients for invoice
    function loadClientesFactura(callback) {
        $.ajax({
            url: '/api/clientes',
            method: 'GET',
            success: function (clientes) {
                clientesList = clientes;
                const select = $('#factura-cliente');
                select.empty();
                select.append('<option value="">Buscar cliente por nombre o identificación...</option>');

                clientes.forEach(function (cliente) {
                    select.append(
                        `<option value="${cliente.id}" data-identificacion="${cliente.identificacion}">
                            ${cliente.nombre} (${cliente.identificacion})
                        </option>`
                    );
                });

                select.select2({
                    placeholder: "Buscar cliente por nombre o identificación...",
                    allowClear: true,
                    width: '100%',
                    minimumInputLength: 0,
                    dropdownParent: $('#facturaModal'),
                    templateResult: formatCliente,
                    templateSelection: formatClienteSelection,
                    matcher: customMatcher
                });

                if (typeof callback === 'function') {
                    callback();
                }
            },
            error: function (xhr) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo cargar la lista de clientes'
                });
                if (typeof callback === 'function') {
                    callback();
                }
            }
        });
    }

    function formatCliente(cliente) {
        if (!cliente.id) return cliente.text;
        return $(`<span>${cliente.text}</span>`);
    }

    function formatClienteSelection(cliente) {
        if (!cliente.id) return cliente.text;
        return cliente.text;
    }

    function customMatcher(params, data) {
        if ($.trim(params.term) === '') {
            return data;
        }

        const term = params.term.toLowerCase();
        const nombre = data.text.toLowerCase();
        const identificacion = $(data.element).data('identificacion')?.toString().toLowerCase() || '';

        if (nombre.indexOf(term) > -1 || identificacion.indexOf(term) > -1) {
            return data;
        }

        return null;
    }

    function onClienteSelected() {
        const clienteId = $('#factura-cliente').val();

        if (!clienteId) {
            clearClienteInfo();
            $('#factura-municipio-id').val('');
            return;
        }

        const clienteSeleccionado = clientesList.find(c => c.id == clienteId);

        if (clienteSeleccionado) {
            $('#cliente-nombres').text(clienteSeleccionado.nombre || '-');
            $('#cliente-tipos').text(clienteSeleccionado.tipoCliente || '-');
            $('#cliente-correos').text(clienteSeleccionado.correo || '-');
            $('#cliente-telefonos').text(clienteSeleccionado.telefono || '-');
            $('#cliente-direcciones').text(clienteSeleccionado.direccion || '-');
            $('#cliente-municipios').text(clienteSeleccionado.municipio || '-');
            $('#factura-identification').val(clienteSeleccionado.identificacion || '');
            $('#factura-api-client-name').val(clienteSeleccionado.nombre || '');
            $('#factura-graphic-representation').val('PDF');
            $('#factura-municipio-id').val(clienteSeleccionado.municipioId || '');
            generateAutoFields();
        } else {
            clearClienteInfo();
            $('#factura-municipio-id').val('');
        }
    }

    function clearClienteInfo() {
        $('#cliente-nombres, #cliente-tipos, #cliente-correos, #cliente-telefonos, #cliente-direcciones, #cliente-municipios').text('-');
        $('#factura-identification, #factura-api-client-name, #factura-graphic-representation').val('');
    }

// Generate automatic fields (reference code, numbering range)
    function generateAutoFields() {
        const referenceCode = "REF-" + Date.now() + "-" + Math.floor(Math.random() * 1000);
        $('#factura-reference-code').val(referenceCode);
        const numberingRangeId = Math.floor(Math.random() * 10000) + 1;
        $('#factura-numbering-range-id').val(numberingRangeId);
        const formaPago = $('#factura-forma-pago').val();
        const today = new Date();
        let dueDate = today;
        if (formaPago === 'Crédito') {
            dueDate = new Date(today.setDate(today.getDate() + 30));
        }
        const yyyy = dueDate.getFullYear();
        let mm = dueDate.getMonth() + 1;
        let dd = dueDate.getDate();
        if (dd < 10) dd = '0' + dd;
        if (mm < 10) mm = '0' + mm;
        $('#factura-due-date').val(`${yyyy}-${mm}-${dd}`);
    }

    // Initialize invoice modal
// Initialize invoice modal
    $('#facturaModal').on('show.bs.modal', function (e) {
        if (!$(e.relatedTarget).hasClass('edit-factura')) {
            $('#facturaModalLabel').text('Nueva Factura');
            $('#factura-form').trigger('reset');
            $('#factura-id').val('');
            $('#factura-items').empty();
            facturaItems = [];
            itemCount = 0;
            updateFacturaTotals();
            loadClientesFactura(function () {
                $('#factura-cliente').off('change').on('change', onClienteSelected);
            });
            const today = new Date();
            const yyyy = today.getFullYear();
            let mm = today.getMonth() + 1;
            let dd = today.getDate();
            if (dd < 10) dd = '0' + dd;
            if (mm < 10) mm = '0' + mm;
            $('#factura-fecha').val(`${yyyy}-${mm}-${dd}`);
        }
    });

    $(document).on('click', '.seleccionar-producto', function () {
        const id = $(this).data('id');
        const name = $(this).data('name');
        const price = $(this).data('price');
        const taxRate = $(this).data('tax-rate');
        console.log('Producto seleccionado:', {id, name, price, taxRate});
        if (!id || id === undefined || !name || price == null || taxRate == null || isNaN(taxRate)) {
            console.error('Datos inválidos en selección:', {id, name, price, taxRate});
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Datos del producto inválidos. Verifique ID y tasa de IVA.'
            });
            return;
        }
        addItemToFactura(id, name, parseFloat(price), parseFloat(taxRate));
        $('#seleccionarProductoModal').modal('hide');
        $('#facturaModal').modal('show');
    });


    function addItemToFactura(id, name, price, taxRate) {
        const item = {
            id: id,
            name: name,
            price: parseFloat(price),
            quantity: 1,
            taxRate: parseFloat(taxRate),
            discount: 0
        };
        itemCount++;
        facturaItems.push(item);
        const subtotal = item.price * item.quantity;
        const discount = subtotal * (item.discount / 100);
        const taxValue = (subtotal - discount) * (item.taxRate / 100);
        const total = subtotal - discount + taxValue;
        $('#factura-items').append(`
      <tr id="item-${itemCount}">
        <td>${item.name}</td>
        <td><input type="number" class="form-control item-quantity" data-index="${facturaItems.length - 1}" value="${item.quantity}" min="1"></td>
        <td>$${item.price.toFixed(2)}</td>
        <td><input type="number" class="form-control item-discount" data-index="${facturaItems.length - 1}" value="${item.discount}" min="0" max="100"></td>
        <td class="item-subtotal">$${subtotal.toFixed(2)}</td>
        <td class="item-tax">$${taxValue.toFixed(2)}</td>
        <td class="item-total">$${total.toFixed(2)}</td>
        <td><button class="btn btn-sm btn-danger remove-item" data-index="${facturaItems.length - 1}"><i class="fas fa-trash"></i></button></td>
      </tr>
    `);
        updateFacturaTotals();
        $('.item-quantity, .item-discount').off('change').on('change', function () {
            const index = $(this).data('index');
            updateItemCalculations(index);
        });
        $('.remove-item').off('click').on('click', function () {
            const index = $(this).data('index');
            removeFacturaItem(index);
        });
    }

    function updateItemCalculations(index) {
        const item = facturaItems[index];
        item.quantity = parseFloat($(`.item-quantity[data-index="${index}"]`).val()) || 1;
        item.discount = parseFloat($(`.item-discount[data-index="${index}"]`).val()) || 0;
        const subtotal = item.price * item.quantity;
        const discount = subtotal * (item.discount / 100);
        const taxValue = (subtotal - discount) * (item.taxRate / 100);
        const total = subtotal - discount + taxValue;
        const row = $(`#factura-items tr:eq(${index})`);
        row.find('.item-subtotal').text(`$${subtotal.toFixed(2)}`);
        row.find('.item-tax').text(`$${taxValue.toFixed(2)}`);
        row.find('.item-total').text(`$${total.toFixed(2)}`);
        updateFacturaTotals();
    }

    function removeFacturaItem(index) {
        facturaItems.splice(index, 1);
        $(`#factura-items tr:eq(${index})`).remove();
        $('#factura-items tr').each(function (i) {
            $(this).find('.item-quantity').data('index', i);
            $(this).find('.item-discount').data('index', i);
            $(this).find('.remove-item').data('index', i);
        });
        updateFacturaTotals();
    }

    function updateFacturaTotals() {
        let subtotal = 0;
        let totalIva = 0;
        let totalDescuento = 0;
        let total = 0;
        facturaItems.forEach(function (item) {
            const itemSubtotal = item.price * item.quantity;
            const itemDiscount = itemSubtotal * (item.discount / 100);
            const itemTax = (itemSubtotal - itemDiscount) * (item.taxRate / 100);
            subtotal += itemSubtotal;
            totalDescuento += itemDiscount;
            totalIva += itemTax;
            total += itemSubtotal - itemDiscount + itemTax;
        });
        $('#factura-subtotal').text(`$${subtotal.toFixed(2)}`);
        $('#factura-iva-total').text(`$${totalIva.toFixed(2)}`);
        $('#factura-descuento-total').text(`$${totalDescuento.toFixed(2)}`);
        $('#factura-total').text(`$${total.toFixed(2)}`);
    }

    function renderFacturaItems() {
        const itemsContainer = $('#factura-items');
        console.log('Rendering facturaItems:', facturaItems); // Debug
        if (!itemsContainer.length) {
            console.error('Selector #factura-items no encontrado');
            return;
        }
        itemsContainer.empty();

        facturaItems.forEach(item => {
            itemsContainer.append(`
            <tr data-id="${item.id}">
                <td>${item.producto || 'N/A'}</td>
                <td>
                    <input type="number" class="form-control item-cantidad" value="${item.cantidad || 1}" min="1" style="width: 80px;">
                </td>
                <td>${formatCurrency(item.precio || 0)}</td>
                <td>
                    <input type="number" class="form-control item-descuento" value="${item.porcentajeDescuento || 0}" min="0" max="100"  style="width: 80px;">
                </td>
                <td>${formatCurrency(item.subtotal || 0)}</td>
                <td>${formatCurrency(item.iva || 0)}</td>
                <td>${formatCurrency(item.total || 0)}</td>
                <td>
                    <button class="btn btn-sm btn-danger remove-item" data-id="${item.id}">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `);
        });
    }


// Save invoice
    $('#guardar-factura').click(function () {
        if (facturaItems.length === 0) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Debe agregar al menos un producto a la factura'
            });
            return;
        }
        const clienteId = $('#factura-cliente').val();
        if (!clienteId) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Debe seleccionar un cliente'
            });
            return;
        }
        const clienteSeleccionado = clientesList.find(c => c.id == clienteId);
        const id = $('#factura-id').val();
        const totalIva = parseFloat($('#factura-iva-total').text().replace('$', ''));
        const tributes = totalIva > 0 ? [{tribute_id: "01", rate: 19.0, amount: totalIva}] : [];
        const factura = {
            cliente: {
                id: clienteSeleccionado.id,
                nombre: clienteSeleccionado.nombre,
                correo: clienteSeleccionado.correo,
                direccion: clienteSeleccionado.direccion,
                telefono: clienteSeleccionado.telefono || '',
                tipoCliente: clienteSeleccionado.tipoCliente,
                identificacion: clienteSeleccionado.identificacion,
                municipio: clienteSeleccionado.municipio,
                municipioId: parseInt(clienteSeleccionado.municipioId) || null
            },
            formaPago: $('#factura-forma-pago').val(),
            metodo: $('#factura-metodo-pago').val(),
            createdAt: new Date().toISOString(),
            numberingRangeId: parseInt($('#factura-numbering-range-id').val()) || 128,
            referenceCode: $('#factura-reference-code').val(),
            subtotal: parseFloat($('#factura-subtotal').text().replace('$', '')),
            totalIva: totalIva,
            totalDescuento: parseFloat($('#factura-descuento-total').text().replace('$', '')),
            total: parseFloat($('#factura-total').text().replace('$', '')),
            municipio: parseInt($('#factura-municipio-id').val()) || null,
            fechaVencimiento: $('#factura-due-date').val() || "2025-05-01",
            linesCount: facturaItems.length,
            tributes: tributes,
            items: facturaItems.map(item => ({
                producto: {
                    id: item.id,
                    name: item.name,
                    price: item.price,
                    taxRate: item.taxRate,
                    unitMeasureId: "70",
                    standardCodeId: "1",
                    excluded: false
                },
                cantidad: item.quantity,
                precio: item.price,
                porcentajeDescuento: item.discount,
                subtotal: item.price * item.quantity,
                iva: (item.price * item.quantity * (item.taxRate / 100)),
                total: (item.price * item.quantity) - (item.price * item.quantity * (item.discount / 100)) + (item.price * item.quantity * (item.taxRate / 100))
            }))
        };
        const url = id ? `/api/facturas/${id}` : '/api/facturas/crear';
        const method = id ? 'PUT' : 'POST';
        $.ajax({
            url: url,
            method: method,
            data: JSON.stringify(factura),
            contentType: 'application/json',
            success: function (response) {
                $('#facturaModal').modal('hide');
                loadFacturas();
                Swal.fire({
                    toast: true,
                    position: 'top-end',
                    icon: 'success',
                    title: 'Éxito',
                    text: id ? 'Factura actualizada con éxito' : `Factura creada con código ${factura.referenceCode}`,
                    timer: 3000,
                    showConfirmButton: false
                });
            },
            error: function (xhr) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: `Error al ${id ? 'actualizar' : 'crear'} factura: ${xhr.responseJSON?.error || 'Desconocido'}`
                });
            }
        });
    });


    // Edit invoice
    function editFactura(id) {
        $.ajax({
            url: `/api/facturas/ver/${id}`,
            method: 'GET',
            success: function (factura) {
                $('#facturaModalLabel').text('Editar Factura');
                $('#factura-id').val(factura.id);
                $('#factura-numero').text(factura.numero);
                $('#factura-reference-code').val(factura.referenceCode);
                $('#factura-reference-code-display').text(factura.referenceCode);
                $('#factura-numbering-range-id').val(factura.numberingRangeId);
                $('#factura-fecha').val(factura.fecha);
                $('#factura-due-date').val(factura.dueDate);
                $('#factura-forma-pago').val(factura.formaPago);
                $('#factura-metodo-pago').val(factura.metodoPago);
                $('#factura-identification').val(factura.identificacion);
                $('#factura-api-client-name').val(factura.apiClientName);
                $('#factura-graphic-representation').val(factura.graphicRepresentation);
                $('#factura-municipio-id').val(factura.municipioId);

                loadClientesFactura(function () {
                    $('#factura-cliente').val(factura.clienteId).trigger('change');
                    $('#factura-cliente').off('change').on('change', function () {
                        onClienteSelected();
                    });
                });

                facturaItems = factura.items.map((item, index) => {
                    const producto = productosList.find(p => p.id === item.productoId) || {
                        name: item.producto || 'Desconocido',
                        price: item.precio,
                        taxRate: item.taxRate
                    };
                    return {
                        id: index,
                        productoId: item.productoId,
                        producto: producto.name,
                        cantidad: item.cantidad,
                        precio: parseFloat(item.precio),
                        taxRate: parseFloat(item.taxRate),
                        porcentajeDescuento: parseFloat(item.porcentajeDescuento || 0),
                        iva: (item.cantidad * item.precio * (item.taxRate / 100)),
                        subtotal: item.cantidad * item.precio,
                        total: (item.cantidad * item.precio * (1 - (item.porcentajeDescuento || 0) / 100)) + (item.cantidad * item.precio * (item.taxRate / 100))
                    };
                });
                itemCount = facturaItems.length;

                renderFacturaItems();
                updateFacturaTotals();
                $('#facturaModal').modal('show');
            },
            error: function (xhr) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo cargar la factura: ' + (xhr.responseJSON?.message || 'Desconocido')
                });
            }
        });
    }
});

// Variables globales para productos y paginación
let productosList = [];
let currentPage = 1;
const itemsPerPage = 5;

// Variables globales para facturas
let facturaItems = [];
let itemCount = 0;


function formatCurrency(value) {
    return '$' + parseFloat(value || 0).toFixed(2);
}

// Cargar productos con paginación y filtros
function loadProductosSeleccion(page = 1) {
    $.ajax({
        url: '/api/productos',
        method: 'GET',
        success: function (data) {
            productosList = data.sort((a, b) => b.id - a.id); // Ordenar por ID descendente
            currentPage = page;
            applyFiltersAndRender(page);
        },
        error: function () {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron cargar los productos'
            });
        }
    });
}

// Aplicar filtros y renderizar productos
function applyFiltersAndRender(page) {
    const searchTerm = $('#buscar-producto').val().toLowerCase();

    // Filtrar productos por nombre o código
    let filteredProductos = productosList.filter(p =>
        p.id.toString().includes(searchTerm) ||
        p.name.toLowerCase().includes(searchTerm)
    );

    // Calcular paginación
    const totalItems = filteredProductos.length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const startIndex = (page - 1) * itemsPerPage;
    const paginatedProductos = filteredProductos.slice(startIndex, startIndex + itemsPerPage);

    // Renderizar tabla
    const tbody = $('#seleccionar-producto-body');
    tbody.empty();

    paginatedProductos.forEach(producto => {
        tbody.append(`
            <tr>
                <td>${producto.id}</td>
                <td>${producto.name}</td>
                <td>${formatCurrency(producto.price)}</td>
                <td>${producto.taxRate || 0}%</td>
                <td>
                    <button class="btn btn-sm btn-primary seleccionar-producto"
                            data-id="${producto.id}"
                            data-name="${producto.name}"
                            data-price="${producto.price}"
                            data-tax-rate="${producto.taxRate || 0}">
                        <i class="fas fa-check"></i>
                    </button>
                </td>
            </tr>
        `);
    });

    // Mostrar mensaje si no hay productos
    if (paginatedProductos.length === 0) {
        tbody.append(`
            <tr>
                <td colspan="5" class="text-center text-muted">
                    <i class="fas fa-box-open"></i>
                    <p>No se encontraron productos</p>
                </td>
            </tr>
        `);
    }

    // Actualizar contador
    $('#productos-total').text(`${totalItems} productos`);

    // Renderizar paginación
    renderPaginations(totalPages, page);
}


// Renderizar paginación
function renderPaginations(totalPages, currentPage) {
    const pagination = $('#producto-pagination');
    pagination.empty();

    if (totalPages <= 1) return;

    // Botón anterior
    pagination.append(`
        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${currentPage - 1}"><i class="fas fa-chevron-left"></i></a>
        </li>
    `);

    // Páginas
    for (let i = 1; i <= totalPages; i++) {
        pagination.append(`
            <li class="page-item ${i === currentPage ? 'active' : ''}">
                <a class="page-link" href="#" data-page="${i}">${i}</a>
            </li>
        `);
    }

    // Botón siguiente
    pagination.append(`
        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" data-page="${currentPage + 1}"><i class="fas fa-chevron-right"></i></a>
        </li>
    `);

    // Evento de cambio de página
    pagination.find('.page-link').click(function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page && !$(this).parent().hasClass('disabled')) {
            currentPage = page;
            applyFiltersAndRender(page);
        }
    });
}

// Evento de búsqueda
$('#buscar-producto').on('input', function () {
    currentPage = 1;
    applyFiltersAndRender(currentPage);
});

// Abrir modal de productos
$('#agregar-item').click(function () {
    currentPage = 1;
    loadProductosSeleccion(currentPage);
    $('#seleccionarProductoModal').modal('show');
});


