$(document).ready(function() {
    // Product search
    $('#buscar-productos').on('keyup', function() {
        loadProductos(1);
    });

    // Navigation: Show productos section on link or card click
    $('#productos-link').click(function(e) {
        e.preventDefault();
        $('.content-section').hide();
        $('#productos-content').show();
        loadProductos();
    });

    $('.clickable-card[data-section="productos"]').click(function() {
        $('.content-section').hide();
        $('#productos-content').show();
        loadProductos();
    });

    // Load products
    function loadProductos(page = 1, itemsPerPage = 9) {
        $.ajax({
            url: '/api/productos',
            method: 'GET',
            success: function(data) {
                data.sort((a, b) => b.id - a.id);
                const tbody = $('#productos-table-body');
                tbody.empty();

                const searchTerm = $('#buscar-productos').val().toLowerCase();
                const filteredData = data.filter(producto =>
                    producto.id.toString().includes(searchTerm) ||
                    producto.name.toLowerCase().includes(searchTerm)
                );

                const totalItems = filteredData.length;
                const totalPages = Math.ceil(totalItems / itemsPerPage);
                const startIndex = (page - 1) * itemsPerPage;
                const endIndex = startIndex + itemsPerPage;
                const paginatedData = filteredData.slice(startIndex, endIndex);

                paginatedData.forEach(function(producto) {
                    tbody.append(`
                        <tr>
                            <td>${producto.id}</td>
                            <td>${producto.name}</td>
                            <td>$${parseFloat(producto.price).toFixed(2)}</td>
                            <td>${producto.unitMeasureId || 'N/A'}</td>
                            <td>${producto.taxRate ? producto.taxRate + '%' : '0%'}</td>
                            <td>${producto.standardCodeId || 'Auto'}</td>
                            <td>
                                <div class="action-buttons">
                                    <button class="btn btn-sm btn-primary edit-producto" data-id="${producto.id}" data-bs-toggle="tooltip" title="Editar">
                                        <i class="fas fa-edit"></i>
                                    </button>
                                    <button class="btn btn-sm btn-danger delete-producto" data-id="${producto.id}" data-bs-toggle="tooltip" title="Eliminar">
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
                            <td colspan="7" class="text-center">
                                <div class="empty-state">
                                    <i class="fas fa-box-open"></i>
                                    <h4>No hay productos</h4>
                                    <p>No se encontraron productos que coincidan con la búsqueda.</p>
                                </div>
                            </td>
                        </tr>
                    `);
                }

                renderPagination(totalPages, page, 'productos');

                $('.edit-producto').click(function(e) {
                    e.preventDefault();
                    const id = $(this).data('id');
                    editProducto(id);
                });

                $('.delete-producto').click(function(e) {
                    e.preventDefault();
                    const id = $(this).data('id');
                    openDeleteModal('producto', id);
                });
            },
            error: function(xhr) {
                $('#productos-table-body').html(`
                    <tr>
                        <td colspan="7" class="text-center text-danger">
                            Error al cargar productos
                        </td>
                    </tr>
                `);
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

        $(`#${section}-pagination .page-link`).click(function(e) {
            e.preventDefault();
            const page = $(this).data('page');
            if (page && !$(this).parent().hasClass('disabled') && !$(this).parent().hasClass('active')) {
                if (section === 'productos') {
                    loadProductos(page);
                }
            }
        });
    }

    // Save product
    $('#guardar-producto').click(function() {
        const id = $('#producto-id').val();
        const currentTime = new Date().getTime();
        const nameSanitized = $('#producto-nombre').val().replace(/[^a-zA-Z0-9]/g, '').substring(0, 4).toUpperCase();

        const producto = {
            name: $('#producto-nombre').val(),
            price: parseFloat($('#producto-precio').val()),
            excluded: $('#producto-excluido').is(':checked'),
            withholdingTaxRate: $('#producto-retencion').val() || "0",
            taxRate: parseFloat($('#producto-tasa-iva').val()),
            standardCodeId: `STD-${nameSanitized}-${currentTime}`,
            unitMeasureId: $('#producto-medida').val(),
        };

        if (!producto.name || producto.price <= 0 || !producto.taxRate) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'El nombre, precio y tasa de IVA son obligatorios y deben ser válidos.'
            });
            return;
        }

        if (id) {
            $.ajax({
                url: `/api/productos/${id}`,
                method: 'PUT',
                data: JSON.stringify(producto),
                contentType: 'application/json',
                success: function() {
                    $('#productoModal').modal('hide');
                    loadProductos();
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Éxito',
                        text: 'Producto actualizado correctamente',
                        timer: 2000,
                        showConfirmButton: false
                    });
                },
                error: function(xhr) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al actualizar producto: ' + (xhr.responseJSON?.message || 'Desconocido')
                    });
                }
            });
        } else {
            $.ajax({
                url: '/api/productos',
                method: 'POST',
                data: JSON.stringify(producto),
                contentType: 'application/json',
                success: function() {
                    $('#productoModal').modal('hide');
                    loadProductos();
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Éxito',
                        text: 'Producto creado correctamente',
                        timer: 2000,
                        showConfirmButton: false
                    });
                },
                error: function(xhr) {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'Error al crear producto: ' + (xhr.responseJSON?.message || 'Desconocido')
                    });
                }
            });
        }
    });

    // Edit product
    function editProducto(id) {
        $.ajax({
            url: `/api/productos/${id}`,
            method: 'GET',
            success: function(producto) {
                $('#producto-id').val(producto.id);
                $('#producto-nombre').val(producto.name);
                $('#producto-precio').val(producto.price);
                $('#producto-excluido').prop('checked', producto.excluded);
                $('#producto-retencion').val(producto.withholdingTaxRate);
                $('#producto-tasa-iva').val(producto.taxRate);
                $('#producto-standardCodeId').val(producto.standardCodeId);
                $('#producto-unitMeasureId').val(producto.unitMeasureId);
                $('#productoModalLabel').text('Editar Producto');
                $('#productoModal').modal('show');
            },
            error: function(xhr) {
                let errorMsg = 'Error al cargar el producto';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg += ': ' + xhr.responseJSON.message;
                } else if (xhr.statusText) {
                    errorMsg += ': ' + xhr.statusText;
                }
                alert(errorMsg);
            }
        });
    }

    // Delete modal
    function openDeleteModal(type, id) {
        $('#delete-type').val(type);
        $('#delete-id').val(id);
        $('#confirmDeleteModal').modal('show');
    }

    $('#confirmar-eliminacion').click(function() {
        const type = $('#delete-type').val();
        const id = $('#delete-id').val();

        if (type === 'producto') {
            $.ajax({
                url: `/api/productos/${id}`,
                method: 'DELETE',
                success: function() {
                    $('#confirmDeleteModal').modal('hide');
                    loadProductos();
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Éxito',
                        text: 'Producto eliminado correctamente',
                        timer: 2000,
                        showConfirmButton: false
                    });
                },
                error: function(error) {
                    alert(`Error al eliminar producto: ${error.responseJSON.message}`);
                }
            });
        }
    });

    // Initialize modal
    $('#productoModal').on('show.bs.modal', function(e) {
        if (e.relatedTarget && !$(e.relatedTarget).hasClass('edit-producto')) {
            $('#productoModalLabel').text('Nuevo Producto');
            $('#producto-form').trigger('reset');
            $('#producto-id').val('');
        }
    });
});