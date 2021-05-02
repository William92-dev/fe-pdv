/**
 * 
 */
package com.gizlocorp.gnvoice.utilitario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * @author
 * 
 */
public class RestServiceInvoker {

	private static Logger log = Logger.getLogger(RestServiceInvoker.class
			.getName());

	public static String invokeWS(String urlService) throws Exception {
		String resultado = "";
		HttpURLConnection conn = null;
		try {
			log.debug(urlService);

			URL url = new URL(urlService);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			// conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED
					&& conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			log.debug("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				resultado += output;
			}

		} catch (MalformedURLException e) {
			log.error("Error invokePost" + e.getMessage());
			throw new Exception(e);
		} catch (IOException e) {
			log.error("Error invokePost" + e.getMessage());
			throw new Exception(e);
		} finally {
			if (conn != null) {
				conn.disconnect();

			}

		}
		return resultado;
	}

}
