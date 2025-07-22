🧾 Software de Facturación Electrónica - Software Factus: 

 - Sistema de facturación electrónica desarrollado en Java con Spring Boot, diseñado para gestionar clientes, productos y generar facturas válidas ante la DIAN a través de la API de Factus.

🛠️ Tecnologías utilizadas:

 - Java 17

 - Spring Boot

 - Spring Security + OAuth2 (Login con Google)

 - Thymeleaf

 - HTML + CSS + JavaScript

 - Maven


* Fctus API (para validación y generación de facturas electrónicas)

📁 Estructura del Proyecto: 

    Software-factus/


    └── mini-control-empleados/


    ├── controlador/       

  	→ Controladores Spring MVC

    ├── entidades/          
 
	→ Clases de modelo (Cliente, Factura, Producto, etc.)

    ├── repositorios/       

	 → Interfaces de acceso a datos con Spring Data JPA

    ├── servicio/    
      
 	 → Lógica de negocio y conexión con Factus API

    ├── config/    
        
 	 → Configuración de la app, seguridad y conexión

    ├── static/   
          
	 → Archivos estáticos (CSS, JS, JSON)

    ├── templates/     
      
	→ Vistas Thymeleaf

    └── application.properties


🔗 API Consumida:

 - El sistema se conecta a la API de Factus (Halltec) para emitir, validar y consultar facturas electrónicas en ambiente de pruebas (sandbox).

 - Más información en: https://www.postman.com/martian-spaceship-418933/api-factus/documentation/fe8izaf/api-factus


* Funcionalidades principales:

 - Registro y gestión de clientes

 - Registro y gestión de productos

 - Generación de facturas electrónicas

 - Envío y validación ante la DIAN por medio de Factus

 - Vista tipo Dashboard para control de facturas

 - Autenticación con Google



🚀 Cómo desplegar en tu entorno local:

1. clona el repositorio

 - git clone https://github.com/BARBOSA191919/Software-factus.git

 - cd Software-factus/Software-factus/mini-control-empleados



2. Configura el archivo application.properties con tus credenciales de Factus y la base de datos:

 - Puedes usar H2 o tu propia instancia de MySQL/PostgreSQL.



3. Compila y ejecuta la aplicación

 - ./mvnw spring-boot:run



4. Accede a la aplicación

 - http://localhost:8080/login



📸 Visualización:

1. Login:

![Image](https://github.com/user-attachments/assets/5f6402ee-00ee-4471-8459-a63748a4c841)

2. Interfaz de Google :

![Image](https://github.com/user-attachments/assets/73bf6692-58c5-42fd-82cc-5f55091b481a)

3.Panel oscuro principal :

![Image](https://github.com/user-attachments/assets/5a78a391-650c-4c27-aea7-ef42cd42adce)

4. panel oscuro de productos:

![Image](https://github.com/user-attachments/assets/6b6a34fd-7e8a-4a8b-927e-b2adb113dd22)

5. Panel oscuro de clientes:

![Image](https://github.com/user-attachments/assets/1e71dd77-f0bd-4bf0-9940-85b971beb9b7)

6. Panel oscuro de facturas:

![Image](https://github.com/user-attachments/assets/c109a606-07a3-49c1-966e-a630fb87e0c7)

7. Modal de producto: 

![Image](https://github.com/user-attachments/assets/ff085d30-1f3e-48c3-a83d-0f71d04c4873)

8. Modal de cliente: 

![Image](https://github.com/user-attachments/assets/aad9891a-52bd-4210-8ce6-6ba8bba3f7fd)

9. Modal de factura: 

![Image](https://github.com/user-attachments/assets/5fbceec5-51e3-44c6-9e77-7b5f3a51bccf)

10. Creación de factura

![Image](https://github.com/user-attachments/assets/d368c676-99ff-4416-b280-cc468722c472)


🔒 Licencia:

 - Este proyecto está protegido por una licencia propietaria. Todos los derechos reservados © 2025 BARBOSA191919

 - No está permitido el uso, copia, distribución ni modificación sin consentimiento expreso del autor.


