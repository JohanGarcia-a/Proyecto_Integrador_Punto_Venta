# **Proyecto Integrador: Sistema de Punto de Venta (POS)

Sistema de gestión comercial de escritorio desarrollado en **Java (Swing)** con base de datos **SQL Server**. Diseñado para optimizar el control de ventas, inventario, compras y flujo de efectivo en pequeños y medianos comercios.

---

## **Características Principales

###  Módulo de Ventas
* **Procesamiento Rápido:** Búsqueda de productos por código, nombre o ID.
* **Cálculos Automáticos:** Subtotal, Descuentos e Impuestos (IVA 16%) en tiempo real.
* **Emisión de Tickets:** Generación de comprobantes de venta.
* **Validación de Stock:** Control automático para no vender productos sin existencia.

### Gestión de Inventario (Almacén)
* **Semáforo de Stock:** Alertas visuales en la tabla de productos:
    * **Rojo:** Agotado (0).
    * **Amarillo:** Stock Bajo (Requiere reorden).
    * **Verde:** Stock Saludable.
* **CRUD Completo:** Gestión de Productos, Categorías y Proveedores.
* **Auditoría:** Historial de entradas manuales de mercancía.

### Control de Caja
* **Corte Ciego:** Seguridad financiera donde el cajero ingresa el monto físico sin ver el esperado por el sistema.
* **Cálculo de Diferencias:** Detección automática de sobrantes o faltantes.
* **Movimientos Manuales:** Registro de Ingresos y Egresos de efectivo (gastos, retiros).

### Compras y Pedidos
* Generación de Órdenes de Compra a Proveedores.
* Recepción de mercancía con actualización automática del inventario.

### Reportes (Business Intelligence)
* Reportes exportables en **PDF** (JasperReports) y visualización en tablas.
* Historial de Ventas, Cortes de Caja e Inventario Valorado.

### Seguridad
* Autenticación de usuarios (Login).
* **Control de Roles:** (Admin, Cajero, Supervisor) con restricción de acceso a módulos sensibles.

---

## Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 21).
* **Interfaz Gráfica:** Java Swing (Diseño modular con JPanels).
* **Base de Datos:** Microsoft SQL Server.
* **Patrón de Arquitectura:** MVC (Modelo-Vista-Controlador).
* **Acceso a Datos:** JDBC con Patrón DAO (Data Access Object).
* **Reportes:** Librería JasperReports.

