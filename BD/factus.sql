-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 12-05-2025 a las 23:53:23
-- Versión del servidor: 8.0.30
-- Versión de PHP: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `factus`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente` (
  `id` bigint NOT NULL,
  `company` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `id_org` varchar(255) DEFAULT NULL,
  `identificacion` varchar(255) DEFAULT NULL,
  `municipio` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `tipo_cliente` varchar(255) DEFAULT NULL,
  `tipo_identificacion` varchar(255) DEFAULT NULL,
  `trade_name` varchar(255) DEFAULT NULL,
  `tribute_id` varchar(255) DEFAULT NULL,
  `verification_digit` varchar(255) DEFAULT NULL,
  `legal_organization_id` varchar(255) DEFAULT NULL,
  `aplicaiva` varchar(255) DEFAULT NULL,
  `municipio_id` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`id`, `company`, `correo`, `direccion`, `id_org`, `identificacion`, `municipio`, `nombre`, `telefono`, `tipo_cliente`, `tipo_identificacion`, `trade_name`, `tribute_id`, `verification_digit`, `legal_organization_id`, `aplicaiva`, `municipio_id`) VALUES
(10, NULL, 'Andres12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '100038752', 'Campohermoso (Boyaca)', 'andres perdomo', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-100038-672', NULL, 'NAT-AND-17622403', NULL, 228),
(12, NULL, 'prueba12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '212121', 'Arjona (Bolivar)', 'nicolle bermeo', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-212121-283', NULL, 'NAT-NIC-18468923', 'Sí', 173),
(16, NULL, 'sbarbosarivas@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '1000386544', 'Bogotá, D.c. (Bogota- D.c.)', 'santiago barbosa', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-100038-476', NULL, 'NAT-SAN-36344118', 'Sí', 169),
(18, NULL, 'prueba13@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '123123123123', 'Concordia (Antioquia)', 'Prueba', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-123123-590', NULL, 'NAT-PRU-82618352', 'Sí', 48),
(19, 'tecnologia', 'nicoll12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '1000386544', 'Tarapaca (Amazonas)', 'origin software', '3217696864', 'Persona Jurídica', 'NIT', 'factus', 'TRIB-NIT-100038-913', '1', 'ORG-TEC-02724491', 'Sí', 11),
(20, NULL, 'leidycuasanchir@gmail.com', 'calle 40 # 3 candido', NULL, '103588784', 'Miriti - Parana (Amazonas)', 'Leidy Yisela Cuansanchir Portilla', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-103588-943', NULL, 'NAT-LEI-78875923', 'Sí', 6),
(21, NULL, 'karol12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '2222222222', 'Aguadas (Caldas)', 'Karol Vela', '3217696864', 'Persona Natural', 'Documento de identificación extranjero', NULL, 'TRIB-CC-222222-823', NULL, 'NAT-KAR-56713788', 'Sí', 339),
(22, NULL, 'camila12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '2222222222', 'Colombia (Huila)', 'camila gutierrez', '3217696864', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-222222-930', NULL, 'NAT-CAM-32438916', 'Sí', 664),
(23, 'tecnologia', 'asdasd@gmail.com', 'calle 40 # 7a guaduales', NULL, '22323232', 'Tarapaca (Amazonas)', 'factus', '3217696864', 'Persona Jurídica', 'NIT', 'factus', 'TRIB-NIT-223232-303', '1', 'ORG-TEC-50745943', 'Sí', 11),
(24, NULL, 'julianpinto12@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '2222222222', 'Guadalupe (Huila)', 'Julian Pinto', '321232123', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-222222-283', NULL, 'NAT-JUL-56933798', 'Sí', 668),
(26, NULL, 'sasasq@gmail.com', 'calle 38 # 7 A 51 las granjas', NULL, '24342332', 'San Fernando (Bolivar)', 'Nicolas Sanabria', '32122122', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-243423-856', NULL, 'NAT-NIC-76786608', 'Sí', 199),
(27, NULL, 'Jorge12@gmail.com', 'calle 38212 # 7 A 51 las granjas', NULL, '22313121212', 'Apartado (Antioquia)', 'Jorge Paterson', '35545354352', 'Persona Natural', 'Documento de identificación extranjero', NULL, 'TRIB-CC-223131-280', NULL, 'NAT-JOR-80621144', 'Sí', 22),
(28, NULL, 'DIEGO12@GMAIL.COM', 'CALLE 43  # 7 A 21 LAS ACACIAS', NULL, '5686465856', 'La Victoria (Amazonas)', 'Diego Rodriguez', '32122321', 'Persona Natural', 'Cédula ciudadanía', NULL, 'TRIB-CC-568646-648', NULL, 'NAT-DIE-37246368', 'Sí', 4),
(29, NULL, 'katherine2@gmail.com', 'caqueta 123', NULL, '5445412216', 'Curillo (Caqueta)', 'Katherine ', '98778', 'Persona Natural', 'Registro civil', NULL, 'TRIB-CC-544541-302', NULL, 'NAT-KAT-11812527', 'Sí', 369),
(30, NULL, 'carlos123@gmail.com', 'avenida 435 nariño', NULL, '8787955623', 'Puerto Colombia (Atlantico)', 'Carlos Rodriguez', '3121225', 'Persona Natural', 'NIT', NULL, 'TRIB-NIT-878795-73', NULL, 'NAT-CAR-29946553', 'Sí', 159),
(31, NULL, 'consumidor12@gmail.com', 'calle 4321', NULL, '2222222222', 'Puerto Triunfo (Antioquia)', 'consumidor final', '212312321', 'Persona Natural', 'Registro civil', NULL, 'TRIB-CC-222222-556', NULL, 'NAT-CON-38087242', 'Sí', 93),
(33, NULL, 'jose12@gmail.com', 'calle 49 # 7 ab 52', NULL, '2222222222', 'Angelopolis (Antioquia)', 'José Martinez', '321223123', 'Persona Natural', 'Registro civil', NULL, 'TRIB-CC-222222-718', NULL, 'NAT-JOS-40371664', 'Sí', 18);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `factura`
--

CREATE TABLE `factura` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `document_name` varchar(255) DEFAULT NULL,
  `forma_pago` varchar(255) DEFAULT NULL,
  `graphic_representation_name` varchar(255) DEFAULT NULL,
  `identification` varchar(255) DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `subtotal` decimal(19,2) DEFAULT NULL,
  `total` decimal(19,2) DEFAULT NULL,
  `total_descuento` decimal(19,2) DEFAULT NULL,
  `total_iva` decimal(19,2) DEFAULT NULL,
  `cliente_id` bigint DEFAULT NULL,
  `document` varchar(255) DEFAULT NULL,
  `numbering_range_id` bigint DEFAULT NULL,
  `observation` varchar(255) DEFAULT NULL,
  `reference_code` varchar(255) DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `inc` decimal(19,2) DEFAULT NULL,
  `municipio` varchar(255) DEFAULT NULL,
  `lines_count` int DEFAULT NULL,
  `total_impuestos` decimal(19,2) DEFAULT NULL,
  `total_inc` decimal(19,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `factura`
--

INSERT INTO `factura` (`id`, `created_at`, `document_name`, `forma_pago`, `graphic_representation_name`, `identification`, `metodo_pago`, `number`, `status`, `subtotal`, `total`, `total_descuento`, `total_iva`, `cliente_id`, `document`, `numbering_range_id`, `observation`, `reference_code`, `fecha_vencimiento`, `inc`, `municipio`, `lines_count`, `total_impuestos`, `total_inc`) VALUES
(144, '2025-04-08 11:45:16.769000', 'Factus', 'Contado', 'PDF', NULL, 'Credito', 'SETP990012442', 'VALIDADA', 100000.00, 119000.00, 0.00, 19000.00, 10, 'Factura electrónica de venta', 128, 'Debe o se envarga a la hembra', 'REF-1744130716769', NULL, NULL, NULL, NULL, NULL, NULL),
(150, '2025-04-10 09:43:34.550000', 'Factus', 'Contado', 'PDF', '1003865544', 'Efectivo', 'SETP990012476', 'VALIDADA', 1400000.00, 1499400.00, 140000.00, 239400.00, 16, 'Factura electrónica de venta', 29, '', 'REF-1744296205086-385', NULL, NULL, NULL, NULL, NULL, NULL),
(153, '2025-04-10 10:27:57.705000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012478', 'VALIDADA', 7500000.00, 8032500.00, 750000.00, 1282500.00, 16, 'Factura electrónica de venta', 1412, '', 'REF-1744298863644-911', NULL, NULL, NULL, NULL, NULL, NULL),
(156, '2025-04-10 10:47:45.741000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012483', 'VALIDADA', 3200000.00, 3808000.00, 0.00, 608000.00, 12, 'Factura electrónica de venta', 2373, '', 'REF-1744300058774-365', NULL, NULL, NULL, NULL, NULL, NULL),
(172, '2025-04-11 14:17:51.030000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012485', 'VALIDADA', 8400000.00, 9450000.00, 840000.00, 1890000.00, 18, 'Factura electrónica de venta', 3974, '', 'REF-1744399064277-772', NULL, NULL, NULL, NULL, NULL, NULL),
(174, '2025-04-11 16:52:14.837000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012490', 'VALIDADA', 130000.00, 154700.00, 0.00, 24700.00, 18, 'Factura electrónica de venta', 1811, '', 'REF-1744408328718-666', NULL, NULL, NULL, NULL, NULL, NULL),
(175, '2025-04-13 16:15:27.589000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012504', 'VALIDADA', 3700000.00, 2985500.00, 1290000.00, 575500.00, 20, 'Factura electrónica de venta', 2572, '', 'REF-1744578900701-859', NULL, NULL, NULL, NULL, NULL, NULL),
(176, '2025-04-14 22:50:32.343000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012514', 'VALIDADA', 150000.00, 178500.00, 0.00, 28500.00, 19, 'Factura electrónica de venta', 640, '', 'REF-1744689027029-338', NULL, NULL, NULL, NULL, NULL, NULL),
(177, '2025-04-15 17:39:56.554000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012531', 'VALIDADA', 400000.00, 428400.00, 40000.00, 68400.00, 21, 'Factura electrónica de venta', 2951, 'Factura de venta', 'REF-1744756772623-965', NULL, NULL, NULL, NULL, NULL, NULL),
(178, '2025-04-17 14:05:25.023000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012554', 'VALIDADA', 3620000.00, 4477000.00, 0.00, 857000.00, 21, 'Factura electrónica de venta', 8844, '', 'REF-1744916715420-967', NULL, NULL, NULL, NULL, NULL, NULL),
(183, '2025-04-17 19:20:23.413000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012562', 'VALIDADA', 900000.00, 1071000.00, 0.00, 171000.00, 22, 'Factura electrónica de venta', 3964, '', 'REF-1744935617676-371', NULL, NULL, NULL, NULL, NULL, NULL),
(184, '2025-04-17 19:28:23.321000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012563', 'VALIDADA', 3200000.00, 3808000.00, 0.00, 608000.00, 22, 'Factura electrónica de venta', 8408, '', 'REF-1744936094071-906', NULL, NULL, NULL, NULL, NULL, NULL),
(185, '2025-04-21 10:23:35.523000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012671', 'VALIDADA', 2860000.00, 3348660.00, 46000.00, 534660.00, 21, 'Factura electrónica de venta', 6131, '', 'REF-1745248998895-702', NULL, NULL, NULL, NULL, NULL, NULL),
(186, '2025-04-21 10:32:35.940000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012672', 'VALIDADA', 120000.00, 144000.00, 0.00, 24000.00, 22, 'Factura electrónica de venta', 1307, 'Compra exitosa', 'REF-1745249537829-124', NULL, NULL, NULL, NULL, NULL, NULL),
(197, '2025-04-21 18:18:07.605000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012674', 'VALIDADA', 870000.00, 1036500.00, 0.00, 166500.00, 26, 'Factura electrónica de venta', 7266, 'soy una gonorrea', 'REF-1745277486319-960', '2025-04-20', NULL, '675', NULL, NULL, NULL),
(202, '2025-04-22 11:48:19.307000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012683', 'VALIDADA', 300000.00, 346500.00, 10800.00, 57300.00, 24, 'Factura electrónica de venta', 371, '', 'REF-1745340483540-890', '2025-04-21', NULL, '17', NULL, NULL, NULL),
(241, '2025-04-22 18:54:32.719000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012694', 'VALIDADA', 6000000.00, 6426000.00, 600000.00, 1026000.00, 24, 'Factura electrónica de venta', 1162, '', 'REF-1745366065712-557', '2025-04-21', NULL, '668', NULL, NULL, NULL),
(242, '2025-04-22 19:24:09.102000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012695', 'VALIDADA', 300000.00, 321300.00, 30000.00, 51300.00, 23, 'Factura electrónica de venta', 9579, '', 'REF-1745367843321-928', '2025-04-21', NULL, '11', NULL, NULL, NULL),
(246, '2025-04-22 21:02:37.542000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012698', 'VALIDADA', 200000.00, 216000.00, 20000.00, 36000.00, 18, 'Factura electrónica de venta', 1210, '', 'REF-1745373751117-555', '2025-04-21', NULL, '48', NULL, NULL, NULL),
(248, '2025-04-22 21:10:19.252000', 'Factus', 'Contado', 'PDF', NULL, 'Transferencia', 'SETP990012699', 'VALIDADA', 7650000.00, 8193150.00, 765000.00, 1308150.00, 26, 'Factura electrónica de venta', 7892, '', 'REF-1745374184500-260', '2025-04-21', NULL, '675', NULL, NULL, NULL),
(250, '2025-04-23 10:08:38.537000', 'Factus', 'Contado', 'PDF', NULL, 'Efectivo', 'SETP990012712', 'VALIDADA', 300000.00, 323460.00, 30000.00, 53460.00, 22, 'Factura electrónica de venta', 1111, '', 'REF-1745420894417-613', '2025-04-22', NULL, '664', NULL, NULL, NULL),
(258, '2025-04-29 09:15:49.175000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012800', 'VALIDADA', 600000.00, 684000.00, 30000.00, 114000.00, 27, NULL, 9035, NULL, 'REF-1745936139126-196', '2025-04-28', NULL, '22', NULL, NULL, NULL),
(259, '2025-04-29 09:34:57.051000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012801', 'VALIDADA', 7250000.00, 8199100.00, 360000.00, 1309100.00, 28, NULL, 8064, NULL, 'REF-1745937256393-528', '2025-04-28', NULL, '4', NULL, NULL, NULL),
(263, '2025-05-02 17:46:04.560000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012845', 'VALIDADA', 640000.00, 685440.00, 64000.00, 109440.00, 16, NULL, 8048, NULL, 'REF-1746225956743-676', '2025-05-01', NULL, '169', NULL, NULL, NULL),
(264, '2025-05-02 17:46:47.311000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012846', 'VALIDADA', 1800000.00, 1927800.00, 180000.00, 307800.00, 30, NULL, 2156, NULL, 'REF-1746225997943-464', '2025-05-01', NULL, '159', NULL, NULL, NULL),
(265, '2025-05-05 15:52:53.911000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012874', 'VALIDADA', 3000000.00, 3570000.00, 0.00, 570000.00, 12, NULL, 886, NULL, 'REF-1746478369831-932', '2025-05-04', NULL, '173', NULL, NULL, NULL),
(267, '2025-05-04 19:00:00.000000', NULL, 'Contado', NULL, NULL, NULL, 'SETP990012876', 'VALIDADA', 500000.00, 595000.00, 0.00, 95000.00, 30, NULL, 2548, NULL, 'REF-1746479358931-4', '2025-05-04', NULL, '159', NULL, NULL, NULL),
(268, '2025-05-04 19:00:00.000000', NULL, 'Contado', NULL, NULL, NULL, 'SETP990012877', 'VALIDADA', 320000.00, 369376.00, 9600.00, 58976.00, 18, NULL, 2209, NULL, 'REF-1746479402890-892', '2025-05-04', NULL, '48', NULL, NULL, NULL),
(269, '2025-05-04 19:00:00.000000', NULL, 'Contado', NULL, NULL, NULL, 'SETP990012878', 'VALIDADA', 920000.00, 1094800.00, 0.00, 174800.00, 12, NULL, 5078, NULL, 'REF-1746479802865-784', '2025-05-04', NULL, '173', NULL, NULL, NULL),
(310, '2025-05-12 09:03:16.966000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012941', 'VALIDADA', 2800000.00, 2998800.00, 280000.00, 478800.00, 33, NULL, 128, NULL, 'REF-1747058592173-917', '2025-05-11', NULL, '18', NULL, NULL, NULL),
(311, '2025-05-12 09:04:08.311000', NULL, 'Contado', NULL, NULL, 'Efectivo', 'SETP990012942', 'VALIDADA', 320000.00, 342720.00, 32000.00, 54720.00, 12, NULL, 128, NULL, 'REF-1747058643180-65', '2025-05-11', NULL, '173', NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `item`
--

CREATE TABLE `item` (
  `id` bigint NOT NULL,
  `cantidad` decimal(19,2) DEFAULT NULL,
  `iva` decimal(19,2) DEFAULT NULL,
  `porcentaje_descuento` decimal(19,2) DEFAULT NULL,
  `precio` decimal(19,2) DEFAULT NULL,
  `subtotal` decimal(19,2) DEFAULT NULL,
  `total` decimal(19,2) DEFAULT NULL,
  `factura_id` bigint DEFAULT NULL,
  `producto_id` bigint DEFAULT NULL,
  `inc` decimal(19,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `item`
--

INSERT INTO `item` (`id`, `cantidad`, `iva`, `porcentaje_descuento`, `precio`, `subtotal`, `total`, `factura_id`, `producto_id`, `inc`) VALUES
(48, 1.00, 19000.00, 0.00, 100000.00, 100000.00, 119000.00, 144, 5, NULL),
(54, 2.00, 266000.00, 10.00, 700000.00, 1400000.00, 1526000.00, 150, 15, NULL),
(61, 6.00, 1026000.00, 10.00, 900000.00, 5400000.00, 5886000.00, 153, 13, NULL),
(62, 3.00, 399000.00, 10.00, 700000.00, 2100000.00, 2289000.00, 153, 15, NULL),
(69, 1.00, 608000.00, 0.00, 3200000.00, 3200000.00, 3808000.00, 156, 6, NULL),
(88, 3.00, 2100000.00, 10.00, 2800000.00, 8400000.00, 9660000.00, 172, 16, NULL),
(91, 1.00, 19000.00, 0.00, 100000.00, 100000.00, 119000.00, 174, 5, NULL),
(92, 1.00, 5700.00, 0.00, 30000.00, 30000.00, 35700.00, 174, 17, NULL),
(93, 1.00, 700000.00, 30.00, 2800000.00, 2800000.00, 2660000.00, 175, 16, NULL),
(94, 1.00, 171000.00, 50.00, 900000.00, 900000.00, 621000.00, 175, 13, NULL),
(96, 2.00, 76000.00, 10.00, 200000.00, 400000.00, 436000.00, 177, 20, 5.00),
(97, 1.00, 133000.00, 0.00, 700000.00, 700000.00, 833000.00, 178, 15, NULL),
(98, 1.00, 700000.00, 0.00, 2800000.00, 2800000.00, 3500000.00, 178, 16, NULL),
(99, 1.00, 24000.00, 0.00, 120000.00, 120000.00, 144000.00, 178, 18, NULL),
(107, 1.00, 171000.00, 0.00, 900000.00, 900000.00, 1071000.00, 183, 13, NULL),
(108, 1.00, 608000.00, 0.00, 3200000.00, 3200000.00, 3808000.00, 184, 6, NULL),
(109, 2.00, 456000.00, 0.00, 1200000.00, 2400000.00, 2856000.00, 185, 14, NULL),
(110, 2.00, 11400.00, 10.00, 30000.00, 60000.00, 65400.00, 185, 17, NULL),
(111, 2.00, 76000.00, 10.00, 200000.00, 400000.00, 436000.00, 185, 20, NULL),
(112, 1.00, 24000.00, 0.00, 120000.00, 120000.00, 144000.00, 186, 18, NULL),
(128, 1.00, 114000.00, 0.00, 600000.00, 600000.00, 714000.00, 197, 22, NULL),
(130, 1.00, 24000.00, 0.00, 120000.00, 120000.00, 144000.00, 197, 18, NULL),
(144, 2.00, 48000.00, 2.00, 120000.00, 240000.00, 283200.00, 202, 18, NULL),
(145, 2.00, 11400.00, 10.00, 30000.00, 60000.00, 65400.00, 202, 17, NULL),
(202, 2.00, 1140000.00, 10.00, 3000000.00, 6000000.00, 6540000.00, 241, 23, NULL),
(207, 1.00, 40000.00, 10.00, 200000.00, 200000.00, 220000.00, 246, 20, NULL),
(210, 2.00, 1140000.00, 10.00, 3000000.00, 6000000.00, 6540000.00, 248, 23, NULL),
(213, 2.00, 11400.00, 10.00, 30000.00, 60000.00, 65400.00, 250, 17, NULL),
(214, 2.00, 48000.00, 10.00, 120000.00, 240000.00, 264000.00, 250, 18, NULL),
(223, 3.00, 120000.00, 5.00, 200000.00, 600000.00, 690000.00, 258, 20, NULL),
(224, 1.00, 9500.00, 0.00, 50000.00, 50000.00, 59500.00, 259, 25, NULL),
(225, 2.00, 228000.00, 5.00, 600000.00, 1200000.00, 1368000.00, 259, 22, NULL),
(226, 2.00, 1140000.00, 5.00, 3000000.00, 6000000.00, 6840000.00, 259, 23, NULL),
(232, 2.00, 121600.00, 10.00, 320000.00, 640000.00, 697600.00, 263, 27, NULL),
(233, 3.00, 342000.00, 10.00, 600000.00, 1800000.00, 1962000.00, 264, 22, NULL),
(234, 1.00, 570000.00, 0.00, 3000000.00, 3000000.00, 3570000.00, 265, 23, NULL),
(237, 1.00, 95000.00, 0.00, 500000.00, 500000.00, 595000.00, 267, 25, NULL),
(239, 1.00, 60800.00, 0.00, 320000.00, 320000.00, 380800.00, 269, 27, NULL),
(240, 1.00, 114000.00, 0.00, 600000.00, 600000.00, 714000.00, 269, 22, NULL),
(295, 1.00, 532000.00, 10.00, 2800000.00, 2800000.00, 3052000.00, 310, 29, NULL),
(296, 1.00, 60800.00, 10.00, 320000.00, 320000.00, 348800.00, 311, 27, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id` bigint NOT NULL,
  `excluded` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `tax_rate` decimal(19,2) DEFAULT NULL,
  `standard_code_id` varchar(255) DEFAULT NULL,
  `unit_measure_id` varchar(255) DEFAULT NULL,
  `inc_rate` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id`, `excluded`, `name`, `price`, `tax_rate`, `standard_code_id`, `unit_measure_id`, `inc_rate`) VALUES
(2, b'1', 'celular iphone ', 400000.00, 19.00, 'STD-CELU-1744402656841', '70', 8),
(5, b'1', 'barrotes', 100000.00, 19.00, 'STD-BARR-1744299796781', '414', 8),
(6, b'1', 'Celular Hawei', 3200000.00, 19.00, 'STD-CELU-1744299790933', '414', 8),
(10, b'1', 'pc de mesa', 32000000.00, 19.00, 'STD-PCDE-1744299540718', '70', 7),
(12, b'1', 'Smarphone 16 pro max', 140000000.00, 19.00, 'STD-SMAR-1744299806668', '414', 2),
(13, b'0', 'celular xiaomi', 900000.00, 19.00, 'STD-CELU-1745250127905', '70', 10),
(14, b'0', 'celular redmi', 1200000.00, 19.00, 'STD-CELU-1745372427051', '70', 9),
(15, b'1', 'celular xiaomi 2', 700000.00, 19.00, 'STD-CELU-1744235012723', '70', 5),
(16, b'1', 'Portail acer', 2800000.00, 25.00, 'STD-PORT-1744382658560', '70', 7),
(17, b'1', 'mouse inalambrico', 30000.00, 19.00, 'STD-MOUS-1744402673722', '70', 8),
(18, b'0', 'celular xiaomi 3', 120000.00, 20.00, 'STD-CELU-1744410930850', '70', 8),
(20, b'1', 'Cargador universal para pc', 200000.00, 20.00, 'STD-CARG-1745250138545', '70', 8),
(22, b'1', 'Impresora', 600000.00, 19.00, 'STD-IMPR-1745276763513', '70', 8),
(23, b'0', 'Pc de mesa hp', 3000000.00, 19.00, 'STD-PCDE-1745877178372', '70', 8),
(25, b'0', 'Calculadora', 500000.00, 19.00, 'STD-CALC-1746222694475', '70', NULL),
(26, b'1', 'soporte de celular ', 32000.00, 19.00, 'STD-SOPO-1745937197484', '70', NULL),
(27, b'1', 'computador asus', 320000.00, 19.00, 'STD-COMP-1746491195236', '70', NULL),
(29, b'1', 'Portatil acer', 2800000.00, 19.00, 'STD-PORT-1746630272349', '70', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tributo`
--

