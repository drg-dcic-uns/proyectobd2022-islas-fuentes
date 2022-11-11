# Creo de la Base de Datos
CREATE DATABASE vuelos;

# selecciono la base de datos sobre la cual voy a hacer modificaciones
USE vuelos;

#-------------------------------------------------------------------------
# Creación Tablas para las entidades

CREATE TABLE ubicaciones (
  pais VARCHAR(20) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  ciudad VARCHAR(20) NOT NULL,
  huso SMALLINT(12) NOT NULL CHECK (huso BETWEEN -12 AND 12),
  
  CONSTRAINT pk_ubicaciones
  PRIMARY KEY (pais,estado,ciudad)
) ENGINE=InnoDB;

CREATE TABLE aeropuertos (
  codigo VARCHAR(45) NOT NULL,
  nombre VARCHAR(40) NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  direccion VARCHAR(30) NOT NULL,
  pais VARCHAR(20) NOT NULL,
  estado VARCHAR(20) NOT NULL,
  ciudad VARCHAR(20) NOT NULL,
  
  CONSTRAINT pk_aeropuerto
  PRIMARY KEY (codigo),
  
  CONSTRAINT FK_aeropuerto_ubicaciones
  FOREIGN KEY (pais,estado,ciudad) REFERENCES ubicaciones (pais,estado,ciudad)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE vuelos_programados (
  numero VARCHAR(10) NOT NULL,
  aeropuerto_salida VARCHAR(45) NOT NULL,
  aeropuerto_llegada VARCHAR(45) NOT NULL,
  
  CONSTRAINT pk_vuelos_programados
  PRIMARY KEY (numero),

  CONSTRAINT fk_vuelos_programados_aeropuerto_salida
  FOREIGN KEY (aeropuerto_salida) REFERENCES aeropuertos (codigo),

  CONSTRAINT fk_vuelos_programados_aeropuerto_llegada
  FOREIGN KEY (aeropuerto_llegada) REFERENCES aeropuertos(codigo)

) ENGINE=InnoDB;

CREATE TABLE modelos_avion (
  modelo VARCHAR(20) NOT NULL,
  fabricante VARCHAR(20) NOT NULL,
  cabinas INT unsigned NOT NULL,
  cant_asientos INT unsigned NOT NULL,

  CONSTRAINT pk_modelo_avion
  PRIMARY KEY (modelo)

) ENGINE=InnoDB;

CREATE TABLE salidas (
  vuelo VARCHAR(10) NOT NULL,
  dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa') NOT NULL, 
  hora_sale TIME NOT NULL,
  hora_llega TIME NOT NULL,
  modelo_avion VARCHAR(20) NOT NULL,
  
  CONSTRAINT pk_salidas
  PRIMARY KEY (vuelo,dia),

  CONSTRAINT fk_salidas_modelo_avion
  FOREIGN KEY (modelo_avion) REFERENCES modelos_avion(modelo),

  CONSTRAINT fk_salidas_vuelo
  FOREIGN KEY (vuelo) REFERENCES vuelos_programados(numero)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE instancias_vuelo (
  vuelo VARCHAR(10) NOT NULL,
  fecha DATE NOT NULL,
  dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa') NOT NULL,
  estado VARCHAR(15) ,

  CONSTRAINT pk_instacia_vuelo
  PRIMARY KEY (vuelo,fecha),
  
  CONSTRAINT FK_instancias_vuelo
  FOREIGN KEY (vuelo,dia) REFERENCES salidas (vuelo,dia) 
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE clases (
  nombre VARCHAR(20) NOT NULL,
  porcentaje DECIMAL(2,2) UNSIGNED NOT NULL,

  CONSTRAINT pk_clases
  PRIMARY KEY (nombre)
) ENGINE=InnoDB;

CREATE TABLE comodidades (
  codigo INT UNSIGNED NOT NULL,
  descripcion TEXT NOT NULL,

  CONSTRAINT pk_comodidades
  PRIMARY KEY (codigo)
) ENGINE=InnoDB;

CREATE TABLE pasajeros (
  doc_tipo VARCHAR(15) NOT NULL, 
  doc_nro INT(15) UNSIGNED NOT NULL, 
  apellido VARCHAR(20) NOT NULL, 
  nombre VARCHAR(20) NOT NULL, 
  direccion VARCHAR(40) NOT NULL, 
  telefono VARCHAR(15) NOT NULL, 
  nacionalidad VARCHAR(20) NOT NULL,

  CONSTRAINT pk_pasajeros
  PRIMARY KEY (doc_tipo,doc_nro)
) ENGINE=InnoDB;

CREATE TABLE empleados (
  legajo INT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  password VARCHAR(32) NOT NULL,
  doc_tipo VARCHAR(15) NOT NULL, 
  doc_nro INT(15) UNSIGNED NOT NULL, 
  apellido VARCHAR(20) NOT NULL, 
  nombre VARCHAR(20) NOT NULL,
  direccion VARCHAR(40) NOT NULL, 
  telefono VARCHAR(15) NOT NULL,

  CONSTRAINT pk_empleados
  PRIMARY KEY (legajo)
) ENGINE=InnoDB;

CREATE TABLE reservas (
  numero INT(20) UNSIGNED NOT NULL AUTO_INCREMENT, 
  fecha DATE NOT NULL, 
  vencimiento DATE NOT NULL, 
  estado VARCHAR(15) NOT NULL, 
  doc_tipo VARCHAR(15) NOT NULL, 
  doc_nro INT(15) UNSIGNED NOT NULL, 
  legajo INT(20) UNSIGNED NOT NULL,

  CONSTRAINT pk_reservas
  PRIMARY KEY (numero),

  CONSTRAINT fk_reservas_pasajero
  FOREIGN KEY (doc_tipo,doc_nro) REFERENCES pasajeros (doc_tipo, doc_nro), 

  CONSTRAINT fk_reservas_empleado
  FOREIGN KEY (legajo) REFERENCES empleados(legajo)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creación Tablas para las relaciones

CREATE TABLE brinda (
  vuelo VARCHAR(10) NOT NULL, 
  dia ENUM('Do','Lu','Ma','Mi','Ju','Vi','Sa') NOT NULL, 
  clase VARCHAR(20) NOT NULL, 
  precio DECIMAL(7,2) UNSIGNED NOT NULL,
  cant_asientos INT(15) UNSIGNED NOT NULL,

  CONSTRAINT pk_brinda
  PRIMARY KEY (vuelo,dia,clase),

  CONSTRAINT fk_brinda_vuelo
  FOREIGN KEY (vuelo,dia) REFERENCES salidas (vuelo, dia),

  CONSTRAINT fk_brinda_clase
  FOREIGN KEY (clase) REFERENCES clases (nombre)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE posee (
  clase VARCHAR(20) NOT NULL, 
  comodidad INT UNSIGNED NOT NULL,

  CONSTRAINT pk_posee
  PRIMARY KEY (clase,comodidad),

  CONSTRAINT fk_posee_nombre_clase
  FOREIGN KEY (clase) REFERENCES clases (nombre),

  CONSTRAINT fk_posee_comodidad
  FOREIGN KEY (comodidad) REFERENCES comodidades (codigo)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE reserva_vuelo_clase (
  numero INT(20) UNSIGNED NOT NULL, 
  vuelo VARCHAR(10) NOT NULL, 
  fecha_vuelo DATE NOT NULL, 
  clase VARCHAR(20) NOT NULL,

  CONSTRAINT pk_reserva_vuelo_clase
  PRIMARY KEY (numero,vuelo,fecha_vuelo),

  CONSTRAINT fk_reserva_vuelo_clase_reserva
  FOREIGN KEY (numero) REFERENCES reservas (numero),

  CONSTRAINT fk_reserva_vuelo_clase_instancia_vuelo
  FOREIGN KEY (vuelo,fecha_vuelo) REFERENCES instancias_vuelo(vuelo,fecha),

  CONSTRAINT fk_reserva_vuelo_clase_nombre_clase
  FOREIGN KEY (clase) REFERENCES clases (nombre)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE asientos_reservados (
  vuelo VARCHAR(10) NOT NULL, 
  fecha DATE NOT NULL, 
  clase VARCHAR(20) NOT NULL, 
  cantidad INT(15) UNSIGNED NOT NULL,

  CONSTRAINT pk_asientos_reservados
  PRIMARY KEY (vuelo,fecha,clase),

  CONSTRAINT fk_asientos_reservados_instancia_vuelo
  FOREIGN KEY (vuelo,fecha) REFERENCES instancias_vuelo(vuelo,fecha),

  CONSTRAINT fk_asientos_reservados_nombre_clase
  FOREIGN KEY (clase) REFERENCES clases (nombre)
  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

#-------------------------------------------------------------------------
# Creación de vistas 
# vuelos_disponibles = contiene informacion sobre cada instancia de vuelo

   CREATE VIEW vuelos_disponibles AS
    SELECT sub_consulta1.*, sub_consulta2.clase, round(sub_consulta2.asientos_disponibles, 0) as asientos_disponibles,
		   sub_consulta2.precio 
	FROM
		
	   (SELECT vp.numero as nro_vuelo, ma.modelo, iv.fecha, s.dia as dia_sale, s.hora_sale, s.hora_llega, 
			CASE WHEN (s.hora_sale > s.hora_llega) THEN (TIME(24 + s.hora_llega + s.hora_sale))
			ELSE (TIME(s.hora_llega - s.hora_sale)) END as tiempo_estimado,
			vp.aeropuerto_salida as codigo_aero_sale, a_sale.nombre as nombre_aero_sale, a_sale.ciudad as ciudad_sale,
			a_sale.estado as estado_sale, a_sale.pais as pais_sale, 
			vp.aeropuerto_llegada as codigo_aero_llega, a_llega.nombre as nombre_aero_llega, a_llega.ciudad as ciudad_llega,
			a_llega.estado as estado_llega, a_llega.pais as pais_llega
		FROM 
			(vuelos_programados vp INNER JOIN salidas s INNER JOIN instancias_vuelo iv INNER JOIN modelos_avion ma
			INNER JOIN aeropuertos a_sale INNER JOIN aeropuertos a_llega)
		WHERE (vp.numero = s.vuelo AND s.vuelo = iv.vuelo AND s.modelo_avion = ma.modelo 
			  AND a_sale.codigo = vp.aeropuerto_salida AND a_llega.codigo = vp.aeropuerto_llegada)
		) sub_consulta1
		
	INNER JOIN 
		(SELECT iv.fecha, s.dia, s.vuelo as vuelo, c.nombre as clase, 
				(b.cant_asientos + c.porcentaje * b.cant_asientos) - ar.cantidad as asientos_disponibles,
				b.precio as precio 
		FROM 
			clases c INNER JOIN salidas s INNER JOIN asientos_reservados ar INNER JOIN brinda b INNER JOIN instancias_vuelo iv
		ON c.nombre = ar.clase AND iv.fecha = ar.fecha AND iv.dia = s.dia AND ar.vuelo = s.vuelo AND ar.vuelo = b.vuelo 
		   AND ar.clase = b.clase AND s.dia = b.dia
		) sub_consulta2
		
	ON sub_consulta1.nro_vuelo = sub_consulta2.vuelo AND sub_consulta1.dia_sale = sub_consulta2.dia
	   AND sub_consulta1.fecha = sub_consulta2.fecha;

# -------------------------------------------------------------------------
# Creacion de procedimientos

USE vuelos;
DELIMITER ! 
CREATE PROCEDURE reservaSoloIda (IN vuelo_ida VARCHAR(10), IN fecha_ida DATE, IN nombre_clase VARCHAR(20), IN tipo_doc VARCHAR(15), IN nro_doc INT(15), IN nro_legajo INT(20))
  BEGIN

    # declaracion de variables
    DECLARE cant_asientos_ida INT(15);
    DECLARE cant_reservados INT(15);
    DECLARE dia_ida VARCHAR(2);
    DECLARE estado_reserva VARCHAR(15);
    DECLARE vencimiento_reserva DATE;
    DECLARE nro_reserva INT(20); 

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
      BEGIN
        SELECT 'SQLEXCEPTION!, transaccion abordata' AS resultado;
        ROLLBACK;
      END;
    START TRANSACTION;
    #Chequear que exista la instancia de vuelos
    IF EXISTS (SELECT * FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_ida AND fecha = fecha_ida) THEN

      SELECT dia INTO dia_ida
      FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_ida AND fecha = fecha_ida;

      #Chequear que esa instancia brinde la clase deseada
      IF EXISTS (SELECT * FROM vuelos.brinda WHERE vuelo = vuelo_ida AND clase = nombre_clase) THEN
        
        SELECT cant_asientos INTO cant_asientos_ida
        FROM brinda WHERE vuelo = vuelo_ida AND clase = nombre_clase AND dia = dia_ida FOR UPDATE;

        #Chequear que halla asientos asientos disponibles para esa clase
        IF cant_asientos_ida > 0 THEN
          SELECT cantidad into cant_reservados
          FROM asientos_reservados WHERE vuelo = vuelo_ida AND fecha = fecha_ida AND clase = nombre_clase FOR UPDATE;
          IF cant_asientos_ida < cant_reservados THEN
            SET estado_reserva = 'En Espera';
          ELSE
            SET estado_reserva = 'Confirmada';
          END IF;

          #Actualizar BD
          SET vencimiento_reserva = DATE_SUB(fecha_ida, INTERVAL 15 DAY) ;

          INSERT INTO reservas(doc_tipo, doc_nro, legajo, fecha, vencimiento, estado)
          VALUES (tipo_doc, nro_doc, nro_legajo, fecha_ida,vencimiento_reserva,estado_reserva);

          SELECT numero INTO nro_reserva
          FROM reservas WHERE numero = LAST_INSERT_ID();

          INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase)
          VALUES (nro_reserva, vuelo_ida, fecha_ida, nombre_clase);

          SELECT nro_reserva AS numero_reserva;
        ELSE
          SELECT 'Error: no hay asientos disponibles' AS resultado;
        END IF;
      ELSE
        SELECT 'Error: el vuelo seleccionado no brinda la clase solicitada' AS resultado;
      END IF;
    ELSE
      SELECT 'Error: no existe la instancia del vuelo seleccionado' AS resultado;
    END IF;

    COMMIT;

  END; !
DELIMITER ;

USE vuelos;
DELIMITER ! 
CREATE PROCEDURE reservaIdaVuelta(IN vuelo_ida VARCHAR(10), IN fecha_ida DATE, IN nombre_clase_ida VARCHAR(20),IN vuelo_vuelta VARCHAR(10), IN fecha_vuelta DATE, IN nombre_clase_vuelta VARCHAR(20) , IN tipo_doc VARCHAR(15), IN nro_doc INT(15), IN nro_legajo INT(20))
  BEGIN

    # declaracion de variables
    DECLARE cant_asientos_ida INT(15);
    DECLARE cant_asientos_vuelta INT(15);
    DECLARE cant_reservados_ida INT(15);
    DECLARE cant_reservados_vuelta INT(15);
    DECLARE dia_ida VARCHAR(2);
    DECLARE dia_vuelta VARCHAR(2);
    DECLARE estado_reserva VARCHAR(15);
    DECLARE vencimiento_reserva DATE;
    DECLARE nro_reserva INT(20); 

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
      BEGIN
        SELECT 'SQLEXCEPTION!, transaccion abordata' AS resultado;
        ROLLBACK;
      END;
    START TRANSACTION;

    #Chequear que existan las instancias de vuelos
    IF EXISTS (SELECT * FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_ida AND fecha = fecha_ida) AND
      EXISTS (SELECT * FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_vuelta AND fecha = fecha_vuelta) THEN

      SELECT dia INTO dia_ida
      FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_ida AND fecha = fecha_ida;
      
      SELECT dia INTO dia_vuelta
      FROM vuelos.instancias_vuelo WHERE vuelo = vuelo_vuelta AND fecha = fecha_vuelta;

      #Chequear que ambas instancias brinden la clase deseada
      IF EXISTS (SELECT * FROM vuelos.brinda WHERE vuelo = vuelo_ida AND clase = nombre_clase_ida) AND
        EXISTS (SELECT * FROM vuelos.brinda WHERE vuelo = vuelo_vuelta AND clase = nombre_clase_vuelta) THEN
        
        SELECT cant_asientos INTO cant_asientos_ida
        FROM brinda WHERE vuelo = vuelo_ida AND clase = nombre_clase_ida AND dia = dia_ida FOR UPDATE;

        SELECT cant_asientos INTO cant_asientos_vuelta
        FROM brinda WHERE vuelo = vuelo_ida AND clase = nombre_clase_ida AND dia = dia_ida FOR UPDATE;

        #Chequear que halla asientos asientos disponibles para esa clase
        IF cant_asientos_ida > 0 AND cant_asientos_vuelta THEN
          SELECT cantidad into cant_reservados_ida
          FROM asientos_reservados WHERE vuelo = vuelo_ida AND fecha = fecha_ida AND clase = nombre_clase_ida FOR UPDATE;

          SELECT cantidad into cant_reservados_vuelta
          FROM asientos_reservados WHERE vuelo = vuelo_vuelta AND fecha = fecha_vuelta AND clase = nombre_clase_vuelta FOR UPDATE;

          IF cant_asientos_ida < cant_reservados_ida OR cant_asientos_vuelta < cant_asientos_vuelta < cant_reservados_vuelta THEN
            SET estado_reserva = 'En Espera';
          ELSE
            SET estado_reserva = 'Confirmada';
          END IF;

          #Actualizar BD
          SET vencimiento_reserva = DATE_SUB(fecha_ida, INTERVAL 15 DAY) ;

          INSERT INTO reservas(doc_tipo, doc_nro, legajo, fecha, vencimiento, estado)
          VALUES (tipo_doc, nro_doc, nro_legajo, fecha_ida,vencimiento_reserva,estado_reserva);

          SELECT numero INTO nro_reserva
          FROM reservas WHERE numero = LAST_INSERT_ID();

          INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase)
          VALUES (nro_reserva, vuelo_ida, fecha_ida, nombre_clase_ida);

          INSERT INTO reserva_vuelo_clase(numero, vuelo, fecha_vuelo, clase)
          VALUES (nro_reserva, vuelo_vuelta, fecha_vuelta, nombre_clase_vuelta);

          SELECT nro_reserva AS numero_reserva;
        ELSE
          SELECT 'Error: no hay asientos disponibles en alguno o ambos vuelos' AS resultado;
        END IF;
      ELSE
        SELECT 'Error: alguno o ambos de los vuelos no brinda la clase solicitada' AS resultado;
      END IF;
    ELSE
      SELECT 'Error: alguna o ambas instancias de vuelo no existen' AS resultado;
    END IF;

    COMMIT;

  END; !
DELIMITER ;

# -------------------------------------------------------------------------
# Creación de triggers

DELIMITER !
  CREATE TRIGGER inicializar_reservas AFTER INSERT ON instancias_vuelo
  FOR EACH ROW
  BEGIN

    # Declaracion de variables
    DECLARE nombre_clase VARCHAR(20);
    DECLARE fin BOOLEAN DEFAULT false;

    # Declaramos un cursor para consultar las clases que brinda
    DECLARE C CURSOR FOR SELECT clase FROM brinda WHERE vuelo = NEW.vuelo AND dia = NEW.dia;

    # Definimos operacion a realizar luego de que fetch falla
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET fin = true;

    #Abrimos el cursor y le ponemos el primer valor
    OPEN C;
    FETCH C INTO nombre_clase;

    # Ciclar con las clases que brinda la instancia de vuelo
    WHILE NOT fin DO

        #Insertar en asientos_reservados la cantidad 0 a partir del vuelo y clase
        INSERT INTO asientos_reservados(vuelo, fecha, clase, cantidad)
        VALUES (NEW.vuelo, NEW.fecha, nombre_clase, 0);

      fetch C into nombre_clase;
    END WHILE;
    CLOSE C;
  END; !
DELIMITER ;
# -------------------------------------------------------------------------
# Creación de usuarios y otorgamiento de privilegios

# admin
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON vuelos.* TO 'admin'@'localhost';

# empleado
CREATE USER 'empleado' IDENTIFIED BY 'empleado';
GRANT SELECT ON vuelos.* TO 'empleado';

GRANT INSERT ON vuelos.reservas TO 'empleado';
GRANT INSERT ON vuelos.pasajeros TO 'empleado';
GRANT INSERT ON vuelos.reserva_vuelo_clase TO 'empleado';

GRANT UPDATE ON vuelos.reservas TO 'empleado';
GRANT UPDATE ON vuelos.pasajeros TO 'empleado';
GRANT UPDATE ON vuelos.reserva_vuelo_clase TO 'empleado';

GRANT DELETE ON vuelos.reservas TO 'empleado';
GRANT DELETE ON vuelos.pasajeros TO 'empleado';
GRANT DELETE ON vuelos.reserva_vuelo_clase TO 'empleado';

GRANT EXECUTE ON PROCEDURE vuelos.reservaSoloIda TO 'empleado';
GRANT EXECUTE ON PROCEDURE vuelos.reservaIdaVuelta TO 'empleado';

# cliente
CREATE USER 'cliente' IDENTIFIED BY 'cliente';
GRANT SELECT ON vuelos_disponibles TO 'cliente';

# ------------------------------------------------------------------------