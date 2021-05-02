package com.gizlocorp.gnvoice.xml;

import java.io.Serializable;
import java.util.List;

import com.gizlocorp.gnvoice.xml.guia.Destinatario;

public class DestinatarioProducto  implements Serializable {
	private Destinatario destinatario;
	private List<Producto> productos;

	public DestinatarioProducto(Destinatario destinatario,
			List<Producto> productos) {
		/* 23 */this.destinatario = destinatario;
		/* 24 */this.productos = productos;
	}

	public Destinatario getDestinatario() {
		/* 28 */return this.destinatario;
	}

	public void setDestinatario(Destinatario destinatario) {
		/* 32 */this.destinatario = destinatario;
	}

	public List<Producto> getProductos() {
		/* 36 */return this.productos;
	}

	public void setProductos(List<Producto> productos) {
		/* 40 */this.productos = productos;
	}
}

// DestinatarioProducto

