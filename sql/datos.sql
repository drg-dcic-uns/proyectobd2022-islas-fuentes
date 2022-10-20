#-------------------------------------------------------------------------
# Carga de datos de Prueba


INSERT INTO vuelos.ubicaciones VALUES ("Italia", "Lacio", "Roma", 10);
INSERT INTO vuelos.ubicaciones VALUES ("Estados Unidos", "California", "Los Angeles", -3);
INSERT INTO vuelos.ubicaciones VALUES ("Argentina", "Buenos Aires", "Ezeiza", 4);

INSERT INTO vuelos.aeropuertos VALUES (1, "Aeropuerto de Roma", 123, "dir 1", "Italia", "Lacio", "Roma");
INSERT INTO vuelos.aeropuertos VALUES (2, "Los Angeles International Airport", 456, "dir 2", "Estados Unidos", "California", "Los Angeles");
INSERT INTO vuelos.aeropuertos VALUES (3, "Aeropuerto Internacional de Ezeiza", 789, "dir 3", "Argentina", "Buenos Aires", "Ezeiza");

INSERT INTO vuelos.modelos_avion VALUES ('737','Boeing','4','168');
INSERT INTO vuelos.modelos_avion VALUES ('A220','Airbus','4','130');
INSERT INTO vuelos.modelos_avion VALUES ('777','Boeing','5','242');

INSERT INTO vuelos.clases VALUES ('ejecutiva','0.15');
INSERT INTO vuelos.clases VALUES ('turista','0.8');
INSERT INTO vuelos.clases VALUES ('primera','0.5');

INSERT INTO vuelos.empleados VALUES (1, "empleado1", "DNI", 123, "ap1", "nom1", "dir1", 123);

INSERT INTO vuelos.pasajeros VALUES ('DNI',40293100, 'Perez', 'Jose', 'Avenida Falsa 123', '+54 555 5555', 'Argentina');
INSERT INTO vuelos.pasajeros VALUES ('DNI',30492192, 'Perez', 'Martin', 'Cortada dudosa 123', '+54 444 4444', 'Argentina');
INSERT INTO vuelos.pasajeros VALUES ('DNI',20930102, 'Antic', 'Marcela', 'Calle Real 123', '+54 444 4434', 'Bolivia');

INSERT INTO vuelos.vuelos_programados(numero, aeropuerto_salida, aeropuerto_llegada) SELECT '630' , S.codigo, L.codigo FROM vuelos.aeropuertos S, vuelos.aeropuertos L WHERE S.codigo = '1' and L.codigo = '2';
INSERT INTO vuelos.vuelos_programados(numero, aeropuerto_salida, aeropuerto_llegada) SELECT '710' , S.codigo, L.codigo FROM vuelos.aeropuertos S, vuelos.aeropuertos L WHERE S.codigo = '2' and L.codigo = '3';
INSERT INTO vuelos.vuelos_programados(numero, aeropuerto_salida, aeropuerto_llegada) SELECT '330' , S.codigo, L.codigo FROM vuelos.aeropuertos S, vuelos.aeropuertos L WHERE S.codigo = '3' and L.codigo = '1';

INSERT INTO vuelos.salidas(vuelo, dia, hora_sale, hora_llega, modelo_avion) SELECT numero, 'Do', '15:30', '19:30', modelo FROM vuelos.vuelos_programados , vuelos.modelos_avion WHERE numero = '630' AND modelo = '737';
INSERT INTO vuelos.salidas(vuelo, dia, hora_sale, hora_llega, modelo_avion) SELECT numero, 'Do', '16:30', '20:30', modelo FROM vuelos.vuelos_programados , vuelos.modelos_avion WHERE numero = '710' AND modelo = 'A220';
INSERT INTO vuelos.salidas(vuelo, dia, hora_sale, hora_llega, modelo_avion) SELECT numero, 'Do', '06:30', '12:00', modelo FROM vuelos.vuelos_programados , vuelos.modelos_avion WHERE numero = '330' AND modelo = '777';

