CREATE DATABASE IF NOT EXISTS sistema_restaurante
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE sistema_restaurante;

CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente VARCHAR(64) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_cliente)
) ENGINE=InnoDB;

SET @cliente_activo_existe = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'Cliente'
      AND column_name = 'activo'
);
SET @sql_migracion_cliente = IF(@cliente_activo_existe = 0,
    'ALTER TABLE Cliente ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1',
    'SELECT 1');
PREPARE sentencia_migracion_cliente FROM @sql_migracion_cliente;
EXECUTE sentencia_migracion_cliente;
DEALLOCATE PREPARE sentencia_migracion_cliente;

CREATE TABLE IF NOT EXISTS Mesa (
    id_mesa VARCHAR(64) NOT NULL,
    numero VARCHAR(30) NOT NULL,
    capacidad INT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_mesa),
    CONSTRAINT chk_mesa_capacidad CHECK (capacidad > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Producto (
    id_producto VARCHAR(64) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(50) NOT NULL DEFAULT 'Otros',
    descripcion VARCHAR(255) NOT NULL DEFAULT '',
    precio_unitario DECIMAL(10,2) NOT NULL,
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_producto),
    CONSTRAINT chk_producto_precio CHECK (precio_unitario > 0),
    CONSTRAINT chk_producto_disponible CHECK (disponible IN (0, 1))
) ENGINE=InnoDB;

SET @producto_categoria_existe = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'Producto'
      AND column_name = 'categoria'
);
SET @sql_migracion_producto_categoria = IF(@producto_categoria_existe = 0,
    'ALTER TABLE Producto ADD COLUMN categoria VARCHAR(50) NOT NULL DEFAULT ''Otros''',
    'SELECT 1');
PREPARE sentencia_migracion_producto_categoria FROM @sql_migracion_producto_categoria;
EXECUTE sentencia_migracion_producto_categoria;
DEALLOCATE PREPARE sentencia_migracion_producto_categoria;

SET @producto_descripcion_existe = (
    SELECT COUNT(*) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'Producto'
      AND column_name = 'descripcion'
);
SET @sql_migracion_producto_descripcion = IF(@producto_descripcion_existe = 0,
    'ALTER TABLE Producto ADD COLUMN descripcion VARCHAR(255) NOT NULL DEFAULT ''Sin descripción''',
    'SELECT 1');
PREPARE sentencia_migracion_producto_descripcion
    FROM @sql_migracion_producto_descripcion;
EXECUTE sentencia_migracion_producto_descripcion;
DEALLOCATE PREPARE sentencia_migracion_producto_descripcion;

CREATE TABLE IF NOT EXISTS Pedido (
    id_pedido VARCHAR(64) NOT NULL,
    fecha DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    id_cliente VARCHAR(64) NULL,
    id_mesa VARCHAR(64) NULL,
    PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente)
        REFERENCES Cliente (id_cliente),
    CONSTRAINT fk_pedido_mesa FOREIGN KEY (id_mesa)
        REFERENCES Mesa (id_mesa) ON DELETE SET NULL,
    CONSTRAINT chk_pedido_total CHECK (total >= 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS DetallePedido (
    id_detalle INT NOT NULL AUTO_INCREMENT,
    id_pedido VARCHAR(64) NOT NULL,
    id_producto VARCHAR(64) NOT NULL,
    cantidad INT NOT NULL,
    precio_aplicado DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido)
        REFERENCES Pedido (id_pedido),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto)
        REFERENCES Producto (id_producto),
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_detalle_precio CHECK (precio_aplicado > 0)
) ENGINE=InnoDB;

SET @fk_pedido_mesa_delete_rule = (
    SELECT DELETE_RULE
    FROM information_schema.REFERENTIAL_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'Pedido'
      AND CONSTRAINT_NAME = 'fk_pedido_mesa'
    LIMIT 1
);
SET @sql_migracion_fk_pedido_mesa_drop = IF(
    @fk_pedido_mesa_delete_rule IS NOT NULL
        AND UPPER(@fk_pedido_mesa_delete_rule) <> 'SET NULL',
    'ALTER TABLE Pedido DROP FOREIGN KEY fk_pedido_mesa',
    'SELECT 1'
);
PREPARE sentencia_migracion_fk_pedido_mesa_drop
    FROM @sql_migracion_fk_pedido_mesa_drop;
EXECUTE sentencia_migracion_fk_pedido_mesa_drop;
DEALLOCATE PREPARE sentencia_migracion_fk_pedido_mesa_drop;

SET @sql_migracion_fk_pedido_mesa_add = IF(
    @fk_pedido_mesa_delete_rule IS NULL
        OR UPPER(@fk_pedido_mesa_delete_rule) <> 'SET NULL',
    'ALTER TABLE Pedido ADD CONSTRAINT fk_pedido_mesa FOREIGN KEY (id_mesa) REFERENCES Mesa (id_mesa) ON DELETE SET NULL',
    'SELECT 1'
);
PREPARE sentencia_migracion_fk_pedido_mesa_add
    FROM @sql_migracion_fk_pedido_mesa_add;
EXECUTE sentencia_migracion_fk_pedido_mesa_add;
DEALLOCATE PREPARE sentencia_migracion_fk_pedido_mesa_add;
