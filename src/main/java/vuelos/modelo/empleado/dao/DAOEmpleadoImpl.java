package vuelos.modelo.empleado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vuelos.modelo.empleado.beans.EmpleadoBean;
import vuelos.modelo.empleado.beans.EmpleadoBeanImpl;


public class DAOEmpleadoImpl implements DAOEmpleado {

	private static Logger logger = LoggerFactory.getLogger(DAOEmpleadoImpl.class);

	// conexión para acceder a la Base de Datos
	private Connection conexion;

	public DAOEmpleadoImpl(Connection c) {
		this.conexion = c;
	}

	@Override
	public EmpleadoBean recuperarEmpleado(int legajo) throws Exception {
		logger.info("recupera el empleado que corresponde al legajo {}.", legajo);

		String sql = "SELECT legajo, apellido, nombre, doc_tipo, doc_nro, direccion, telefono, password FROM"
					+ " empleados WHERE legajo ='"+legajo+"';";
		
		logger.debug("SQL: {}",sql);
		
		EmpleadoBean empleado = null;
		try {
			
			Statement select = conexion.createStatement();
			ResultSet rs = select.executeQuery(sql);
			
			rs.next();
			logger.debug("Se recuperó el item  legajo {}, nombre {}, apellido {}, doc_tipo {},"
					+ " doc_nro {}, direccion {}, telefono {}, password {}",
					rs.getString("legajo"), rs.getString("nombre"), rs.getString("apellido"), rs.getString("doc_tipo"),
					rs.getInt("doc_nro"), rs.getString("direccion"), rs.getString("telefono"), rs.getString("password"));
			
			empleado = new EmpleadoBeanImpl();
			empleado.setLegajo(rs.getInt("legajo"));
			empleado.setApellido(rs.getString("apellido"));
			empleado.setNombre(rs.getString("nombre"));
			empleado.setTipoDocumento(rs.getString("doc_tipo"));
			empleado.setNroDocumento(rs.getInt("doc_nro"));
			empleado.setDireccion(rs.getString("direccion"));
			empleado.setTelefono(rs.getString("telefono"));
			empleado.setPassword(rs.getString("password")); 
			
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
		return empleado;
	}

}
