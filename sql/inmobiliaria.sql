-- =====================================================================
--  TP Final Programacion 2 - Sistema de Inmobiliaria
--  Base de datos para JDBC (MySQL). Se ejecuta completo en Workbench.
--  Las columnas coinciden con las clases del TP (Usuario, Propiedad,
--  Departamento, Chalet) y con los enums del paquete enums.
-- =====================================================================

DROP DATABASE IF EXISTS inmobiliaria;
CREATE DATABASE inmobiliaria
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE inmobiliaria;

-- ---------------------------------------------------------------------
--  USUARIOS (Administrador, Cliente y ProductorDeVenta en una sola tabla)
-- ---------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario       INT AUTO_INCREMENT,
    tipo_usuario     ENUM('ADMINISTRADOR','CLIENTE','PRODUCTOR_DE_VENTA') NOT NULL,
    nombre           VARCHAR(50)  NOT NULL,
    apellido         VARCHAR(50)  NOT NULL,
    telefono         VARCHAR(50)  NOT NULL,
    email            VARCHAR(100) NOT NULL,
    pass_hash        VARCHAR(64)  NOT NULL,
    activo           BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_alta       DATE         NOT NULL,
    -- solo Administrador
    legajo           VARCHAR(20)  NULL,
    -- ProductorDeVenta (cuil) y Cliente (dni) usan la misma columna
    cuil             VARCHAR(20)  NULL,
    -- solo ProductorDeVenta
    cantidad_ventas  INT           NULL DEFAULT 0,
    monto_ventas     DECIMAL(12,2) NULL DEFAULT 0.00,

    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_email UNIQUE (email)
) ENGINE = InnoDB;


