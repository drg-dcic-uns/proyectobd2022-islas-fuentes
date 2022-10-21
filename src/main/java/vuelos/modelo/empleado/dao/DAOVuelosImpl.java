package vuelos.modelo.empleado.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vuelos.modelo.empleado.beans.AeropuertoBean;
import vuelos.modelo.empleado.beans.AeropuertoBeanImpl;
import vuelos.modelo.empleado.beans.DetalleVueloBean;
import vuelos.modelo.empleado.beans.DetalleVueloBeanImpl;
import vuelos.modelo.empleado.beans.InstanciaVueloBean;
import vuelos.modelo.empleado.beans.InstanciaVueloBeanImpl;
import vuelos.modelo.empleado.beans.UbicacionesBean;
import vuelos.modelo.empleado.beans.UbicacionesBeanImpl;
import vuelos.modelo.empleado.dao.datosprueba.DAOVuelosDatosPrueba;

public class DAOVuelosImpl implements DAOVuelos {

	private static Logger logger = LoggerFactory.getLogger(DAOVuelosImpl.class);
	
	//conexión para acceder a la Base de Datos
	private Connection conexion;
	
	public DAOVuelosImpl(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public ArrayList<InstanciaVueloBean> recuperarVuelosDisponibles(Date fechaVuelo, UbicacionesBean origen, UbicacionesBean destino)  throws Exception {
		/** 
		 * TODO EMPEZADO Debe retornar una lista de vuelos disponibles para ese día con origen y destino según los parámetros. 
		 *      Debe propagar una excepción si hay algún error en la consulta.    
		 *      
		 *      Nota: para acceder a la B.D. utilice la propiedad "conexion" que ya tiene una conexión
		 *      establecida con el servidor de B.D. (inicializada en el constructor DAOVuelosImpl(...)).  
		 */
		//Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sql = "SELECT DISTINCT * FROM vuelos_disponibles WHERE fecha = '"+sdf.format(fechaVuelo)+"' AND ciudad_sale = '"+origen.getCiudad()+"' AND estado_sale = '"+origen.getEstado()+"' AND pais_sale = '"+origen.getPais()+"' AND ciudad_llega = '"+destino.getCiudad()+"' AND estado_llega = '"+destino.getEstado()+"' AND pais_llega = '"+destino.getPais()+"';";
		//Chequear que todos valgan
		logger.debug("SQL: {}",sql);
		ArrayList<InstanciaVueloBean> resultado = new ArrayList<InstanciaVueloBean>();  
		
		try {
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			 while (rs.next()) {
					//logger.debug("Se recuperó el item  {}", rs.getString("nombre_batalla"), rs.getDate("fecha")); COMPLETAR
					InstanciaVueloBean v= new InstanciaVueloBeanImpl(); 
					AeropuertoBean v_salida = new AeropuertoBeanImpl();
					AeropuertoBean v_llegada = new AeropuertoBeanImpl();
					v_salida.setCodigo(rs.getString("codigo_aero_sale"));
					v_llegada.setCodigo(rs.getString("codigo_aero_llega"));
					v.setNroVuelo(rs.getString("nro_vuelo"));
					v.setAeropuertoSalida(v_salida);
					v.setHoraSalida(rs.getTime("hora_sale"));
					v.setAeropuertoLlegada(v_llegada);
					v.setHoraLlegada(rs.getTime("hora_llega"));
					v.setModelo(rs.getString("modelo"));
					v.setTiempoEstimado(rs.getTime("tiempo_estimado"));
					resultado.add(v);		//SE agrega doble	
				  }	
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
		return resultado;
		// Fin datos estáticos de prueba.
	}

	@Override
	public ArrayList<DetalleVueloBean> recuperarDetalleVuelo(InstanciaVueloBean vuelo) throws Exception {
		/** 
		 * TODO EMPEZADO Debe retornar una lista de clases, precios y asientos disponibles de dicho vuelo.		   
		 *      Debe propagar una excepción si hay algún error en la consulta.    
		 *      
		 *      Nota: para acceder a la B.D. utilice la propiedad "conexion" que ya tiene una conexión
		 *      establecida con el servidor de B.D. (inicializada en el constructor DAOVuelosImpl(...)).
		 */
		//Datos estáticos de prueba. Quitar y reemplazar por código que recupera los datos reales.
		DAOVuelosDatosPrueba.generarDetalles(vuelo);
		ArrayList<DetalleVueloBean> resultado = new ArrayList<DetalleVueloBean>();
		System.out.println("Entro a recuperarDetallevuelos");
		String sql = "SELECT clase, precio, asientos_disponibles FROM vuelos_disponibles WHERE nro_vuelo = '"+vuelo.getNroVuelo()+"';";
		Statement select = conexion.createStatement();
		ResultSet rs = select.executeQuery(sql);
		
		logger.debug("SQL: {}", sql);
		
		while (rs.next()) {
			logger.debug("Se recupero el item con clase {} , precio {} y asientos_disponibles {}", rs.getString("clase"), rs.getString("precio"), rs.getString("asientos_disponibles"));
			DetalleVueloBean dv = new DetalleVueloBeanImpl();
			dv.setAsientosDisponibles(rs.getInt("asientos_disponibles"));
			dv.setClase(rs.getString("clase"));
			dv.setPrecio(rs.getFloat("precio"));
			resultado.add(dv);
		}
		return resultado; 
		// Fin datos estáticos de prueba.
	}
}
