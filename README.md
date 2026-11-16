# Sistema de Gestión de Pedidos de Restaurante

Aplicación de escritorio desarrollada en **Java (JDK 21)** utilizando **Java Swing** y persistencia relacional en **MySQL** (vía XAMPP). El sistema permite administrar clientes, mesas, productos y el ciclo completo de pedidos con cálculo automático de subtotales y totales.

---

## 📋 Requisitos Previos y Descargas Oficiales

Para mantener ligero el paquete de entrega del proyecto, no se incluyen los instaladores de las herramientas de desarrollo. Descárgalos directamente desde sus portales oficiales:

1. **Java Development Kit (JDK 21):**
   * [Descargar Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) o [Adoptium OpenJDK 21](https://adoptium.net/)
2. **XAMPP (Servidor MySQL y phpMyAdmin):**
   * [Descargar XAMPP Oficial (Apache Friends)](https://www.apachefriends.org/es/index.html)
3. **IDE de Desarrollo (Opcional si se ejecuta desde consola/JAR):**
   * [Apache NetBeans 22](https://netbeans.apache.org/front/main/download/)
4. **Conector JDBC para MySQL:**
   * [Descargar MySQL Connector/J (Platform Independent)](https://dev.mysql.com/downloads/connector/j/) *(Seleccionar versión `.zip` o `.tar.gz` para extraer el archivo `.jar`)*.

---

## ⚙️ Guía de Instalación y Configuración

### Paso 1: Instalar e Iniciar MySQL con XAMPP
1. Ejecuta el instalador de **XAMPP** y asegúrate de marcar al menos los componentes **MySQL** y **phpMyAdmin**.
2. Abre el **XAMPP Control Panel**.
3. Haz clic en el botón **Start** en los módulos **Apache** y **MySQL** (ambos deben ponerse en verde).

---

### Paso 2: Crear la Base de Datos y Ejecutar el Script SQL
1. Abre tu navegador e ingresa a `http://localhost/phpmyadmin`.
2. Haz clic en la pestaña **SQL** en el menú superior.
3. Copia y pega el siguiente script completo y presiona **Continuar** (o *Go*) para crear la base de datos `sistema_restaurante.sql` y sus tablas:

```sql
CREATE DATABASE IF NOT EXISTS restaurante_db
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

USE restaurante_db;

-- 1. Tabla Cliente
CREATE TABLE IF NOT EXISTS Cliente (
    id_cliente VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) DEFAULT NULL,
    PRIMARY KEY (id_cliente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabla Mesa
CREATE TABLE IF NOT EXISTS Mesa (
    id_mesa VARCHAR(50) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    capacidad INT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    PRIMARY KEY (id_mesa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla Producto
CREATE TABLE IF NOT EXISTS Producto (
    id_producto VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    descripcion TEXT DEFAULT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    disponible TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabla Pedido
CREATE TABLE IF NOT EXISTS Pedido (
    id_pedido VARCHAR(50) NOT NULL,
    fecha DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    id_cliente VARCHAR(50) DEFAULT NULL,
    id_mesa VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (id_cliente) REFERENCES Cliente(id_cliente) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_pedido_mesa FOREIGN KEY (id_mesa) REFERENCES Mesa(id_mesa) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Tabla DetallePedido
CREATE TABLE IF NOT EXISTS DetallePedido (
    id_detalle INT AUTO_INCREMENT NOT NULL,
    id_pedido VARCHAR(50) NOT NULL,
    id_producto VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL,
    precio_aplicado DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_pedido FOREIGN KEY (id_pedido) REFERENCES Pedido(id_pedido) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES Producto(id_producto) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
