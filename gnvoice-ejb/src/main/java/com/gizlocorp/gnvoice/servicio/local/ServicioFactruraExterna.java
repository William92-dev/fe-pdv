package com.gizlocorp.gnvoice.servicio.local;

import javax.ejb.Local;

@Local
public interface ServicioFactruraExterna {
	void insertFacturaMdb(String mensaje);

}
