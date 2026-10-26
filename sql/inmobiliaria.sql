-- =====================================================================
--  TP JDBC - Sistema de Inmobiliaria
--  Script de creación de la base de datos y carga de datos iniciales.
--
--  Ejecutalo completo en MySQL Workbench, phpMyAdmin o la consola de
--  MySQL
-- =====================================================================

create database if not exists inmobiliaria
CHARACTER SET utf8mb4
collate utf8mb4_0900_ai_ci;

use inmobiliaria;

create table usuarios (
                          id_usuario int auto_increment,
                          tipo_usuario enum('ADMINISTRADOR','CLIENTE','PRODUCTOR_DE_VENTA') not null,
                          nombre varchar(50) not null,
                          apellido varchar(50) not null,
                          telefono varchar(50) not null,
                          email varchar(100) not null,
                          pass_hash varchar(64) not null,
                          activo boolean default false,
                          fecha_alta date not null,

-- Campo exclusivo Admin
                          legajo varchar(20) null,
-- Campo exclusivo Productor de Venta
                          cuil varchar(20) null, -- campo compartido con cliente
                          cantidad_ventas int null default 0,
                          monto_ventas decimal(12,2) null default 0.00,

-- Campo Exclusivo Cliente
                          tipo_cliente enum('COMPRADOR','VENDEDOR','AMBOS') null,


                          constraint pk_usuario primary key(id_usuario),
                          constraint uq_email unique(email)
)engine = InnoDB;

create table propiedades(
                            id_propiedad int auto_increment,
                            tipo_propiedad enum('DEPARTAMENTO','CHALET'),
                            titulo varchar(100),
                            descripcion text,
                            direccion_calle varchar(50) not null,
                            direccion_numero int not null,
                            localidad varchar(50) not null,
                            anio_construccion int not null,
                            precio decimal(12,2) not null default 0.00,
                            tipo_operacion enum('VENTA','ALQUILER'),
                            moneda enum('ARS','USD') not null,
                            ambientes int not null,
                            dormitorios int not null,
                            banios int not null,
                            cochera boolean null default false,
                            balcon boolean null default false,
                            fecha_publicacion date not null,
                            id_cliente int not null,
                            estado_propiedad enum('VENDIDA', 'NO_DISPONIBLE', 'TASACION', 'DISPONIBLE', 'RESERVADA') not null default 'NO_DISPONIBLE',
                            superficie_total decimal (6,2) not null,
                            superficie_descubierta decimal(6,2) not null default 0.00,
                            superficie_cubierta decimal(6,2) not null,
                            ocupacion boolean not null default false,
                            apto_credito boolean not null default true,
-- exclusivo Departamento
                            piso int null default 0,
                            departamento varchar(10) null default 0,
                            expensas decimal(10,2),
-- exclusivo chalet
                            cantidad_pisos int null default 0,
                            pileta boolean null default false,
                            quincho boolean null default false,

                            constraint pk_propiedad primary key(id_propiedad),
                            constraint uq_titulo unique(titulo),
                            constraint fk_cliente foreign key(id_cliente) references usuarios(id_cliente)
)engine = InnoDB;