CREATE TABLE propiedades (
    id_propiedad         INT AUTO_INCREMENT,
    tipo_propiedad       ENUM('DEPARTAMENTO','CHALET') NOT NULL,
    descripcion          VARCHAR(150),
    zona                 ENUM('NORTE','SUR','CENTRO') NOT NULL,
    direccion_calle      VARCHAR(80)  NOT NULL,
    direccion_numero     INT          NOT NULL,
    precio               INT          NOT NULL DEFAULT 0,
    balcon               BOOLEAN      NOT NULL DEFAULT FALSE,
    ambientes            INT          NOT NULL,
    dormitorios          INT          NOT NULL DEFAULT 0,
    banios               INT          NOT NULL,
    cochera              BOOLEAN      NOT NULL DEFAULT FALSE,
    anio_construccion    INT          NOT NULL,
    fecha_publicacion    DATE         NOT NULL,
    estado_propiedad     ENUM('VENDIDA','NO_DISPONIBLE','TASACION','DISPONIBLE','RESERVADA') NOT NULL DEFAULT 'TASACION',
    estado_conservacion  ENUM('EXCELENTE','MUY_BUENO','BUENO','A_RECICLAR','NADA') NOT NULL DEFAULT 'NADA',
    terreno              DECIMAL(8,2) NOT NULL DEFAULT 0.00,
    superficie_cubierta  DECIMAL(8,2) NOT NULL,
    -- solo Departamento
    departamento         VARCHAR(10)  NULL,
    piso                 INT          NULL,
    ascensor             BOOLEAN      NULL,
    vista_al_mar         BOOLEAN      NULL,
    disposicion          ENUM('FRENTE','CONTRA_FRENTE','LATERAL','INTERNO','NINGUNA') NULL,
    -- solo Chalet
    cantidad_pisos       INT          NULL,
    pileta               BOOLEAN      NULL,
    quincho              BOOLEAN      NULL,
    jardin               BOOLEAN      NULL,

    CONSTRAINT pk_propiedad PRIMARY KEY (id_propiedad)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
--  DATOS DE PRUEBA - USUARIOS (personas inventadas)
--  La contraseña de todos es 1234 (guardada con SHA-256 como en SecurityUtils)
-- ---------------------------------------------------------------------
INSERT INTO usuarios (tipo_usuario, nombre, apellido, telefono, email, pass_hash, activo, fecha_alta, legajo, cuil, cantidad_ventas, monto_ventas) VALUES
('ADMINISTRADOR',      'Laura',   'Gomez',    '223-5550101', 'admin@inmobiliaria.com',   '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-01', 'ADM-001', NULL, NULL, NULL),
('PRODUCTOR_DE_VENTA', 'Martin',  'Rivas',    '223-5550202', 'mrivas@inmobiliaria.com',  '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-05', NULL, '20-30111222-3', 4, 410000.00),
('PRODUCTOR_DE_VENTA', 'Carla',   'Benitez',  '223-5550303', 'cbenitez@inmobiliaria.com','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', false, '2026-11-10', NULL, '27-35444555-6', 0, 0.00),
('CLIENTE',            'Jorge',   'Paredes',  '223-5550404', 'jparedes@mail.com',        '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-20', NULL, '28111222',      NULL, NULL),
('CLIENTE',            'Sofia',   'Lucero',   '223-5550505', 'slucero@mail.com',         '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-11-02', NULL, '35999888',      NULL, NULL),
('CLIENTE',            'Ramiro',  'Acosta',   '223-5550606', 'racosta@mail.com',         '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-11-12', NULL, '40123456',      NULL, NULL),
('PRODUCTOR_DE_VENTA', 'Luciano', 'Mendez',   '223-5550707', 'lmendez@inmobiliaria.com', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-10', NULL, '20-28333444-1', 7, 785000.00),
('PRODUCTOR_DE_VENTA', 'Valeria', 'Duarte',   '223-5550808', 'vduarte@inmobiliaria.com',  '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-12', NULL, '27-32111222-4', 2, 195000.00),
('PRODUCTOR_DE_VENTA', 'Marcos',  'Sosa',     '223-5550909', 'msosa@inmobiliaria.com',    '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-18', NULL, '20-37444555-9', 5, 520000.00),
('PRODUCTOR_DE_VENTA', 'Natalia', 'Gimenez',  '223-5551010', 'ngimenez@inmobiliaria.com','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-10-25', NULL, '27-39111222-5', 1,  89000.00),
('PRODUCTOR_DE_VENTA', 'Federico','Blanco',   '223-5551111', 'fblanco@inmobiliaria.com',  '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', true,  '2026-11-01', NULL, '20-33555666-8', 3, 310000.00);




-- ---------------------------------------------------------------------
--  DATOS DE PRUEBA - PROPIEDADES
--  Zona sur: tomadas de propiedades publicadas en Punta Mogotes, Alfar y
--  Bosque Peralta Ramos (sin datos de propietarios). Centro y norte: inventadas.
--  El precio esta calculado con calcularPrecioFinal() del TP.
-- ---------------------------------------------------------------------
INSERT INTO propiedades (tipo_propiedad, descripcion, zona, direccion_calle, direccion_numero,
    precio, balcon, ambientes, dormitorios, banios, cochera, anio_construccion, fecha_publicacion,
    estado_propiedad, estado_conservacion, terreno, superficie_cubierta,
    departamento, piso, ascensor, vista_al_mar, disposicion,
    cantidad_pisos, pileta, quincho, jardin) VALUES
('CHALET', 'Casa en Alfar', 'SUR', 'Castilla y Leon', 936, 82950, false, 2, 1, 1, false, 1995, '2026-11-17', 'DISPONIBLE', 'BUENO', 50.00, 75.00, NULL, NULL, NULL, NULL, NULL, 1, false, false, true),
('DEPARTAMENTO', 'Departamento en Punta Mogotes', 'SUR', 'Pacheco', 2072, 31920, false, 2, 1, 1, false, 1985, '2026-11-17', 'DISPONIBLE', 'BUENO', 0.00, 40.00, '5', 0, false, false, 'INTERNO', NULL, NULL, NULL, NULL),
('CHALET', 'Casa en Punta Mogotes', 'SUR', 'Vucetich', 2281, 99456, false, 2, 1, 1, true, 2005, '2026-11-10', 'DISPONIBLE', 'MUY_BUENO', 0.00, 74.00, NULL, NULL, NULL, NULL, NULL, 1, false, false, false),
('CHALET', 'Casa en Punta Mogotes', 'SUR', 'Giacobini', 2678, 103320, false, 3, 2, 1, true, 1980, '2026-11-05', 'DISPONIBLE', 'BUENO', 120.00, 90.00, NULL, NULL, NULL, NULL, NULL, 1, false, true, true),
('CHALET', 'Casa en Bosque Peralta Ramos', 'SUR', 'Tupac Amaru', 174, 309120, false, 4, 3, 2, true, 2012, '2026-11-10', 'DISPONIBLE', 'EXCELENTE', 600.00, 160.00, NULL, NULL, NULL, NULL, NULL, 2, true, true, true),
('CHALET', 'Casa en Bosque Peralta Ramos', 'SUR', 'Los Quichuas', 350, 317520, false, 4, 3, 2, true, 2015, '2026-11-11', 'RESERVADA', 'MUY_BUENO', 800.00, 180.00, NULL, NULL, NULL, NULL, NULL, 2, true, true, true),
('CHALET', 'Casa en Punta Mogotes', 'SUR', 'Giacobini', 1930, 115430, false, 3, 2, 1, false, 1975, '2026-11-14', 'NO_DISPONIBLE', 'A_RECICLAR', 150.00, 85.00, NULL, NULL, NULL, NULL, NULL, 1, false, false, true),
('DEPARTAMENTO', 'Departamento en Punta Mogotes', 'SUR', 'Castro Barros', 2668, 36708, true, 2, 1, 1, false, 1990, '2026-11-04', 'NO_DISPONIBLE', 'BUENO', 0.00, 38.00, '21', 2, false, false, 'CONTRA_FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Departamento en Chauvin', 'CENTRO', 'Roca', 3135, 75600, true, 3, 2, 1, false, 1978, '2026-11-12', 'DISPONIBLE', 'BUENO', 0.00, 70.00, 'A', 1, false, false, 'FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Departamento en Centro', 'CENTRO', 'San Martin', 2450, 54000, true, 2, 1, 1, false, 1970, '2026-11-08', 'DISPONIBLE', 'BUENO', 0.00, 45.00, 'B', 7, true, false, 'FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Departamento con vista al mar', 'CENTRO', 'Av. Colon', 1050, 193800, true, 3, 2, 2, true, 2018, '2026-11-15', 'DISPONIBLE', 'EXCELENTE', 0.00, 85.00, 'A', 10, true, true, 'FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Monoambiente en Centro', 'CENTRO', 'Rivadavia', 2780, 37800, false, 1, 0, 1, false, 1965, '2026-10-20', 'VENDIDA', 'A_RECICLAR', 0.00, 28.00, 'C', 3, true, false, 'INTERNO', NULL, NULL, NULL, NULL),
('CHALET', 'Casa en Constitucion', 'NORTE', 'Av. Constitucion', 5200, 203280, false, 4, 3, 2, true, 2010, '2026-11-09', 'DISPONIBLE', 'MUY_BUENO', 300.00, 140.00, NULL, NULL, NULL, NULL, NULL, 2, true, true, true),
('DEPARTAMENTO', 'Departamento en Constitucion', 'NORTE', 'Av. Constitucion', 4800, 81000, true, 2, 1, 1, true, 2020, '2026-11-13', 'DISPONIBLE', 'EXCELENTE', 0.00, 50.00, 'D', 3, true, false, 'LATERAL', NULL, NULL, NULL, NULL),
('CHALET', 'Casa en Parque Luro', 'NORTE', 'Padre Cardiel', 1200, 112800, false, 3, 2, 1, true, 1998, '2026-11-16', 'TASACION', 'BUENO', 250.00, 100.00, NULL, NULL, NULL, NULL, NULL, 1, false, true, true),
('CHALET', 'Segundo Chalet en Alfar', 'SUR', 'Castilla y Leon', 1140, 91000, false, 3, 2, 2, true, 2002, '2026-11-18', 'DISPONIBLE', 'MUY_BUENO', 120.00, 95.00, NULL, NULL, NULL, NULL, NULL, 1, true, false, true),
('CHALET', 'Chalet sobre Giacobini', 'SUR', 'Giacobini', 2410, 88000, false, 2, 1, 1, false, 1990, '2026-11-15', 'DISPONIBLE', 'BUENO', 100.00, 70.00, NULL, NULL, NULL, NULL, NULL, 1, false, false, true),
('DEPARTAMENTO', 'Depto 2 ambientes en Zona Sur', 'SUR', 'Pacheco', 2150, 33000, true, 2, 1, 1, false, 1992, '2026-11-16', 'DISPONIBLE', 'BUENO', 0.00, 42.00, '12', 1, false, false, 'FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Monoambiente céntrico', 'CENTRO', 'San Martin', 1820, 41000, false, 1, 0, 1, false, 1988, '2026-11-14', 'DISPONIBLE', 'BUENO', 0.00, 30.00, '2', 2, false, false, 'INTERNO', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Amplio piso en calle Roca', 'CENTRO', 'Roca', 2250, 98000, true, 4, 3, 2, true, 2011, '2026-11-10', 'DISPONIBLE', 'EXCELENTE', 0.00, 110.00, 'B', 4, true, false, 'FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Depto frente a la plaza', 'CENTRO', 'San Martin', 3010, 62000, true, 3, 2, 1, false, 1995, '2026-11-05', 'RESERVADA', 'MUY_BUENO', 0.00, 65.00, '4A', 4, true, false, 'FRENTE', NULL, NULL, NULL, NULL),
('CHALET', 'Chalet en Constitución Norte', 'NORTE', 'Av. Constitucion', 6100, 240000, false, 5, 4, 3, true, 2019, '2026-11-12', 'DISPONIBLE', 'EXCELENTE', 450.00, 210.00, NULL, NULL, NULL, NULL, NULL, 2, true, true, true),
('DEPARTAMENTO', 'Duplex en Constitución', 'NORTE', 'Av. Constitucion', 3900, 95000, true, 3, 2, 2, true, 2021, '2026-11-17', 'DISPONIBLE', 'EXCELENTE', 0.00, 80.00, '101', 1, true, true, 'FRENTE', NULL, NULL, NULL, NULL),
('CHALET', 'Casa familiar en Parque Luro', 'NORTE', 'Padre Cardiel', 1550, 129000, false, 4, 3, 2, true, 2004, '2026-11-11', 'DISPONIBLE', 'MUY_BUENO', 300.00, 125.00, NULL, NULL, NULL, NULL, NULL, 1, false, true, true),
('DEPARTAMENTO', 'Monoambiente zona norte', 'NORTE', 'Av. Constitucion', 4200, 48000, false, 1, 0, 1, false, 2015, '2026-11-03', 'DISPONIBLE', 'MUY_BUENO', 0.00, 32.00, '3C', 3, true, false, 'CONTRA_FRENTE', NULL, NULL, NULL, NULL),
('DEPARTAMENTO', 'Departamento 2 ambientes centro', 'CENTRO', 'Roca', 1500, 52000, true, 2, 1, 1, false, 1982, '2026-11-07', 'DISPONIBLE', 'BUENO', 0.00, 48.00, '7B', 7, true, false, 'LATERAL', NULL, NULL, NULL, NULL),
('CHALET', 'Chalet clásico zona sur', 'SUR', 'Giacobini', 2180, 107000, false, 3, 2, 1, true, 1985, '2026-10-30', 'DISPONIBLE', 'BUENO', 180.00, 92.00, NULL, NULL, NULL, NULL, NULL, 1, false, true, true),
('DEPARTAMENTO', 'Depto 3 ambientes sur', 'SUR', 'Pacheco', 2300, 45000, true, 3, 2, 1, false, 1994, '2026-11-02', 'DISPONIBLE', 'BUENO', 0.00, 60.00, '14', 3, false, false, 'FRENTE', NULL, NULL, NULL, NULL),
('CHALET', 'Casa amplia en Alfar', 'SUR', 'Castilla y Leon', 850, 79000, false, 2, 1, 1, false, 1990, '2026-10-28', 'TASACION', 'A_RECICLAR', 200.00, 68.00, NULL, NULL, NULL, NULL, NULL, 1, false, false, true),
('DEPARTAMENTO', 'Depto céntrico San Martín', 'CENTRO', 'San Martin', 2100, 59000, true, 2, 1, 1, false, 1976, '2026-11-06', 'DISPONIBLE', 'BUENO', 0.00, 52.00, '8', 8, true, false, 'FRENTE', NULL, NULL, NULL, NULL);

