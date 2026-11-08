# Guía de Desarrollo y Restricciones del Proyecto

**Asignatura:** Proyecto de Programación

**Entorno de Base de Datos:** MySQL

**Lenguaje / Entorno:** Java 21 (NetBeans 22)

## 1. Diagrama de Clases UML (Modelo de Referencia)

<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 1080" width="100%" height="100%" style="background-color: #0d1117; font-family: -apple-system, BlinkMacSystemFont, &#39;Segoe UI&#39;, Roboto, Helvetica, Arial, sans-serif;">
  <style>
    .title-box { fill: #161b22; stroke: #30363d; stroke-width: 2; rx: 8px; }
    .title-text { fill: #f0f6fc; font-size: 16px; font-weight: bold; text-anchor: middle; letter-spacing: 1px; }
    
    .class-box { fill: #161b22; stroke: #58a6ff; stroke-width: 2; rx: 6px; }
    .header-box { fill: #1f6feb; rx: 4px; }
    .class-name { fill: #ffffff; font-size: 16px; font-weight: bold; text-anchor: middle; }
    
    .section-title { fill: #8b949e; font-size: 12px; font-weight: bold; }
    .attribute-text { fill: #79c0ff; font-size: 13px; font-family: 'Courier New', Courier, monospace; }
    .method-text { fill: #d2a8ff; font-size: 13px; font-family: 'Courier New', Courier, monospace; }
    .line-divider { stroke: #30363d; stroke-width: 1.5; }
    
    .arrow-line { stroke: #8b949e; stroke-width: 2; }
    .arrow-head { fill: #8b949e; }
  </style>

  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" class="arrow-head" />
    </marker>
  </defs>

  <!-- BANNER TÍTULO -->
  <rect x="150" y="30" width="600" height="50" class="title-box" />
  <text x="450" y="62" class="title-text">DIAGRAMA DE CLASE UML - SISTEMA PEDIDOS RESTAURANTE</text>

  <!-- CLASE CLIENTE -->
  <g transform="translate(50, 140)">
    <rect x="0" y="0" width="240" height="230" class="class-box" />
    <path d="M 0 0 h 240 v 40 h -240 z" class="header-box" />
    <text x="120" y="26" class="class-name">Cliente</text>
    
    <text x="15" y="60" class="section-title">Atributos</text>
    <text x="15" y="82" class="attribute-text">- idCliente: String</text>
    <text x="15" y="104" class="attribute-text">- nombre: String</text>
    <text x="15" y="126" class="attribute-text">- telefono: String</text>
    
    <line x1="0" y1="140" x2="240" y2="140" class="line-divider" />
    
    <text x="15" y="160" class="section-title">Métodos</text>
    <text x="15" y="182" class="method-text">+ registrarCliente(): void</text>
    <text x="15" y="204" class="method-text">+ buscarCliente(): Cliente</text>
  </g>

  <!-- CLASE PEDIDO -->
  <g transform="translate(330, 140)">
    <rect x="0" y="0" width="240" height="270" class="class-box" />
    <path d="M 0 0 h 240 v 40 h -240 z" class="header-box" />
    <text x="120" y="26" class="class-name">Pedido</text>
    
    <text x="15" y="60" class="section-title">Atributos</text>
    <text x="15" y="82" class="attribute-text">- idPedido: String</text>
    <text x="15" y="104" class="attribute-text">- fecha: Date</text>
    <text x="15" y="126" class="attribute-text">- estado: String</text>
    <text x="15" y="148" class="attribute-text">- total: String</text>
    
    <line x1="0" y1="162" x2="240" y2="162" class="line-divider" />
    
    <text x="15" y="182" class="section-title">Métodos</text>
    <text x="15" y="204" class="method-text">+ crearPedido(): void</text>
    <text x="15" y="226" class="method-text">+ calcularTotal(): double</text>
  </g>

  <!-- CLASE MESA -->
  <g transform="translate(610, 140)">
    <rect x="0" y="0" width="240" height="250" class="class-box" />
    <path d="M 0 0 h 240 v 40 h -240 z" class="header-box" />
    <text x="120" y="26" class="class-name">Mesa</text>
    
    <text x="15" y="60" class="section-title">Atributos</text>
    <text x="15" y="82" class="attribute-text">- idMesa: String</text>
    <text x="15" y="104" class="attribute-text">- numero: String</text>
    <text x="15" y="126" class="attribute-text">- capacidad: int</text>
    <text x="15" y="148" class="attribute-text">- estado: String</text>
    
    <line x1="0" y1="162" x2="240" y2="162" class="line-divider" />
    
    <text x="15" y="182" class="section-title">Métodos</text>
    <text x="15" y="204" class="method-text">+ cambiarEstado(): void</text>
    <text x="15" y="226" class="method-text">+ consultarMesas(): void</text>
  </g>

  <!-- CLASE DETALLEPEDIDO -->
  <g transform="translate(330, 490)">
    <rect x="0" y="0" width="240" height="200" class="class-box" />
    <path d="M 0 0 h 240 v 40 h -240 z" class="header-box" />
    <text x="120" y="26" class="class-name">DetallePedido</text>
    
    <text x="15" y="60" class="section-title">Atributos</text>
    <text x="15" y="82" class="attribute-text">- cantidad: int</text>
    <text x="15" y="104" class="attribute-text">- precioAplicado: double</text>
    
    <line x1="0" y1="120" x2="240" y2="120" class="line-divider" />
    
    <text x="15" y="140" class="section-title">Métodos</text>
    <text x="15" y="162" class="method-text">+ calcularSubtotal(): double</text>
  </g>

  <!-- CLASE PRODUCTO -->
  <g transform="translate(330, 770)">
    <rect x="0" y="0" width="240" height="250" class="class-box" />
    <path d="M 0 0 h 240 v 40 h -240 z" class="header-box" />
    <text x="120" y="26" class="class-name">Producto</text>
    
    <text x="15" y="60" class="section-title">Atributos</text>
    <text x="15" y="82" class="attribute-text">- idProducto: String</text>
    <text x="15" y="104" class="attribute-text">- nombre: String</text>
    <text x="15" y="126" class="attribute-text">- precioUnitario: double</text>
    <text x="15" y="148" class="attribute-text">- disponible: boolean</text>
    
    <line x1="0" y1="162" x2="240" y2="162" class="line-divider" />
    
    <text x="15" y="182" class="section-title">Métodos</text>
    <text x="15" y="204" class="method-text">+ registrarProductos(): void</text>
    <text x="15" y="226" class="method-text">+ buscarProducto(): Producto</text>
  </g>

  <!-- RELACIONES (FLECHAS) -->
  <line x1="330" y1="240" x2="290" y2="240" class="arrow-line" marker-end="url(#arrow)" />
  <line x1="570" y1="240" x2="610" y2="240" class="arrow-line" marker-end="url(#arrow)" />
  <line x1="450" y1="410" x2="450" y2="490" class="arrow-line" marker-end="url(#arrow)" />
  <line x1="450" y1="690" x2="450" y2="770" class="arrow-line" marker-end="url(#arrow)" />
</svg>

### Vista de Texto / Estructura Tabular

Si la plataforma donde abres el archivo `.md` no soporta bloques de código de tipo `mermaid`, puedes guiarte por esta matriz de referencia:

| Clase | Atributos | Métodos | Relaciones | 
| ----- | ----- | ----- | ----- | 
| **Cliente** | `- idCliente: String`  `- nombre: String`  `- telefono: String` | `+ registrarCliente(): void`  `+ buscarCliente(): Cliente` | Referenciado por `Pedido` (0..1) | 
| **Mesa** | `- idMesa: String`  `- numero: String`  `- capacidad: int`  `- estado: String` | `+ cambiarEstado(): void`  `+ consultarMesas(): void` | Referenciada por `Pedido` (0..1) | 
| **Producto** | `- idProducto: String`  `- nombre: String`  `- precioUnitario: double`  `- disponible: boolean` | `+ registrarProductos(): void`  `+ buscarProducto(): Producto` | Referenciado por `DetallePedido` (1) | 
| **Pedido** | `- idPedido: String`  `- fecha: Date`  `- estado: String`  `- total: String` | `+ crearPedido(): void`  `+ calcularTotal(): double` | Apunta a `Cliente` (0..1), `Mesa` (0..1) y `DetallePedido` (1..\*) | 
| **DetallePedido** | `- cantidad: int`  `- precioAplicado: double` | `+ calcularSubtotal(): double` | Pertenece a `Pedido` (1) y apunta a `Producto` (1) | 

## 2. Mapeo Completo de Clases, Atributos y Métodos en Java

A continuación se detalla la estructura exacta de atributos y métodos requeridos para la capa **Modelo / Servicios** en Java (`com.restaurante.modelo`), respetando la visibilidad (`-` privado, `+` público) del diagrama UML.

### 2.1 Clase `Cliente`

* **Atributos:**

  * `- idCliente: String`

  * `- nombre: String`

  * `- telefono: String`

* **Métodos Obligatorios:**

  * `+ registrarCliente(): void`

  * `+ buscarCliente(): Cliente`

### 2.2 Clase `Mesa`

* **Atributos:**

  * `- idMesa: String`

  * `- numero: String`

  * `- capacidad: int`

  * `- estado: String`

* **Métodos Obligatorios:**

  * `+ cambiarEstado(): void`

  * `+ consultarMesas(): void`

### 2.3 Clase `Producto`

* **Atributos:**

  * `- idProducto: String`

  * `- nombre: String`

  * `- precioUnitario: double`

  * `- disponible: boolean`

* **Métodos Obligatorios:**

  * `+ registrarProductos(): void`

  * `+ buscarProducto(): Producto`

### 2.4 Clase `Pedido`

* **Atributos:**

  * `- idPedido: String`

  * `- fecha: java.util.Date`

  * `- estado: String`

  * `- total: String`

  * `- cliente: Cliente` *(Asociación UML)*

  * `- mesa: Mesa` *(Asociación UML)*

* **Métodos Obligatorios:**

  * `+ crearPedido(): void`

  * `+ calcularTotal(): double`

### 2.5 Clase `DetallePedido`

* **Atributos:**

  * `- cantidad: int`

  * `- precioAplicado: double`

  * `- producto: Producto` *(Asociación UML)*

* **Métodos Obligatorios:**

  * `+ calcularSubtotal(): double`

## 3. Adaptación Técnica para la Base de Datos (MySQL)

Al realizar el mapeo relacional en **MySQL**, se aplican los siguientes criterios para mantener la consistencia con las clases:

1. **Atributo `total` en la Tabla `Pedido`:** En la base de datos se almacena como `DECIMAL(10,2)` para permitir que el método `calcularTotal(): double` opere con precisión contable.

2. **Claves Foráneas (FK):**

   * Tabla `Pedido`: contiene `id_cliente` (hacia `Cliente`) e `id_mesa` (hacia `Mesa`), ambas opcionales (`NULLABLE`).

   * Tabla `DetallePedido`: contiene `id_pedido` (hacia `Pedido`) e `id_producto` (hacia `Producto`).

3. **Clave Primaria de `DetallePedido`:** Se añade la clave primaria sintética `id_detalle INT AUTO_INCREMENT PRIMARY KEY`.

4. **Campo `disponible`:** En MySQL se mapea a `TINYINT(1)` (1 = `true`, 0 = `false`).

## 4. Instrucciones Generales de Desarrollo

1. **Estructura del Proyecto:**

   * Desarrollar en **VS Code** o **NetBeans 22** con **JDK 21**.

   * Paquetes de trabajo:

     * `com.restaurante.modelo` (Contiene las 5 clases UML y sus métodos).

     * `com.restaurante.dao` (Persistencia JDBC hacia MySQL).

     * `com.restaurante.controlador`

     * `com.restaurante.vista` (Interfaz Swing).

     * `com.restaurante.config` (Conexión a base de datos).

2. **Reglas y Validaciones:**

   * Cantidades e importes deben ser mayores que cero.

   * No se pueden agregar productos con `disponible = false`.

   * Los pedidos en estado *Cerrado* o *Completado* son inmutables.

   * Cada modificación en `DetallePedido` debe invocar automáticamente la actualización de `calcularTotal()`.

3. **Validación Final:**

   * Probar el proyecto con `Clean and Build` en NetBeans 22 antes de crear la carpeta de entrega final `Proyecto_GTGP`.
