$(document).ready(function () {
    let municipiosData = [];

    // Navigation: Show clientes section on link or card click
    $('#clientes-link').click(function (e) {
        e.preventDefault();
        $('.content-section').hide();
        $('#clientes-content').show();
        loadClientes(); // Load client table
    });

    $('.clickable-card[data-section="clientes"]').click(function () {
        $('.content-section').hide();
        $('#clientes-content').show();
        loadClientes(); // Load client table

    });

    // Load municipalities
    async function loadMunicipios(searchTerm = '') {
        try {
            const response = await fetch('/Json/municipios.json');
            if (!response.ok) throw new Error('Error al cargar municipios');
            const data = await response.json();
            municipiosData = data.data;
            const datalist = document.getElementById('municipios-list');
            datalist.innerHTML = '';

            const filteredMunicipios = searchTerm
                ? municipiosData.filter(m =>
                    m.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                    m.department.toLowerCase().includes(searchTerm.toLowerCase())
                )
                : municipiosData;

            filteredMunicipios.slice(0, 50).forEach(m => {
                const option = document.createElement('option');
                option.value = `${m.name} (${m.department})`;
                option.dataset.id = m.id;
                datalist.appendChild(option);
            });

            if (filteredMunicipios.length === 0) {
                const option = document.createElement('option');
                option.value = 'No se encontraron municipios';
                option.disabled = true;
                datalist.appendChild(option);
            }
        } catch (error) {
            console.error('Error:', error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No se pudieron cargar los municipios'
            });
        }
    }

    // Handle municipality input
    const municipioInput = document.getElementById('cliente-municipio');
    const municipioIdInput = document.getElementById('cliente-municipio-id');
    municipioInput.addEventListener('input', function () {
        const searchTerm = this.value;
        loadMunicipios(searchTerm);

        const selectedOption = Array.from(document.getElementById('municipios-list').options).find(
            option => option.value === this.value && option.dataset.id
        );
        municipioIdInput.value = selectedOption ? selectedOption.dataset.id : '';
    });

    // Client modal handling
    document.getElementById('clienteModal').addEventListener('show.bs.modal', async function (e) {
        await loadMunicipios();
        const form = document.getElementById('cliente-form');
        const municipioInput = document.getElementById('cliente-municipio');
        const municipioIdInput = document.getElementById('cliente-municipio-id');

        if (!e.relatedTarget.classList.contains('edit-cliente')) {
            document.getElementById('clienteModalLabel').textContent = 'Nuevo Cliente';
            form.reset();
            document.getElementById('cliente-id').value = '';
            document.getElementById('campos-juridica').style.display = 'none';
            municipioInput.value = '';
            municipioIdInput.value = '';
        } else {
            const id = e.relatedTarget.dataset.id;
            try {
                const response = await fetch(`/api/clientes/${id}`);
                if (!response.ok) throw new Error('Error al cargar cliente');
                const cliente = await response.json();
                if (cliente.municipio) {
                    municipioInput.value = `${cliente.municipio.name} (${cliente.municipio.department})`;
                    municipioIdInput.value = cliente.municipio.id;
                    const datalist = document.getElementById('municipios-list');
                    datalist.innerHTML = '';
                    const option = document.createElement('option');
                    option.value = municipioInput.value;
                    option.dataset.id = cliente.municipio.id;
                    datalist.appendChild(option);
                }
            } catch (error) {
                console.error('Error al cargar cliente:', error);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudo cargar el municipio del cliente'
                });
            }
        }
    });

    // Toggle juridical fields
    document.getElementById('cliente-tipo').addEventListener('change', function () {
        const camposJuridica = document.getElementById('campos-juridica');
        if (this.value === 'Persona Jurídica') {
            camposJuridica.style.display = 'block';
            document.getElementById('cliente-tipo-identificacion').value = 'NIT';
        } else {
            camposJuridica.style.display = 'none';
        }
    });

    // Client search
    $('#buscar-clientes').on('keyup', function () {
        loadClientes(1);
    });

    // Load clients
    function loadClientes(page = 1, itemsPerPage = 9) {
        $.ajax({
            url: '/api/clientes',
            method: 'GET',
            success: function (data) {
                // Ordenar clientes por ID descendente
                data.sort((a, b) => b.id - a.id);
                const tbody = $('#clientes-table-body');
                tbody.empty();

                const searchTerm = $('#buscar-clientes').val().toLowerCase();
                const filteredData = data.filter(cliente =>
                    cliente.id.toString().includes(searchTerm) ||
                    cliente.nombre.toLowerCase().includes(searchTerm) ||
                    cliente.identificacion.toLowerCase().includes(searchTerm)
                );

                const totalItems = filteredData.length;
                const totalPages = Math.ceil(totalItems / itemsPerPage);
                const startIndex = (page - 1) * itemsPerPage;
                const endIndex = startIndex + itemsPerPage;
                const currentPage = Math.min(Math.max(1, page), totalPages);
                const paginatedData = filteredData.slice(startIndex, endIndex);

                paginatedData.forEach(function (cliente) {
                    tbody.append(`
                        <tr>
                            <td>${cliente.id}</td>
                            <td>${cliente.nombre}</td>
                            <td>${cliente.tipoCliente}</td>
                            <td>${cliente.identificacion}</td>
                            <td>${cliente.telefono || 'N/A'}</td>
                            <td>${cliente.correo || 'N/A'}</td>
                            <td>${cliente.legalOrganizationId || 'N/A'}</td>
                            <td>${cliente.tributeId || 'N/A'}</td>
                            <td>${cliente.aplicaIVA || 'No'}</td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn btn-sm btn-primary edit-cliente" data-id="${cliente.id}" data-bs-toggle="tooltip" title="Editar">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-cliente" data-id="${cliente.id}" data-bs-toggle="tooltip" title="Eliminar">
                                        <i class="fas fa-trash"></i>
                                    </button>
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
                                    <i class="fas fa-users"></i>
                                    <h4>No hay clientes</h4>
                                    <p>No se encontraron clientes que coincidan con la búsqueda.</p>
                                </div>
                            </td>
                        </tr>
                    `);
                }

                renderPagination(totalPages, currentPage, 'clientes');

                $('.edit-cliente').click(function () {
                    const id = $(this).data('id');
                    editCliente(id);
                });

                $('.delete-cliente').click(function () {
                    const id = $(this).data('id');
                    openDeleteModal('cliente', id);
                });
            },
            error: function (xhr) {
                console.error('Error loading clients:', xhr);
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'No se pudieron cargar los clientes'
                });
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
                if (section === 'clientes') {
                    loadClientes(page);
                }
            }
        });
    }

    // Save client
    $('#guardar-cliente').click(function () {
        const id = $('#cliente-id').val();
        const tipoCliente = $('#cliente-tipo').val();
        const tipoIdentificacion = $('#cliente-tipo-identificacion').val();
        const identificacion = $('#cliente-identificacion').val();

        let legalOrganizationId;
        if (tipoCliente === 'Persona Jurídica') {
            const companyName = $('#cliente-company').val() || $('#cliente-nombre').val();
            const companyPrefix = companyName.substring(0, 3).toUpperCase();
            legalOrganizationId = `ORG-${companyPrefix}-${new Date().getTime().toString().substring(5)}`;
        } else {
            const namePrefix = $('#cliente-nombre').val().substring(0, 3).toUpperCase();
            legalOrganizationId = `NAT-${namePrefix}-${new Date().getTime().toString().substring(5)}`;
        }

        const tributePrefix = tipoIdentificacion === 'NIT' ? 'TRIB-NIT' : 'TRIB-CC';
        const tributeId = `${tributePrefix}-${identificacion.replace(/[^0-9]/g, '').substring(0, 6)}-${Math.floor(Math.random() * 1000)}`;

        const cliente = {
            nombre: $('#cliente-nombre').val(),
            tipoCliente: tipoCliente,
            tipoIdentificacion: tipoIdentificacion,
            identificacion: identificacion,
            telefono: $('#cliente-telefono').val(),
            correo: $('#cliente-correo').val(),
            municipio: $('#cliente-municipio').val(),
            municipioId: parseInt($('#cliente-municipio-id').val()) || null,
            direccion: $('#cliente-direccion').val(),
            aplicaIVA: $('#cliente-aplica-iva').val(),
            legalOrganizationId: id ? $('#cliente-legalOrganizationId').val() : legalOrganizationId,
            tributeId: id ? $('#cliente-tributeId').val() : tributeId
        };

        if (tipoCliente === 'Persona Jurídica') {
            cliente.company = $('#cliente-company').val();
            cliente.tradeName = $('#cliente-tradeName').val();
            cliente.verificationDigit = $('#cliente-verificationDigit').val();
            cliente.idOrg = $('#cliente-idOrg').val();
        }

        if (!cliente.nombre || !cliente.identificacion || !cliente.tipoCliente || !cliente.municipioId) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'El nombre, identificación, tipo de cliente y municipio son obligatorios.'
            });
            return;
        }

        if (id) {
            $.ajax({
                url: `/api/clientes/${id}`,
                method: 'PUT',
                data: JSON.stringify(cliente),
                contentType: 'application/json',
                success: function () {
                    $('#clienteModal').modal('hide');
                    loadClientes();
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Éxito',
                        text: 'Cliente actualizado correctamente',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                },
                error: function (xhr) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al actualizar cliente: ' + (xhr.responseJSON?.message || 'Desconocido')
                    });
                }
            });
        } else {
            $.ajax({
                url: '/api/clientes',
                method: 'POST',
                data: JSON.stringify(cliente),
                contentType: 'application/json',
                success: function () {
                    $('#clienteModal').modal('hide');
                    loadClientes();
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Éxito',
                        text: 'Cliente creado correctamente',
                        timer: 2000,
                        timerProgressBar: true,
                        showConfirmButton: false
                    });
                },
                error: function (xhr) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al crear cliente: ' + (xhr.responseJSON?.message || 'Desconocido')
                    });
                }
            });
        }
    });

    // Edit client
    function editCliente(id) {
        console.log("Fetching client data for ID:", id);
        $.ajax({
            url: `/api/clientes/${id}`,
            method: 'GET',
            success: function (cliente) {
                $('#cliente-form').trigger('reset');
                $('#clienteModalLabel').text('Editar Cliente');
                $('#cliente-id').val(cliente.id);
                $('#cliente-tipo').val(cliente.tipoCliente);
                $('#cliente-tipo-identificacion').val(cliente.tipoIdentificacion);
                $('#cliente-identificacion').val(cliente.identificacion);
                $('#cliente-nombre').val(cliente.nombre);
                $('#cliente-telefono').val(cliente.telefono);
                $('#cliente-correo').val(cliente.correo);
                $('#cliente-municipio').val(cliente.municipio);
                $('#cliente-direccion').val(cliente.direccion);
                $('#cliente-aplica-iva').val(cliente.aplicaIVA);
                $('#cliente-legalOrganizationId').val(cliente.legalOrganizationId);
                $('#cliente-tributeId').val(cliente.tributeId);

                if (cliente.tipoCliente === 'Persona Jurídica') {
                    $('#cliente-company').val(cliente.company || '');
                    $('#cliente-tradeName').val(cliente.tradeName || '');
                    $('#cliente-verificationDigit').val(cliente.verificationDigit || '');
                    $('#cliente-idOrg').val(cliente.idOrg || '');
                    $('#campos-juridica').show();
                } else {
                    $('#campos-juridica').hide();
                }

                $('#clienteModal').modal('show');
            },
            error: function (xhr) {
                let errorMsg = 'Error al cargar el cliente';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg += ': ' + xhr.responseJSON.message;
                } else if (xhr.statusText) {
                    errorMsg += ': ' + xhr.statusText;
                }
                alert(errorMsg);
            }
        });
    }

    // Dentro de $(document).ready(function () { ... })

    function openDeleteModal(type, id) {
        $('#delete-type').val(type);
        $('#delete-id').val(id);
        Swal.fire({
            title: '¿Estás seguro?',
            text: 'Eliminar este cliente también eliminará todas sus facturas asociadas. Esta acción no se puede deshacer.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar',
            timer: 6000,
            timerProgressBar: true,
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/api/clientes/${id}`,
                    method: 'DELETE',
                    success: function () {
                        loadClientes();
                        Swal.fire({
                            toast: true,
                            position: 'top-end',
                            icon: 'success',
                            title: 'Éxito',
                            text: 'Cliente y facturas asociadas eliminados correctamente',
                            timer: 2000,
                            timerProgressBar: true,
                            showConfirmButton: false
                        });
                    },
                    error: function (xhr) {
                        Swal.fire({
                            icon: 'error',
                            title: 'Error',
                            text: xhr.responseJSON?.error || 'Error al eliminar cliente'
                        });
                    }
                });
            }
        });
    }

    // Initialize modal
    $('#clienteModal').on('show.bs.modal', function (e) {
        if (e.relatedTarget && !$(e.relatedTarget).hasClass('edit-cliente')) {
            $('#clienteModalLabel').text('Nuevo Cliente');
            $('#cliente-form').trigger('reset');
            $('#cliente-id').val('');
        }
    });
});

