package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.gizlocorp.adm.enumeracion.SistemaExternoEnum;
import com.gizlocorp.adm.modelo.UsuarioRol;
import com.gizlocorp.gnvoice.enumeracion.TipoComprobante;
import com.gizlocorp.gnvoice.servicio.local.ServicioFactruraExterna;
import com.gizlocorp.gnvoice.viewscope.ViewScoped;


@ViewScoped
@Named("consultarFarcturaExterna")
public class ConsultarFarcturaExterna extends BaseBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private static Logger log = Logger.getLogger(FacturaBean.class.getName());
	
	@EJB(lookup = "java:global/gnvoice-ejb/ServicioFactruraExternaImpl!com.gizlocorp.gnvoice.servicio.local.ServicioFactruraExterna")
	ServicioFactruraExterna servicioFactruraExterna;
	
	@Inject
	private SessionBean sessionBean;
	
	
	private Date fechaDesde;

	private Date fechaHasta;
	
	private SistemaExternoEnum sistemExterno;
	
	private TipoComprobante tipoComprobanteDes;
	
	private boolean comboComprobante = false;
	
	private String rucComprador;
	
	
	public void procesar(){
		log.info("***Procesano Integracion***");
		String mensaje = null;
		DateFormat fecha = new SimpleDateFormat("MM/dd/yyyy");
		if(fechaDesde == null) fechaDesde = Calendar.getInstance().getTime();
		if(fechaHasta == null) fechaHasta = Calendar.getInstance().getTime();
		
		if(sistemExterno.equals(SistemaExternoEnum.SISTEMACRPDTA)){
			mensaje = fecha.format(fechaDesde)+ "&" +fecha.format(fechaHasta)+"&"+sistemExterno;	
		}
		if(sistemExterno.equals(SistemaExternoEnum.SISTEMABODEGA)){
			String movimiento = null;
			if(tipoComprobanteDes.equals(TipoComprobante.FACTURA)){
				movimiento = "01";
			}else{
				if(tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO)){
					movimiento = "03";
				}
			}
			mensaje = fecha.format(fechaDesde)+ "&" +fecha.format(fechaHasta)+"&"+sistemExterno+"&"+movimiento+"&"+rucComprador;	
		}
		if(sistemExterno.equals(SistemaExternoEnum.SISTEMAPROVEEDORES)){
			String movimiento = null;
			if(tipoComprobanteDes.equals(TipoComprobante.FACTURA)){
				movimiento = "01";
			}else{
				if(tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO)){
					movimiento = "03";
				}
			}
			mensaje = fecha.format(fechaDesde)+ "&" +fecha.format(fechaHasta)+"&"+sistemExterno+"&"+movimiento+"&"+rucComprador;	
		}
			
		servicioFactruraExterna.insertFacturaMdb(mensaje);
		
	}
	
	public void ejecutaComboSistemaExterno(){
		if(sistemExterno.equals(SistemaExternoEnum.SISTEMABODEGA)||sistemExterno.equals(SistemaExternoEnum.SISTEMAPROVEEDORES)){
			comboComprobante = true;
		}else{
			comboComprobante = false;
		}
		
	}
	
	
	@PostConstruct
	public void postContruct() {
		log.debug("inicio");
		try {
			rucComprador = null;
			List<UsuarioRol> usuarioRoles = sessionBean.getUsuario()
					.getUsuariosRoles();
			if (usuarioRoles != null && !usuarioRoles.isEmpty()) {
				for (UsuarioRol usuRol : usuarioRoles) {
					if (usuRol.getRol().getCodigo().equals("CONSU")) {
						rucComprador = sessionBean.getUsuario().getPersona().getIdentificacion();
					}
				}
			}
			rucComprador = sessionBean.getUsuario().getPersona().getIdentificacion();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
	
	
	
	public List<SelectItem> getStistemaExterno() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (SistemaExternoEnum tipoComprobanteDes : SistemaExternoEnum.values()) {
			items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
					.getDescripcion()));
		}
		return items;
	}
	
	public List<SelectItem> getTipoComprobanteDesList() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (TipoComprobante tipoComprobanteDes : TipoComprobante.values()) {
			if (tipoComprobanteDes.equals(TipoComprobante.FACTURA)
					|| tipoComprobanteDes.equals(TipoComprobante.NOTA_CREDITO)) {
				items.add(new SelectItem(tipoComprobanteDes, tipoComprobanteDes
						.getEtiqueta()));
			}

		}
		return items;
	}

	public Date getFechaDesde() {
		return fechaDesde;
	}

	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}

	public Date getFechaHasta() {
		return fechaHasta;
	}

	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}

	public SistemaExternoEnum getSistemExterno() {
		return sistemExterno;
	}

	public void setSistemExterno(SistemaExternoEnum sistemExterno) {
		this.sistemExterno = sistemExterno;
	}

	public boolean isComboComprobante() {
		return comboComprobante;
	}

	public void setComboComprobante(boolean comboComprobante) {
		this.comboComprobante = comboComprobante;
	}

	public TipoComprobante getTipoComprobanteDes() {
		return tipoComprobanteDes;
	}

	public void setTipoComprobanteDes(TipoComprobante tipoComprobanteDes) {
		this.tipoComprobanteDes = tipoComprobanteDes;
	}

	
	
	
	
	
	
	

}
