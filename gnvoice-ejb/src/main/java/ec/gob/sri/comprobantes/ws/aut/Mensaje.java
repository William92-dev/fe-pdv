/*   1:    */package ec.gob.sri.comprobantes.ws.aut;

/*   2:    */
/*   3:    */import javax.xml.bind.annotation.XmlAccessType;
/*   4:    */
import javax.xml.bind.annotation.XmlAccessorType;
/*   5:    */
import javax.xml.bind.annotation.XmlType;

/*   6:    */
/*   7:    */@XmlAccessorType(XmlAccessType.FIELD)
/* 8: */@XmlType(name = "mensaje", propOrder = { "identificador", "mensaje",
		"informacionAdicional", "tipo" })
/* 9: */public class Mensaje
/* 10: */{

	/* 11: */protected String identificador;
	/* 12: */protected String mensaje;
	/* 13: */protected String informacionAdicional;
	/* 14: */protected String tipo;

	/* 15: */
	/* 16: */public String getIdentificador()
	/* 17: */{
		/* 18: 54 */return this.identificador;
		/* 19: */}

	/* 20: */
	/* 21: */public void setIdentificador(String value)
	/* 22: */{
		/* 23: 66 */this.identificador = value;
		/* 24: */}

	/* 25: */
	/* 26: */public String getMensaje()
	/* 27: */{
		// if (this.mensaje != null) {
		// return StringEscapeUtils.escapeXml(this.mensaje);
		// }

		/* 28: 78 */return this.mensaje;
		/* 29: */}

	/* 30: */
	/* 31: */public void setMensaje(String value)
	/* 32: */{
		/* 33: 90 */this.mensaje = value;
		/* 34: */}

	/* 35: */
	/* 36: */public String getInformacionAdicional()
	/* 37: */{

		// if (this.informacionAdicional != null) {
		// return StringEscapeUtils.escapeXml(this.informacionAdicional);
		// }

		/* 38:102 */return this.informacionAdicional;
		/* 39: */}

	/* 40: */
	/* 41: */public void setInformacionAdicional(String value)
	/* 42: */{
		/* 43:114 */this.informacionAdicional = value;
		/* 44: */}

	/* 45: */
	/* 46: */public String getTipo()
	/* 47: */{
		/* 48:126 */return this.tipo;
		/* 49: */}

	/* 50: */
	/* 51: */public void setTipo(String value)
	/* 52: */{
		/* 53:138 */this.tipo = value;
		/* 54: */}
	/* 55: */
}

/*
 * Location: C:\Program Files\ComprobantesElectronicos\lib\cliente-file-ws.jar
 * Qualified Name: ec.gob.sri.comprobantes.ws.aut.Mensaje JD-Core Version:
 * 0.7.0.1
 */