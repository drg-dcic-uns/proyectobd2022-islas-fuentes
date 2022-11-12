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

import vuelos.modelo.empleado.beans.AeropuertoBean;
import vuelos.modelo.empleado.beans.AeropuertoBeanImpl;
import vuelos.modelo.empleado.beans.DetalleVueloBean;
import vuelos.modelo.empleado.beans.DetalleVueloBeanImpl;
import vuelos.modelo.empleado.beans.EmpleadoBean;
import vuelos.modelo.empleado.beans.EmpleadoBeanImpl;
import vuelos.modelo.empleado.beans.InstanciaVueloBean;
import vuelos.modelo.empleado.beans.InstanciaVueloBeanImpl;
import vuelos.modelo.empleado.beans.InstanciaVueloClaseBean;
import vuelos.modelo.empleado.beans.InstanciaVueloClaseBeanImpl;
import vuelos.modelo.empleado.beans.PasajeroBean;
import vuelos.modelo.empleado.beans.PasajeroBeanImpl;
import vuelos.modelo.empleado.beans.ReservaBean;
import vuelos.modelo.empleado.beans.ReservaBeanImpl;
import vuelos.modelo.empleado.beans.UbicacionesBean;
import vuelos.modelo.empleado.beans.UbicacionesBeanImpl;
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
		
		try {
			
			Statement select = conexion.createStatement();
			ResultSet rs_reserva = select.executeQuery(sql);
			rs_reserva.next();
			
			logger.debug("Se recuperó la reserva: {}, {}", rs_reserva.getString("numero"), rs_reserva.getString("estado"));
			
			//TODO recuperar datos de la reserva
			reserva = new ReservaBeanImpl();
			reserva.setNumero(rs_reserva.getInt("numero"));
			reserva.setEstado(rs_reserva.getString("estado"));
			reserva.setVencimiento(rs_reserva.getDate("vencimiento"));
			reserva.setFecha(rs_reserva.getDate("fecha"));
			
			//TODO recuperar datos del empleado y setear todos sus atributos
			
			Statement select_empleado = conexion.createStatement();
			String consultaEmpleado = "SELECT * FROM empleados WHERE legajo = "+rs_reserva.getInt("legajo")+";";
			ResultSet rs_empleado = select_empleado.executeQuery(consultaEmpleado);
			rs_empleado.next();
			
			EmpleadoBean empleado = new EmpleadoBeanImpl();
			empleado.setNombre(rs_empleado.getString("nombre"));
			empleado.setApellido(rs_empleado.getString("apellido"));
			empleado.setDireccion(rs_empleado.getString("direccion"));
			empleado.setLegajo(rs_empleado.getInt("legajo"));
			empleado.setNroDocumento(rs_empleado.getInt("doc_nro"));
			empleado.setTipoDocumento(rs_empleado.getString("doc_tipo"));
			empleado.setTelefono(rs_empleado.getString("telefono"));
			empleado.setPassword(rs_empleado.getString("password")); //Puede no ser necesario
			
			reserva.setEmpleado(empleado);
			
			//TODO recuperar datos del pasajero y setear todos sus atributos
			Statement select_pasajero = conexion.createStatement();
			String consultaPasajero = "SELECT * FROM pasajeros WHERE doc_tipo = '"+rs_reserva.getString("doc_tipo")+"' AND "+rs_reserva.getInt("doc_nro")+";";
			ResultSet rs_pasajero = select_pasajero.executeQuery(consultaPasajero);
			rs_pasajero.next();
			
			PasajeroBean pasajero = new PasajeroBeanImpl();
			pasajero.setNombre(rs_pasajero.getString("nombre"));
			pasajero.setApellido(rs_pasajero.getString("apellido"));
			pasajero.setTipoDocumento(rs_pasajero.getString("doc_tipo"));
			pasajero.setNroDocumento(rs_pasajero.getInt("doc_nro"));
			pasajero.setDireccion(rs_pasajero.getString("direccion"));
			pasajero.setTelefono(rs_pasajero.getString("telefono"));
			pasajero.setNacionalidad(rs_pasajero.getString("nacionalidad"));
			
			reserva.setPasajero(pasajero);

			//TODO ciclar en las reservas_vuelo_clase y meterlas en un ArrayList, si tiene 2 setear esIdaVuelta(); Usar bucle
			Statement select_vuelosClase = conexion.createStatement();
			String consultaVuelosClase = "SELECT * FROM reserva_vuelo_clase WHERE numero = "+codigoReserva+";";
			ResultSet rs_vuelosClase = select_vuelosClase.executeQuery(consultaVuelosClase);
			while (rs_vuelosClase.next()) {
				InstanciaVueloClaseBean instancia_vueloClase = new InstanciaVueloClaseBeanImpl();
				InstanciaVueloBean instancia_vuelo = new InstanciaVueloBeanImpl(); 
				DetalleVueloBean detalle_vuelo = new DetalleVueloBeanImpl();
				String consulta_aux;
				ResultSet rs_aux;
				String nombre_clase = rs_vuelosClase.getString("clase");
				
				instancia_vuelo.setFechaVuelo(rs_vuelosClase.getDate("fecha_vuelo"));
				instancia_vuelo.setNroVuelo(rs_vuelosClase.getString("vuelo"));
				
				//Consultar select * from instancias_vuelo where vuelo = nro_vuelo and fecha = fecha; para saber el dia
				Statement select_dia = conexion.createStatement();
				consulta_aux = "SELECT * FROM instancias_vuelo WHERE vuelo = '"+instancia_vuelo.getNroVuelo()+"' AND '"+instancia_vuelo.getFechaVuelo()+"';";
				ResultSet rs_dia = select_dia.executeQuery(consulta_aux);
				rs_dia.next();
				instancia_vuelo.setDiaSalida(rs_dia.getString("dia"));
				
				//Consultar select * from salidas where vuelo = nro_vuelo and dia = dia
				Statement select_horas = conexion.createStatement();
				consulta_aux = "SELECT * FROM salidas WHERE vuelo = '"+instancia_vuelo.getNroVuelo()+"' AND dia ='"+instancia_vuelo.getDiaSalida()+"';";
				ResultSet rs_horas = select_horas.executeQuery(consulta_aux);
				rs_horas.next();

				System.out.println("Se asigna horas con "+instancia_vuelo.getNroVuelo()+" y "+instancia_vuelo.getDiaSalida());
				instancia_vuelo.setHoraLlegada(rs_horas.getTime("hora_llega"));
				System.out.println("Asigno hora llegada");
				instancia_vuelo.setHoraSalida(rs_horas.getTime("hora_sale"));
				instancia_vuelo.setModelo(rs_horas.getString("modelo_avion"));

				System.out.println("Se consulta aero");
				//Consultamos los vuelos programados con el numero de vuelo, sleect * from vuelos_programados Where numero = vuelo;
				Statement select_aero = conexion.createStatement();
				consulta_aux = "SELECT * FROM vuelos_programados WHERE numero = '"+instancia_vuelo.getNroVuelo()+"';";
				ResultSet rs_aero = select_aero.executeQuery(consulta_aux);
				rs_aero.next();
				
				AeropuertoBean aero_llega = new AeropuertoBeanImpl();
				AeropuertoBean aero_sale = new AeropuertoBeanImpl();
				
				
				aero_llega.setCodigo(rs_aero.getString("aeropuerto_llegada"));
				aero_sale.setCodigo(rs_aero.getString("aeropuerto_salida"));
				
				UbicacionesBean ubicacion_aero_llega = new UbicacionesBeanImpl();
				UbicacionesBean ubicacion_aero_sale = new UbicacionesBeanImpl();

				System.out.println("Se consulta aero llega");
				//Busquemos los datos de cada aeropuerto
				Statement select_datos_aero_llega = conexion.createStatement();
				consulta_aux = "SELECT * FROM aeropuertos WHERE codigo = '"+aero_llega.getCodigo()+"';";
				ResultSet rs_datos_aero_llega = select_datos_aero_llega.executeQuery(consulta_aux);
				rs_datos_aero_llega.next();
				
				aero_llega.setDireccion(rs_datos_aero_llega.getString("direccion"));
				aero_llega.setNombre(rs_datos_aero_llega.getString("nombre"));
				aero_llega.setTelefono(rs_datos_aero_llega.getString("telefono"));
				
				ubicacion_aero_llega.setCiudad(rs_datos_aero_llega.getString("ciudad"));
				ubicacion_aero_llega.setEstado(rs_datos_aero_llega.getString("estado"));
				ubicacion_aero_llega.setPais(rs_datos_aero_llega.getString("pais"));


				System.out.println("Se cobnsutl aero vuelve");
				Statement select_datos_aero_sale = conexion.createStatement();
				consulta_aux = "SELECT * FROM aeropuertos WHERE codigo = '"+aero_sale.getCodigo()+"';";
				ResultSet rs_datos_aero_sale = select_datos_aero_sale.executeQuery(consulta_aux);
				rs_datos_aero_sale.next();
				
				aero_sale.setDireccion(rs_datos_aero_sale.getString("direccion"));
				aero_sale.setNombre(rs_datos_aero_sale.getString("nombre"));
				aero_sale.setTelefono(rs_datos_aero_sale.getString("telefono"));
				
				ubicacion_aero_sale.setCiudad(rs_datos_aero_sale.getString("ciudad"));
				ubicacion_aero_sale.setEstado(rs_datos_aero_sale.getString("estado"));
				ubicacion_aero_sale.setPais(rs_datos_aero_sale.getString("pais"));
				

				System.out.println("Se consulta husop");
				//Busquemos los huso
				Statement select_huso_llega = conexion.createStatement();
				consulta_aux = "SELECT huso FROM ubicaciones WHERE pais = '"+ubicacion_aero_llega.getPais()+"'AND estado = '"+ubicacion_aero_llega.getEstado()+"'AND ciudad = '"+ubicacion_aero_llega.getCiudad()+"';";
				ResultSet rs_huso_llega = select_huso_llega.executeQuery(consulta_aux);
				rs_huso_llega.next();
				
				ubicacion_aero_llega.setHuso(rs_huso_llega.getInt("huso"));

				Statement select_huso_sale = conexion.createStatement();
				consulta_aux = "SELECT huso FROM ubicaciones WHERE pais = '"+ubicacion_aero_sale.getPais()+"'AND estado = '"+ubicacion_aero_sale.getEstado()+"'AND ciudad = '"+ubicacion_aero_sale.getCiudad()+"';";
				ResultSet rs_huso_sale = select_huso_sale.executeQuery(consulta_aux);
				rs_huso_sale.next();
				
				ubicacion_aero_sale.setHuso(rs_huso_sale.getInt("huso"));
				
				aero_llega.setUbicacion(ubicacion_aero_llega);
				aero_sale.setUbicacion(ubicacion_aero_sale);
				
				instancia_vuelo.setAeropuertoLlegada(aero_llega);
				instancia_vuelo.setAeropuertoSalida(aero_sale);
				
				//Consulta el tiempo estimado desde la vista
				Statement select_tiempo_estimado = conexion.createStatement();
				consulta_aux = "SELECT * FROM vuelos_disponibles WHERE nro_vuelo = '"+instancia_vuelo.getNroVuelo()+"' AND '"+instancia_vuelo.getModelo()+"' AND fecha = '"+instancia_vuelo.getFechaVuelo()+
						" AND dia_sale = '"+instancia_vuelo.getDiaSalida()+"' AND hora_sale = '"+instancia_vuelo.getHoraSalida()+"' AND hora_llega = '"+instancia_vuelo.getHoraLlegada()+"';";
				ResultSet rs_tiempo_estimado = select_tiempo_estimado.executeQuery(consulta_aux);
				rs_tiempo_estimado.next();
				instancia_vuelo.setTiempoEstimado(rs_tiempo_estimado.getTime("tiempo_estiamdo"));

				System.out.println("Se consulta brinda");
				//Consultamos SELECT * FROM brinda WHERE vuelo = nro_vuelo AND clase = rs_vuelosClase.getString("clase") AND dia = dia (antes se encuentra);
				Statement select_brinda = conexion.createStatement();
				consulta_aux = "SELECT * FROM brinda WHERE vuelo = '"+instancia_vuelo.getNroVuelo()+"'AND dia = '"+instancia_vuelo.getDiaSalida()+"' AND clase = '"+nombre_clase+"';";
				ResultSet rs_brinda = select_brinda.executeQuery(consulta_aux);
				rs_brinda.next();
				
				detalle_vuelo.setPrecio(rs_brinda.getFloat("precio"));
				detalle_vuelo.setAsientosDisponibles(rs_brinda.getInt("cant_asientos"));
				
				detalle_vuelo.setVuelo(instancia_vuelo);
				detalle_vuelo.setClase(rs_vuelosClase.getString("clase"));
				
				instancia_vueloClase.setClase(detalle_vuelo);
				instancia_vueloClase.setVuelo(instancia_vuelo);
				vuelosClase.add(instancia_vueloClase);
			}
			reserva.setVuelosClase(vuelosClase);
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