CREATE TABLE `tributo` (
  `id` bigint NOT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `tribute_id` varchar(255) DEFAULT NULL,
  `factura_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Volcado de datos para la tabla `tributo`
--

INSERT INTO `tributo` (`id`, `amount`, `rate`, `tribute_id`, `factura_id`) VALUES
(4, 109440.00, 19, '01', 263),
(5, 307800.00, 19, '01', 264),
(6, 570000.00, 19, '01', 265),
(8, 95000.00, 19, '01', 267),
(9, 58976.00, 19, '01', 268),
(10, 174800.00, 19, '01', 269),
(46, 478800.00, 19, '01', 310),
(47, 54720.00, 19, '01', 311);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `factura`
--
ALTER TABLE `factura`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK2602efsrpmevi8yxg464stfn5` (`cliente_id`);

--
-- Indices de la tabla `item`
--
ALTER TABLE `item`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKc99lsoso80ijvxrfiwese58wo` (`factura_id`),
  ADD KEY `FKkwnhatcjl5aynqitkhy513pka` (`producto_id`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `tributo`
--
ALTER TABLE `tributo`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK79a3cqiplb3y6j57x852dmhf` (`factura_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT de la tabla `factura`
--
ALTER TABLE `factura`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=314;

--
-- AUTO_INCREMENT de la tabla `item`
--
ALTER TABLE `item`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=299;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT de la tabla `tributo`
--
ALTER TABLE `tributo`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=50;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `factura`
--
ALTER TABLE `factura`
  ADD CONSTRAINT `FK2602efsrpmevi8yxg464stfn5` FOREIGN KEY (`cliente_id`) REFERENCES `cliente` (`id`);

--
-- Filtros para la tabla `item`
--
ALTER TABLE `item`
  ADD CONSTRAINT `FKc99lsoso80ijvxrfiwese58wo` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`),
  ADD CONSTRAINT `FKkwnhatcjl5aynqitkhy513pka` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id`);

--
-- Filtros para la tabla `tributo`
--
ALTER TABLE `tributo`
  ADD CONSTRAINT `FK79a3cqiplb3y6j57x852dmhf` FOREIGN KEY (`factura_id`) REFERENCES `factura` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
