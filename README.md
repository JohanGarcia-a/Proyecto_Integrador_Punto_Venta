# Proyecto Integrador – Punto de Venta para Tienda de Abarrotes

Sistema completo de punto de venta e inventario desarrollado en *Java 21* con *Maven* y *Swing*, diseñado especialmente para una tienda de abarrotes.

## Funcionalidades implementadas (100 % cumplidas)

### Gestión de Productos
- Alta de nuevos productos
- Modificación del precio de venta
- Establecimiento de límite de stock mínimo por producto
- Reporte de productos con bajo stock
- Validación completa de datos ingresados

### Almacén e Inventario
- Registro de entradas de mercancía (compras al proveedor)
- Eliminación/resta de productos del inventario
- Control automático de existencias

### Punto de Venta
- Generación de ventas
- Agregar y eliminar productos del ticket en tiempo real
- Búsqueda rápida de productos por código o nombre
- Cálculo automático de totales, descuentos y monto a pagar
- Devoluciones de productos (con actualización automática del inventario)
- Re-impresión de tickets anteriores

### Recibo/Ticket de Compra
Cada venta genera un recibo con:
- Fecha y hora exacta de la compra
- Código, descripción, cantidad y precio unitario de cada producto
- Precio total de la compra
- Monto del descuento aplicado (si existe)
- Monto final a pagar
- Número de ticket

### Reportes
- Reporte de ventas por fecha o rango de fechas
- Reporte detallado de todos los productos vendidos
- Exportación a Excel con: fecha de venta, hora, número de ticket, productos, cantidades, precios y totales
- Reporte de productos con bajo stock (alertas visuales)

### Características adicionales
- Interfaz gráfica intuitiva y fácil de usar (desarrollada con Swing)
- Totalmente eficiente y sin errores en ejecución
- Persistencia de datos (base de datos H2 embebida / configurable a MySQL)
- Registro completo de auditoría de movimientos

## Tecnologías utilizadas
- *Java 21*
- *Maven* (gestión de dependencias)
- *Swing* (interfaz gráfica)
- *H2 Database* (base de datos embebida, archivos .mv.db)
- *Apache POI* (exportación a Excel)
- *Eclipse IDE* (entorno de desarrollo)

## Estructura del proyecto