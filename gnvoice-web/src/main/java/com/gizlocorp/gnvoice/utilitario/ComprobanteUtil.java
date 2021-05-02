package com.gizlocorp.gnvoice.utilitario;

import java.io.StringWriter;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

public class ComprobanteUtil {

	public static String generarCuerpoMensaje(
			final Map<String, Object> parametrosBody, final String descripcion,
			final String valor) throws Exception {
		Velocity.init();
		VelocityContext context = new VelocityContext();
		if (parametrosBody != null) {
			for (String key : parametrosBody.keySet()) {
				context.put(key, parametrosBody.get(key));
			}

		}
		StringWriter w = new StringWriter();
		w = new StringWriter();
		Velocity.evaluate(context, w, descripcion, valor);

		return w.toString();

	}

}