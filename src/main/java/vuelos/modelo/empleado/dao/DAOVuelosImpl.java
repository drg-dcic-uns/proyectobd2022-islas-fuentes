package vuelos.modelo.empleado.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		String sql = "SELECT DISTINCT nro_vuelo, codigo_aero_sale, nombre_aero_sale, hora_sale, codigo_aero_llega ,"
					+ "nombre_aero_llega, hora_llega, modelo, tiempo_estimado FROM vuelos_disponibles WHERE"
						+ " fecha = '"+sdf.format(fechaVuelo)+"' AND "
						+ "ciudad_sale = '"+origen.getCiudad()+"' AND "
						+ "estado_sale = '"+origen.getEstado()+"' AND "
						+ "pais_sale = '"+origen.getPais()+"' AND "
						+ "ciudad_llega = '"+destino.getCiudad()+"' AND "
						+ "estado_llega = '"+destino.getEstado()+"' AND"
						+ " pais_llega = '"+destino.getPais()+"';";

		logger.debug("SQL: {}",sql);
		ArrayList<InstanciaVueloBean> resultado = new ArrayList<InstanciaVueloBean>();  
		
		try {
			 Statement select = conexion.createStatement();
			 ResultSet rs= select.executeQuery(sql);
			 while (rs.next()) {
					logger.debug("Se recuperó el item  nro_vuelo {}, nombre_aero_sale {}, hora_sale {}, "
								+ "nombre_aero_llega {}, hora_llega {}, modelo {}, tiempo_estimado {}", 
					rs.getString("nro_vuelo"), rs.getString("nombre_aero_sale"), rs.getTime("hora_sale"),
					rs.getString("nombre_aero_llega"), rs.getTime("hora_llega"), rs.getString("modelo"),
					rs.getTime("tiempo_estimado"));
					
					AeropuertoBean v_salida = new AeropuertoBeanImpl();
					v_salida.setCodigo(rs.getString("codigo_aero_sale"));
					v_salida.setNombre(rs.getString("nombre_aero_sale"));
					v_salida.setUbicacion(origen);
					
					AeropuertoBean v_llegada = new AeropuertoBeanImpl();
					v_llegada.setCodigo(rs.getString("codigo_aero_llega"));
					v_llegada.setNombre(rs.getString("nombre_aero_llega"));
					v_llegada.setUbicacion(destino);
					
					InstanciaVueloBean v = new InstanciaVueloBeanImpl(); 
					java.sql.Date fecha_vuelo = new java.sql.Date(fechaVuelo.getTime());
					v.setFechaVuelo(fecha_vuelo);
					v.setNroVuelo(rs.getString("nro_vuelo"));
					v.setAeropuertoSalida(v_salida);
					v.setAeropuertoLlegada(v_llegada);
					v.setHoraSalida(rs.getTime("hora_sale"));
					v.setHoraLlegada(rs.getTime("hora_llega"));
					v.setModelo(rs.getString("modelo"));
					v.setTiempoEstimado(rs.getTime("tiempo_estimado"));
					resultado.add(v);
				  }
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error inesperado al consultar la B.D.");
		}
		
		return resultado;
	}

	@Override
	public ArrayList<DetalleVueloBean> recuperarDetalleVuelo(InstanciaVueloBean vuelo) throws Exception {
		
		ArrayList<DetalleVueloBean> resultado = new ArrayList<DetalleVueloBean>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			
			String sql = "SELECT clase, precio, asientos_disponibles FROM vuelos_disponibles WHERE"
							+ " nro_vuelo = '"+vuelo.getNroVuelo()+"' AND"
							+ " fecha = '"+sdf.format(vuelo.getFechaVuelo())+"' ;";
			
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			
			logger.debug("SQL: {}", sql);
			
			while (rs.next()) {
				logger.debug("Se recupero el item con clase {} , precio {} y asientos_disponibles {}", 
						rs.getString("clase"), 
						rs.getString("precio"), 
						rs.getString("asientos_disponibles"));
				
				DetalleVueloBean dv = new DetalleVueloBeanImpl();
				dv.setAsientosDisponibles(rs.getInt("asientos_disponibles"));
				dv.setClase(rs.getString("clase"));
				dv.setPrecio(rs.getFloat("precio"));
				resultado.add(dv);
			}
			
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
		return resultado; 
	}
}
