package vuelos.modelo.empleado;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vuelos.modelo.ModeloImpl;
import vuelos.modelo.empleado.beans.DetalleVueloBean;
import vuelos.modelo.empleado.beans.EmpleadoBean;
import vuelos.modelo.empleado.beans.InstanciaVueloBean;
import vuelos.modelo.empleado.beans.PasajeroBean;
import vuelos.modelo.empleado.beans.ReservaBean;
import vuelos.modelo.empleado.beans.UbicacionesBean;
import vuelos.modelo.empleado.beans.UbicacionesBeanImpl;
import vuelos.modelo.empleado.dao.DAOEmpleado;
import vuelos.modelo.empleado.dao.DAOEmpleadoImpl;
import vuelos.modelo.empleado.dao.DAOPasajero;
import vuelos.modelo.empleado.dao.DAOPasajeroImpl;
import vuelos.modelo.empleado.dao.DAOReserva;
import vuelos.modelo.empleado.dao.DAOReservaImpl;
import vuelos.modelo.empleado.dao.DAOVuelos;
import vuelos.modelo.empleado.dao.DAOVuelosImpl;
import vuelos.modelo.empleado.dao.datosprueba.DAOUbicacionesDatosPrueba;

public class ModeloEmpleadoImpl extends ModeloImpl implements ModeloEmpleado {

	private static Logger logger = LoggerFactory.getLogger(ModeloEmpleadoImpl.class);	

	
	private Integer legajo = null;
	
	public ModeloEmpleadoImpl() {
		logger.debug("Se crea el modelo Empleado.");
	}
	

	@Override
	public boolean autenticarUsuarioAplicacion(String legajo, String password) throws Exception {
		boolean autenticado = false;
		logger.info("Se intenta autenticar el legajo {} con password {}", legajo, password);
		String sql = "SELECT legajo, password FROM empleados WHERE legajo = '"+legajo+"' AND password = md5('"+password+"');";
		
		logger.debug("SQL: {}",sql);
		try {
			ResultSet rs= consulta(sql);
			if (rs.next()) {
				logger.debug("Se recupero el item con legajo{} y password {}", rs.getString("legajo"), rs.getString("password"));
				this.legajo = rs.getInt("legajo");
				autenticado = true;
			}
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
		return autenticado;
	}
	
	@Override
	public ArrayList<String> obtenerTiposDocumento() {
		logger.info("recupera los tipos de documentos.");
		
		String sql = "SELECT DISTINCT doc_tipo FROM pasajeros";
		ArrayList<String> tipos = new ArrayList<String>();
		try {
			ResultSet rs = consulta(sql);
			
			while(rs.next()) {
				logger.debug("Se recupero el item con tipo {}", rs.getString("doc_tipo"));
				String doc_tipo = rs.getString("doc_tipo");
				tipos.add(doc_tipo);
			}
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
		return tipos;
	}		
	
	@Override
	public EmpleadoBean obtenerEmpleadoLogueado() throws Exception {
		logger.info("Solicita al DAO un empleado con legajo {}", this.legajo);
		if (this.legajo == null) {
			logger.info("No hay un empleado logueado.");
			throw new Exception("No hay un empleado logueado. La sesión terminó.");
		}
		
		DAOEmpleado dao = new DAOEmpleadoImpl(this.conexion);
		return dao.recuperarEmpleado(this.legajo);
	}	

	@Override
	public ArrayList<UbicacionesBean> recuperarUbicaciones() throws Exception {
		
		logger.info("recupera las ciudades que tienen aeropuertos.");
		
		String sql = "SELECT * FROM ubicaciones;";
		ArrayList<UbicacionesBean> ubicaciones = new ArrayList<UbicacionesBean>();
		
		try {
			ResultSet rs = consulta(sql);
			
			while (rs.next()) {
				logger.debug("Se recupero el item con pais {} , estado {} , ciudad {} y huso {}", rs.getString("pais"), rs.getString("estado"), rs.getString("ciudad"), rs.getInt("huso"));
				UbicacionesBean ub = new UbicacionesBeanImpl();
				ub.setCiudad(rs.getString("ciudad"));
				ub.setEstado(rs.getString("estado"));
				ub.setPais(rs.getString("pais"));
				ub.setHuso(rs.getInt("huso"));
				ubicaciones.add(ub);
			}
		} catch (SQLException ex) {
			logger.error("SQLException: " + ex.getMessage());
			logger.error("SQLState: " + ex.getSQLState());
			logger.error("VendorError: " + ex.getErrorCode());
		}
	
		return ubicaciones;
	}


	@Override
	public ArrayList<InstanciaVueloBean> obtenerVuelosDisponibles(Date fechaVuelo, UbicacionesBean origen, UbicacionesBean destino) throws Exception {
		
		logger.info("Recupera la lista de vuelos disponibles para la fecha {} desde {} a {}.", fechaVuelo, origen, destino);

		DAOVuelos dao = new DAOVuelosImpl(this.conexion);		
		return dao.recuperarVuelosDisponibles(fechaVuelo, origen, destino);
	}
	
	@Override
	public ArrayList<DetalleVueloBean> obtenerDetalleVuelo(InstanciaVueloBean vuelo) throws Exception {

		logger.info("Recupera la cantidad de asientos y precio del vuelo {} .", vuelo.getNroVuelo());
		
		DAOVuelos dao = new DAOVuelosImpl(this.conexion);		
		return dao.recuperarDetalleVuelo(vuelo);
	}


	@Override
	public PasajeroBean obtenerPasajero(String tipoDoc, int nroDoc) throws Exception {
		logger.info("Solicita al DAO un pasajero con tipo {} y nro {}", tipoDoc, nroDoc);
		
		DAOPasajero dao = new DAOPasajeroImpl(this.conexion);
		return dao.recuperarPasajero(tipoDoc, nroDoc);
	}


	@Override
	public ReservaBean reservarSoloIda(PasajeroBean pasajero, InstanciaVueloBean vuelo, DetalleVueloBean detalleVuelo)
			throws Exception {
		logger.info("Se solicita al modelo realizar una reserva solo ida");

		EmpleadoBean empleadoLogueado = this.obtenerEmpleadoLogueado();
		
		DAOReserva dao = new DAOReservaImpl(this.conexion);
		int nroReserva = dao.reservarSoloIda(pasajero, vuelo, detalleVuelo, empleadoLogueado);
		
		ReservaBean reserva = dao.recuperarReserva(nroReserva); 
		return reserva;
	}


	@Override
	public ReservaBean reservarIdaVuelta(PasajeroBean pasajeroSeleccionado, 
									 InstanciaVueloBean vueloIdaSeleccionado,
									 DetalleVueloBean detalleVueloIdaSeleccionado, 
									 InstanciaVueloBean vueloVueltaSeleccionado,
									 DetalleVueloBean detalleVueloVueltaSeleccionado) throws Exception {
		
		logger.info("Se solicita al modelo realizar una reserva de ida y vuelta");
		
		EmpleadoBean empleadoLogueado = this.obtenerEmpleadoLogueado();
		
		DAOReserva dao = new DAOReservaImpl(this.conexion);
		
		int nroReserva = dao.reservarIdaVuelta(pasajeroSeleccionado, 
									 vueloIdaSeleccionado, 
									 detalleVueloIdaSeleccionado, 
									 vueloVueltaSeleccionado, 
									 detalleVueloVueltaSeleccionado, 
									 empleadoLogueado);
		
		ReservaBean reserva = dao.recuperarReserva(nroReserva); 
		return reserva;		
	}
}
