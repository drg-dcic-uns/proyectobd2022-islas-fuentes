package vuelos.modelo.empleado.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vuelos.modelo.empleado.beans.DetalleVueloBean;
import vuelos.modelo.empleado.beans.EmpleadoBean;
import vuelos.modelo.empleado.beans.EmpleadoBeanImpl;
import vuelos.modelo.empleado.beans.InstanciaVueloBean;
import vuelos.modelo.empleado.beans.InstanciaVueloClaseBean;
import vuelos.modelo.empleado.beans.PasajeroBean;
import vuelos.modelo.empleado.beans.PasajeroBeanImpl;
import vuelos.modelo.empleado.beans.ReservaBean;
import vuelos.modelo.empleado.beans.ReservaBeanImpl;
import vuelos.modelo.empleado.dao.datosprueba.DAOReservaDatosPrueba;

public class DAOReservaImpl implements DAOReserva {

	private static Logger logger = LoggerFactory.getLogger(DAOReservaImpl.class);

	// conexión para acceder a la Base de Datos
	private Connection conexion;

	public DAOReservaImpl(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public int reservarSoloIda(PasajeroBean pasajero, InstanciaVueloBean vuelo, DetalleVueloBean detalleVuelo,
			EmpleadoBean empleado) throws Exception {
		logger.info("Realiza la reserva de solo ida con pasajero {}", pasajero.getNroDocumento());

		int resultado = -1;
		
		try {
			CallableStatement cstmt = conexion.prepareCall("CALL reservaSoloIda('"+vuelo.getNroVuelo()+"','"+vuelo.getFechaVuelo()+"','"+detalleVuelo.getClase()+
					"','"+pasajero.getTipoDocumento()+"','"+pasajero.getNroDocumento()+"','"+empleado.getLegajo()+"')");
			cstmt.execute();
			ResultSet r = cstmt.getResultSet();
			r.next();
			
			logger.debug("Reserva: {}, {}", r.getInt("numero_reserva"));
			
			resultado = r.getInt("numero_reserva");
		}
		catch (SQLException ex){
			logger.debug("Error al consultar la BD. SQLException: {}. SQLState: {}. VendorError: {}.", ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
			throw ex; 
		}

		return resultado;
	}

	@Override
	public int reservarIdaVuelta(PasajeroBean pasajero, InstanciaVueloBean vueloIda, DetalleVueloBean detalleVueloIda,
			InstanciaVueloBean vueloVuelta, DetalleVueloBean detalleVueloVuelta, EmpleadoBean empleado)
			throws Exception {

		logger.info("Realiza la reserva de ida y vuelta con pasajero {}", pasajero.getNroDocumento());
		
		int resultado = -1;
		
		try {
			CallableStatement cstmt = conexion.prepareCall("CALL reservaIdaVuelta('"+vueloIda.getNroVuelo()+"','"+vueloIda.getFechaVuelo()+"','"+detalleVueloIda.getClase()+
					"','"+vueloVuelta.getNroVuelo()+"','"+vueloVuelta.getFechaVuelo()+"','"+detalleVueloVuelta.getClase()+"','"+
					pasajero.getTipoDocumento()+"','"+pasajero.getNroDocumento()+"','"+empleado.getLegajo()+"')");
			cstmt.execute();
			ResultSet r = cstmt.getResultSet();
			r.next();
			
			logger.debug("Reserva: {}, {}", r.getInt("numero_reserva"));
			
			resultado = r.getInt("numero_reserva");
		}
		catch (SQLException ex){
			logger.debug("Error al consultar la BD. SQLException: {}. SQLState: {}. VendorError: {}.", ex.getMessage(), ex.getSQLState(), ex.getErrorCode());
			throw ex; 
		}

		return resultado;
	}

	@Override
	public ReservaBean recuperarReserva(int codigoReserva) throws Exception {

		logger.info("Solicita recuperar información de la reserva con codigo {}", codigoReserva);

		/**
		 * TODO (parte 2) Debe realizar una consulta que retorne un objeto ReservaBean
		 * donde tenga los datos de la reserva que corresponda con el codigoReserva y en
		 * caso que no exista generar una excepción.
		 *
		 * Debe poblar la reserva con todas las instancias de vuelo asociadas a dicha
		 * reserva y las clases correspondientes.
		 * 
		 * Los objetos ReservaBean además de las propiedades propias de una reserva
		 * tienen un arraylist con pares de instanciaVuelo y Clase. Si la reserva es
		 * solo de ida va a tener un unico elemento el arreglo, y si es de ida y vuelta
		 * tendrá dos elementos.
		 * 
		 * Nota: para acceder a la B.D. utilice la propiedad "conexion" que ya tiene una
		 * conexión establecida con el servidor de B.D. (inicializada en el constructor
		 * DAOReservaImpl(...)).
		 */
		/*
		 * Importante, tenga en cuenta de setear correctamente el atributo IdaVuelta con
		 * el método setEsIdaVuelta en la ReservaBean
		 */
		// Datos estáticos de prueba. Quitar y reemplazar por código que recupera los
		// datos reales.
		ReservaBean reserva = null;
		ArrayList<InstanciaVueloClaseBean> vuelosClase = new ArrayList<InstanciaVueloClaseBean>();
		
		String sql = "select * from reservas WHERE numero = "+codigoReserva+";";
		
		//TODO hacer consultas auxiliares para obtener datos
		String consultaEmpleado = "";
		String consultaPasajero = "";
		String consultaVuelosClase = "";
		try {
			
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			
			logger.debug("Se recuperó la reserva: {}, {}", rs.getString("numero"), rs.getString("estado"));
			
			//TODO recuperar datos de la reserva
			reserva = new ReservaBeanImpl();
			reserva.setNumero(rs.getInt("numero"));
			reserva.setEstado(rs.getString("estado"));
			reserva.setVencimiento(rs.getDate("vencimiento"));
			reserva.setFecha(rs.getDate("fecha"));
			
			//TODO recuperar datos del empleado y setear todos sus atributos
			EmpleadoBean empleado = new EmpleadoBeanImpl();
			empleado.setNombre(rs.getString("nombre"));
			
			reserva.setEmpleado(empleado);
			//TODO recuperar datos del pasajero y setear todos sus atributos
			PasajeroBean pasajero = new PasajeroBeanImpl();
			
			reserva.setPasajero(pasajero);
			//TODO ciclar en las reservas_vuelo_clase y meterlas en un ArrayList, si tiene 2 setear esIdaVuelta(); Usar bucle
			if (vuelosClase.size() > 1) 
				reserva.setEsIdaVuelta(true);
			else
				reserva.setEsIdaVuelta(false);
			
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception ("No se pudo recuperar la reserva");
		}
		

		return reserva;
	}

}
