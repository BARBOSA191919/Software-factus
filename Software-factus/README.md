🧾 Software de Facturación Electrónica - Factus API:

Sistema de facturación electrónica desarrollado en Java con Spring Boot, diseñado para gestionar clientes, productos y generar facturas válidas ante la DIAN a través de la API de Factus.

🛠️ Tecnologías utilizadas:

Java 17

Spring Boot

Spring Security + OAuth2 (Login con Google)

Thymeleaf

HTML + CSS + JavaScript

Maven

Factus API (para validación y generación de facturas electrónicas)


📁 Estructura del Proyecto

Software-factus/
└── mini-control-empleados/
    ├── controlador/         → Controladores Spring MVC
    ├── entidades/           → Clases de modelo (Cliente, Factura, Producto, etc.)
    ├── repositorios/        → Interfaces de acceso a datos con Spring Data JPA
    ├── servicio/            → Lógica de negocio y conexión con Factus API
    ├── config/              → Configuración de la app, seguridad y conexión
    ├── static/              → Archivos estáticos (CSS, JS, JSON)
    ├── templates/           → Vistas Thymeleaf
    └── application.properties


🔗 API Consumida:

El sistema se conecta a la API de Factus (Halltec) para emitir, validar y consultar facturas electrónicas en ambiente de pruebas (sandbox).
Más información en: https://www.postman.com/martian-spaceship-418933/api-factus/documentation/fe8izaf/api-factus



🧪 Funcionalidades principales:

Registro y gestión de clientes.

Registro y gestión de productos.

Generación de facturas electrónicas.

Envío y validación ante la DIAN por medio de Factus.

Vista tipo dashboard para control de facturas.

Autenticación con Google.



🚀 Cómo desplegar en tu entorno local:

1. clona el repositorio

git clone https://github.com/BARBOSA191919/Software-factus.git

2. Configura el archivo application.properties con tus credenciales de Factus y la base de datos:
 (puedes usar H2 o tu propia instancia de MySQL/PostgreSQL).


3. Compila y ejecuta la aplicación

./mvnw spring-boot:run

4. Accede a la aplicación

http://localhost:8080/login


📸 Visualización:

Login:

![Image](https://github.com/user-attachments/assets/5f6402ee-00ee-4471-8459-a63748a4c841)

Dashboard principal:

Formulario de cliente:

Formulario de productos:

Formulario de facturas:


🔒 Licencia
Este proyecto está protegido por una licencia propietaria. Todos los derechos reservados © 2025 BARBOSA191919.

No está permitido el uso, copia, distribución ni modificación sin consentimiento expreso del autor.



