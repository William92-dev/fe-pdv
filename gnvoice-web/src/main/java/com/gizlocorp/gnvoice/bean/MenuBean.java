package com.gizlocorp.gnvoice.bean;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.component.UIPanelMenu;
import org.richfaces.component.UIPanelMenuGroup;
import org.richfaces.component.UIPanelMenuItem;

@RequestScoped
@Named("menuBean")
public class MenuBean extends BaseBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private UIPanelMenu menu;

	@Inject
	SessionBean sessionBean;

	public UIPanelMenu getMenu() {
		if (menu == null) {
			String rol = sessionBean.getCodigoRol();
			menu = new UIPanelMenu();
			String contextPath = FacesContext.getCurrentInstance()
					.getExternalContext().getContextName();
			String urlServidor = sessionBean.getUrlServidor();
			                     

			if ("ADMIN".equals(rol)) {

				// Administracion
				UIPanelMenuGroup menuGroup1 = new UIPanelMenuGroup();
				menuGroup1.setLabel("Administracion");

				UIPanelMenuItem item11 = new UIPanelMenuItem();
				item11.setLabel("Usuarios");
				item11.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaUsuario.jsf'");
				menuGroup1.getChildren().add(item11);

				UIPanelMenuItem item12 = new UIPanelMenuItem();
				item12.setLabel("Emisores");
				item12.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaEmisor.jsf'");
				menuGroup1.getChildren().add(item12);

				UIPanelMenuItem item13 = new UIPanelMenuItem();
				item13.setLabel("Parametros del Sistema");
				item13.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametro.jsf'");
				menuGroup1.getChildren().add(item13);
				
				UIPanelMenuItem item131 = new UIPanelMenuItem();
				item131.setLabel("Parametros del Sistema fecha corte");
				item131.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametroFechaLimite.jsf'");
				menuGroup1.getChildren().add(item131);
				

				UIPanelMenuItem item14 = new UIPanelMenuItem();
				item14.setLabel("Plantillas Notificaciones");
				item14.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaPlantilla.jsf'");
				menuGroup1.getChildren().add(item14);


				UIPanelMenuItem item18 = new UIPanelMenuItem();
				item18.setLabel("Bitacora Evento");
				item18.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaConsultaEvento.jsf'");
				menuGroup1.getChildren().add(item18);
				
				
				UIPanelMenuItem item118 = new UIPanelMenuItem();
				item18.setLabel("Parametrizacion Codigo de Barras");
				item18.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaCodigoBarras.jsf'");
				menuGroup1.getChildren().add(item118);
				
				menu.getChildren().add(menuGroup1);

			}
			
			
			
			
			if ("REC".equals(rol)) {
				// Administracion
				UIPanelMenuGroup menuGroup1 = new UIPanelMenuGroup();
				menuGroup1.setLabel("Administracion");
				
				UIPanelMenuItem item131 = new UIPanelMenuItem();
				item131.setLabel("Parametros del Sistema fecha corte");
				item131.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametroFechaLimite.jsf'");
				menuGroup1.getChildren().add(item131);
				
				menu.getChildren().add(menuGroup1);
				
			}

			if ("SOPOR".equals(rol)) {

				// Administracion
				UIPanelMenuGroup menuGroup1 = new UIPanelMenuGroup();
				menuGroup1.setLabel("Administracion");

				UIPanelMenuItem item11 = new UIPanelMenuItem();
				item11.setLabel("Usuarios");
				item11.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaUsuario.jsf'");
				menuGroup1.getChildren().add(item11);

				menu.getChildren().add(menuGroup1);
			}
			
			if ("SGABF".equals(rol)) {

				// Administracion
				UIPanelMenuGroup menuGroup1 = new UIPanelMenuGroup();
				menuGroup1.setLabel("ABF");

				UIPanelMenuItem item11 = new UIPanelMenuItem();
				item11.setLabel("Usuarios");
				item11.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaUsuario.jsf'");
				menuGroup1.getChildren().add(item11);
				
				UIPanelMenuItem item12 = new UIPanelMenuItem();
				item12.setLabel("Facturas ABF ");
				item12.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoFacturaABF.jsf'");
				menuGroup1.getChildren().add(item12);
				
				UIPanelMenuItem item13 = new UIPanelMenuItem();
				item13.setLabel("Autorización Pendiente");
				item13.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoFacturaABFProcesar.jsf'");
				menuGroup1.getChildren().add(item13);

				menu.getChildren().add(menuGroup1);
			}
			
			if ("CNABF".equals(rol) ) {
				// Consulta
				UIPanelMenuGroup menuGroup4 = new UIPanelMenuGroup();
				menuGroup4.setLabel("Consulta");

				if ("CNABF".equals(rol)) {
					menuGroup4.setExpanded(true);
				}

				UIPanelMenuItem item41 = new UIPanelMenuItem();
				item41.setLabel("Facturas");
				item41.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaFactura.jsf'");
				menuGroup4.getChildren().add(item41);
				
				UIPanelMenuItem item42 = new UIPanelMenuItem();
				item42.setLabel("Retenciones");
				item42.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaRetencion.jsf'");
				menuGroup4.getChildren().add(item42);

				UIPanelMenuItem item43 = new UIPanelMenuItem();
				item43.setLabel("Notas de Credito");
				item43.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaNotaCredito.jsf'");
				menuGroup4.getChildren().add(item43);
				
				UIPanelMenuItem item44 = new UIPanelMenuItem();
				item44.setLabel("Facturas ABF ");
				item44.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoFacturaClientesABF.jsf'");
				menuGroup4.getChildren().add(item44);
				
				menu.getChildren().add(menuGroup4);
			}
			
			
			
			if ("FYPDV".equals(rol)) {
				// Seguimiento
				UIPanelMenuGroup menuGroup2 = new UIPanelMenuGroup();
				menuGroup2.setLabel("Seguimiento");

				UIPanelMenuItem item21 = new UIPanelMenuItem();
				item21.setLabel("Facturas");
				item21.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoFactura.jsf'");
				menuGroup2.getChildren().add(item21);

				UIPanelMenuItem item23 = new UIPanelMenuItem();
				item23.setLabel("Notas de Credito");
				item23.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoNotaCredito.jsf'");
				menuGroup2.getChildren().add(item23);
                
				/**
				  
				UIPanelMenuItem item25 = new UIPanelMenuItem();
				item25.setLabel("Guias de Remision");
				item25.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoGuia.jsf'");
				menuGroup2.getChildren().add(item25);
				
				UIPanelMenuItem item22 = new UIPanelMenuItem();
				item22.setLabel("Retenciones");
				item22.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoRetencion.jsf'");
				menuGroup2.getChildren().add(item22); */

				menu.getChildren().add(menuGroup2);

			}
			
			
			
			
			

			if ("ADMIN".equals(rol) || "SEGUM".equals(rol) || "SOPOR".equals(rol)) {
				// Seguimiento
				UIPanelMenuGroup menuGroup2 = new UIPanelMenuGroup();
				menuGroup2.setLabel("Seguimiento");

				UIPanelMenuItem item21 = new UIPanelMenuItem();
				item21.setLabel("Facturas");
				item21.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoFactura.jsf'");
				menuGroup2.getChildren().add(item21);
				

				UIPanelMenuItem item23 = new UIPanelMenuItem();
				item23.setLabel("Notas de Credito");
				item23.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoNotaCredito.jsf'");
				menuGroup2.getChildren().add(item23);

				UIPanelMenuItem item25 = new UIPanelMenuItem();
				item25.setLabel("Guias de Remision");
				item25.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoGuia.jsf'");
				menuGroup2.getChildren().add(item25);
				
				UIPanelMenuItem item22 = new UIPanelMenuItem();
				item22.setLabel("Retenciones");
				item22.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/seguimientoRetencion.jsf'");
				menuGroup2.getChildren().add(item22);

				// Recepcion
				UIPanelMenuGroup menuGroup3 = new UIPanelMenuGroup();
				menuGroup3.setLabel("Recepción");

				UIPanelMenuItem item31 = new UIPanelMenuItem();
				item31.setLabel("Facturas");
				item31.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFactura.jsf'");
				menuGroup3.getChildren().add(item31);

				UIPanelMenuItem item33 = new UIPanelMenuItem();
				item33.setLabel("Notas de Credito");
				item33.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionNotaCredito.jsf'");
				menuGroup3.getChildren().add(item33);

				UIPanelMenuItem item35 = new UIPanelMenuItem();
				item35.setLabel("Guias de Remision");
				item35.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionGuia.jsf'");
				menuGroup3.getChildren().add(item35);

				UIPanelMenuItem item36 = new UIPanelMenuItem();
				item36.setLabel("Recepcion Comprobante");
				item36.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionComprobante.jsf'");
				menuGroup3.getChildren().add(item36);
				
				UIPanelMenuItem item37 = new UIPanelMenuItem();
				item37.setLabel("Recepcion Comprobante Masivo");
				item37.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionComprobanteMasivo.jsf'");
				menuGroup3.getChildren().add(item37);
				
				UIPanelMenuItem item38 = new UIPanelMenuItem();
				item38.setLabel("Recepcion Orden de Compra");
				item38.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionOrdenesCompra.jsf'");
				menuGroup3.getChildren().add(item38);
				
				UIPanelMenuItem item100 = new UIPanelMenuItem();
				item100.setLabel("Recepcion Clave Acceso");
				item100.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFacturaMail.jsf'");
				menuGroup3.getChildren().add(item100);
				
				
				UIPanelMenuItem item1001 = new UIPanelMenuItem();
				item1001.setLabel("Parametro Recepcion Clave Acceso ");
				item1001.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFacturaMailParametro.jsf'");
				menuGroup3.getChildren().add(item1001);
				
				UIPanelMenuItem item101 = new UIPanelMenuItem();
				item101.setLabel("Parametros Orden Compra");
				item101.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametroOrdenCompra.jsf'");
				menuGroup3.getChildren().add(item101);
				

				menu.getChildren().add(menuGroup2);
				//menu.getChildren().add(menuGroup3);
			}

			if ("ADMIN".equals(rol) || "CONSU".equals(rol) || "ASE".equals(rol) ) {
				// Consulta
				UIPanelMenuGroup menuGroup4 = new UIPanelMenuGroup();
				menuGroup4.setLabel("Consulta");

				if ("CONSU".equals(rol)) {
					menuGroup4.setExpanded(true);
				}

				UIPanelMenuItem item41 = new UIPanelMenuItem();
				item41.setLabel("Facturas");
				item41.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaFactura.jsf'");
				menuGroup4.getChildren().add(item41);
				
				UIPanelMenuItem item42 = new UIPanelMenuItem();
				item42.setLabel("Retenciones");
				item42.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaRetencion.jsf'");
				menuGroup4.getChildren().add(item42);

				UIPanelMenuItem item43 = new UIPanelMenuItem();
				item43.setLabel("Notas de Credito");
				item43.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaNotaCredito.jsf'");
				menuGroup4.getChildren().add(item43);
				
				menu.getChildren().add(menuGroup4);
			}
			
			
			
			if("PROVE".equals(rol)){
				// Consulta
				UIPanelMenuGroup menuGroup4 = new UIPanelMenuGroup();
				menuGroup4.setLabel("Consulta");

				if ("PROVE".equals(rol)) {
					menuGroup4.setExpanded(false);
				}

				UIPanelMenuItem item41 = new UIPanelMenuItem();
				item41.setLabel("Facturas");
				item41.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaFactura.jsf'");
				menuGroup4.getChildren().add(item41);
				
				UIPanelMenuItem item42 = new UIPanelMenuItem();
				item42.setLabel("Retenciones");
				item42.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaRetencion.jsf'");
				menuGroup4.getChildren().add(item42);

				UIPanelMenuItem item43 = new UIPanelMenuItem();
				item43.setLabel("Notas de Credito");
				item43.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/consultaNotaCredito.jsf'");
				menuGroup4.getChildren().add(item43);				

				//recepcion
				UIPanelMenuGroup menuGroup7 = new UIPanelMenuGroup();
				menuGroup7.setLabel("Recepción Proveedores");
				
				if ("PROVE".equals(rol)) {
					menuGroup7.setExpanded(false);			
				}
				
				UIPanelMenuItem item77 = new UIPanelMenuItem();
				item77.setLabel("Facturas");
				item77.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFactura.jsf'");
				menuGroup7.getChildren().add(item77);

				UIPanelMenuItem item78 = new UIPanelMenuItem();
				item78.setLabel("Notas de Credito");
				item78.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionNotaCredito.jsf'");
				menuGroup7.getChildren().add(item78);
				
				
				
				UIPanelMenuItem item79 = new UIPanelMenuItem();
				item78.setLabel("Parametros Orden Compra");
				item78.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametroOrdenCompra.jsf'");
				menuGroup7.getChildren().add(item79);
				
				

				menu.getChildren().add(menuGroup7);
				menu.getChildren().add(menuGroup4);
			}
			
			if ("REC".equals(rol) ) {
				// Recepcion
				UIPanelMenuGroup menuGroup3 = new UIPanelMenuGroup();
				menuGroup3.setLabel("Recepción");

				if ("REC".equals(rol)) {
					menuGroup3.setExpanded(true);
				}
				

				UIPanelMenuItem item31 = new UIPanelMenuItem();
				item31.setLabel("Facturas");
				item31.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFactura.jsf'");
				menuGroup3.getChildren().add(item31);

				UIPanelMenuItem item33 = new UIPanelMenuItem();
				item33.setLabel("Notas de Credito");
				item33.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionNotaCredito.jsf'");
				menuGroup3.getChildren().add(item33);

				UIPanelMenuItem item35 = new UIPanelMenuItem();
				item35.setLabel("Guias de Remision");
				item35.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionGuia.jsf'");
				menuGroup3.getChildren().add(item35);

				UIPanelMenuItem item36 = new UIPanelMenuItem();
				item36.setLabel("Recepcion Comprobante");
				item36.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionComprobante.jsf'");
				menuGroup3.getChildren().add(item36);
				
				UIPanelMenuItem item100 = new UIPanelMenuItem();
				item100.setLabel("Recepcion Clave Acceso");
				item100.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFacturaMail.jsf'");
				menuGroup3.getChildren().add(item100);
				
				UIPanelMenuItem item1001 = new UIPanelMenuItem();
				item1001.setLabel("Parametro - Recepcion Clave Acceso ");
				item1001.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/recepcionFacturaMailParametro.jsf'");
				menuGroup3.getChildren().add(item1001);
				
				
				UIPanelMenuItem item101 = new UIPanelMenuItem();
				item101.setLabel("Parametros Orden Compra");
				item101.setOnclick("document.location.href='http://"+ urlServidor + "/" + contextPath+ "/pages/listaParametroOrdenCompra.jsf'");
				menuGroup3.getChildren().add(item101);
				

				menu.getChildren().add(menuGroup3);
			}

		}
		return menu;
	}

	public void setMenu(UIPanelMenu menu) {
		this.menu = menu;
	}

}