INSERT INTO vuelos.instancias_vuelo (vuelo, fecha, dia, estado) SELECT vuelo, "1942-11-15", dia, "espera" FROM vuelos.salidas WHERE dia = 'Do' AND vuelo = '630';
INSERT INTO vuelos.instancias_vuelo (vuelo, fecha, dia, estado) SELECT vuelo, "1942-11-15", dia, "espera" FROM vuelos.salidas WHERE dia = 'Do' AND vuelo = '710';
INSERT INTO vuelos.instancias_vuelo (vuelo, fecha, dia, estado) SELECT vuelo, "1942-11-15", dia, "finalizado" FROM vuelos.salidas WHERE dia = 'Do' AND vuelo = '330';

INSERT INTO vuelos.comodidades VALUES ('100' , 'Diseño en los asientos y servicio con catering, vajillas , mantas y kits de viaje personalizado');
INSERT INTO vuelos.comodidades VALUES ('101' , 'Servicio diferencial con atencion dedicada por la tripulacion' );
INSERT INTO vuelos.comodidades VALUES ('102' , 'Cabina clasica en toda la flota');

INSERT INTO vuelos.posee (clase,comodidad) SELECT nombre, codigo FROM vuelos.clases , vuelos.comodidades WHERE nombre = 'turista' AND codigo = '102';
INSERT INTO vuelos.posee (clase,comodidad) SELECT nombre, codigo FROM vuelos.clases , vuelos.comodidades WHERE nombre = 'ejecutiva' AND codigo = '101';
INSERT INTO vuelos.posee (clase,comodidad) SELECT nombre, codigo FROM vuelos.clases , vuelos.comodidades WHERE nombre = 'primera' AND codigo = '100';

INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '9000.90', '90' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '710' AND salidas.dia = 'Do' AND clases.nombre = 'turista';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '15000', '15' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '710' AND salidas.dia = 'Do' AND clases.nombre = 'ejecutiva';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '20000', '10' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '710' AND salidas.dia = 'Do' AND clases.nombre = 'primera';

INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '4500', '90' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '630' AND salidas.dia = 'Do' AND clases.nombre = 'turista';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '9000', '15' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '630' AND salidas.dia = 'Do' AND clases.nombre = 'ejecutiva';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '15000', '10' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '630' AND salidas.dia = 'Do' AND clases.nombre = 'primera';

INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '32090', '160' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '330' AND salidas.dia = 'Do' AND clases.nombre = 'turista';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '45900', '0' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '330' AND salidas.dia = 'Do' AND clases.nombre = 'ejecutiva';
INSERT INTO vuelos.brinda (vuelo,dia,clase,precio,cant_asientos) SELECT vuelo, dia, nombre, '90000', '5' FROM vuelos.salidas , vuelos.clases WHERE salidas.vuelo = '330' AND salidas.dia = 'Do' AND clases.nombre = 'primera';

INSERT INTO vuelos.reservas(numero, fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT 4,'1942-11-10', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '40293100' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-01', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '30492192' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-11', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '20930102' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-01', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '30492192' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-01', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '30492192' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-01', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '30492192' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reservas(fecha, vencimiento, estado , doc_tipo, doc_nro, legajo) 
SELECT '1942-11-01', '1942-11-15', 'Pago', pasajeros.doc_tipo, pasajeros. doc_nro , legajo 
FROM vuelos.pasajeros , vuelos.empleados 
WHERE pasajeros.doc_nro = '30492192' AND pasajeros.doc_tipo = 'DNI' AND legajo = '1';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '4' AND instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '5' AND instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '6' AND instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '7' AND instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '8' AND instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '9' AND instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'ejecutiva';

INSERT INTO vuelos.reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase) 
SELECT reservas.numero, instancias_vuelo.vuelo, instancias_vuelo.fecha, clases.nombre 
FROM vuelos.reservas, vuelos.instancias_vuelo, vuelos.clases 
WHERE reservas.numero = '10' AND instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'ejecutiva';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 3
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 0
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'ejecutiva';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 0
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '630' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'primera';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 2
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'turista';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 2
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'ejecutiva';

INSERT INTO vuelos.asientos_reservados (vuelo, fecha, clase, cantidad) 
SELECT vuelo, fecha, nombre, 0
FROM vuelos.instancias_vuelo, vuelos.clases 
WHERE instancias_vuelo.vuelo = '710' AND instancias_vuelo.fecha = '1942-11-15' AND clases.nombre = 'primera';

