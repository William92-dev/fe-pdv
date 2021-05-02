package com.gizlocorp.gnvoice.bean;

import java.util.Date;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.security.auth.spi.Util;

import com.gizlocorp.adm.modelo.Parametro;
import com.gizlocorp.adm.modelo.SistemaExterno;
import com.gizlocorp.adm.modelo.Usuario;
import com.gizlocorp.adm.modelo.UsuarioExterno;
import com.gizlocorp.adm.servicio.local.ServicioSistemaExternoLocal;
import com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal;
import com.gizlocorp.adm.utilitario.TripleDESUtil;



@WebService
public class ServicioLoginExterno {
	
	@EJB(lookup = "java:global/adm-ejb/ServicioUsuarioImpl!com.gizlocorp.adm.servicio.local.ServicioUsuarioLocal")
	ServicioUsuarioLocal usuarioUsuarioLocal;
	
	@EJB(lookup = "java:global/adm-ejb/ServicioSistemaExternoImpl!com.gizlocorp.adm.servicio.local.ServicioSistemaExternoLocal")
	ServicioSistemaExternoLocal servicioSistemaExternoLocal;
	
	@EJB
	CacheBean cacheBean;	
	

    public ServicioLoginExterno() {}

    @WebMethod
    public String ingresoLoginExterno(String usuario, String clave, String usuarioAplicacion) {
    	UsuarioExterno usuarioObj = usuarioUsuarioLocal.obtenerUsuarioExterno(usuario);
    	Usuario usuario2Obj = usuarioUsuarioLocal.obtenerUsuario(usuarioAplicacion);
    	String createPasswordHash = Util.createPasswordHash("SHA-256", "hex", null, null, clave);
    	SistemaExterno sistemaExterno = new SistemaExterno();
    	if(usuarioObj != null && usuario2Obj != null){    		
    		if(usuarioObj.getUsername().equals(usuario) && usuarioObj.getPassword().equals(createPasswordHash.toLowerCase())){    			
    			try {
    				System.out.println("***usuario correcto***");
	    			
	    			sistemaExterno.setUsuarioSistema(usuario);
	    			sistemaExterno.setClave(TripleDESUtil._encrypt(clave));    
	    			sistemaExterno.setUsuarioAplicacion(usuarioAplicacion);
	    			sistemaExterno.setFecha(new Date());
					sistemaExterno=servicioSistemaExternoLocal.guardar(sistemaExterno);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
    	}
    	
//    	Parametro parametro= cacheBean.consultarParametro("URL_SERVIDOR", usuario2Obj.getPersona().getOrganizacion().getId());
//    	String urlservidor = parametro.getValor();
    	String rutaServlet = "http://localhost:8330"+"/facturacionGPF/LoginServlet?id="+sistemaExterno.getId();  	
    	
        return rutaServlet;
    }
	

}
