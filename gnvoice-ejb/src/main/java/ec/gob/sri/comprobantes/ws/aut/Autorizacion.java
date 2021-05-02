package ec.gob.sri.comprobantes.ws.aut;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "autorizacion", propOrder = { "claveAcceso", "estado",
		"numeroAutorizacion", "fechaAutorizacion", "ambiente", "comprobante",
		"mensajes" })
@XmlRootElement(name = "autorizacion", namespace = "")
public class Autorizacion
/* 14: */{
	/* 15: */protected String claveAcceso;
	/* 15: */protected String estado;
	/* 16: */protected String numeroAutorizacion;
	/* 17: */@XmlSchemaType(name = "dateTime")
	/* 18: */protected XMLGregorianCalendar fechaAutorizacion;
	protected String ambiente;
	protected String comprobante;
	/* 20: */protected Mensajes mensajes;

	public String getClaveAcceso() {
		return claveAcceso;
	}

	public void setClaveAcceso(String claveAcceso) {
		this.claveAcceso = claveAcceso;
	}

	/* 21: */
	/* 22: */public String getEstado()
	/* 23: */{
		/* 24: 72 */return this.estado;
		/* 25: */}

	/* 26: */
	/* 27: */public void setEstado(String value)
	/* 28: */{
		/* 29: 84 */this.estado = value;
		/* 30: */}

	/* 31: */
	/* 32: */public String getNumeroAutorizacion()
	/* 33: */{
		/* 34: 96 */return this.numeroAutorizacion;
		/* 35: */}

	/* 36: */
	/* 37: */public void setNumeroAutorizacion(String value)
	/* 38: */{
		/* 39:108 */this.numeroAutorizacion = value;
		/* 40: */}

	/* 41: */
	/* 42: */public XMLGregorianCalendar getFechaAutorizacion()
	/* 43: */{
		/* 44:120 */return this.fechaAutorizacion;
		/* 45: */}

	/* 46: */
	/* 47: */public void setFechaAutorizacion(XMLGregorianCalendar value)
	/* 48: */{
		/* 49:132 */this.fechaAutorizacion = value;
		/* 50: */}

	public String getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(String ambiente) {
		this.ambiente = ambiente;
	}

	/* 51: */
	/* 52: */public String getComprobante()
	/* 53: */{
		/* 54:144 */return this.comprobante;
		/* 55: */}

	/* 56: */
	/* 57: */public void setComprobante(String value)
	/* 58: */{
		/* 59:156 */this.comprobante = value;
		/* 60: */}

	/* 61: */
	/* 62: */public Mensajes getMensajes()
	/* 63: */{
		/* 64:168 */return this.mensajes;
		/* 65: */}

	/* 66: */
	/* 67: */public void setMensajes(Mensajes value)
	/* 68: */{
		/* 69:180 */this.mensajes = value;
		/* 70: */}

	/* 71: */
	/* 72: */@XmlAccessorType(XmlAccessType.FIELD)
	/* 73: */@XmlType(name = "", propOrder = { "mensaje" })
	/* 74: */public static class Mensajes
	/* 75: */{
		/* 76: */protected List<Mensaje> mensaje;

		/* 77: */
		/* 78: */public List<Mensaje> getMensaje()
		/* 79: */{
			/* 80:234 */if (this.mensaje == null) {
				/* 81:235 */this.mensaje = new ArrayList();
				/* 82: */}
			/* 83:237 */return this.mensaje;
			/* 84: */}
		/* 85: */
	}
	/* 86: */
}

/*
 * Location: C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * 
 * Qualified Name: ec.gob.sri.comprobantes.ws.aut.Autorizacion
 * 
 * JD-Core Version: 0.7.0.1
 */