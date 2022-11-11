package vuelos.modelo.empleado.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vuelos.modelo.empleado.beans.PasajeroBean;
import vuelos.modelo.empleado.beans.PasajeroBeanImpl;
import vuelos.modelo.empleado.dao.datosprueba.DAOPasajeroDatosPrueba;

public class DAOPasajeroImpl implements DAOPasajero {

	private static Logger logger = LoggerFactory.getLogger(DAOPasajeroImpl.class);

	private static final long serialVersionUID = 1L;

	// conexión para acceder a la Base de Datos
	private Connection conexion;

	public DAOPasajeroImpl(Connection conexion) {
		this.conexion = conexion;
	}

	@Override
	public PasajeroBean recuperarPasajero(String tipoDoc, int nroDoc) throws Exception {
		
		PasajeroBean pasajero = null;
		String sql = "SELECT * FROM vuelos.pasajeros WHERE doc_tipo = '"+tipoDoc+"' AND doc_nro = "+nroDoc+";";
		
		try {
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			
			rs.next();
			logger.debug("Se recupero el item con doc_tipo {}, doc_nro {}, apellido {}, nombre {}, direccion {} , telefono {} y nacionalida {}",
					rs.getString("doc_tipo"), rs.getString("doc_nro"), rs.getString("apellido"), 
					rs.getString("nombre"), rs.getString("direccion"), rs.getString("telefono"), rs.getString("nacionalidad"));
			
			pasajero = new PasajeroBeanImpl();
			pasajero.setNombre(rs.getString("nombre"));
			pasajero.setApellido(rs.getString("apellido"));
			pasajero.setDireccion(rs.getString("direccion"));
			pasajero.setNroDocumento(rs.getInt("doc_nro"));
			pasajero.setTipoDocumento(rs.getString("doc_tipo"));
			pasajero.setTelefono(rs.getString("telefono"));
			pasajero.setNacionalidad(rs.getString("nacionalidad"));
			
		} catch (SQLException ex ) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
			throw new Exception("Error al buscar pasajero en la B.D.");
		}
		
		
		logger.info("El DAO retorna al pasajero {} {}", pasajero.getApellido(), pasajero.getNombre());
		
		return pasajero;
	}

}
