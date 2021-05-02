package com.gizlocorp.firmaelectronica.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.gizlocorp.firmaelectronica.ElectronicSignature;
import com.gizlocorp.firmaelectronica.util.DOMUtil;
import com.gizlocorp.firmaelectronica.util.FileUtil;
import com.gizlocorp.firmaelectronica.xml.message.FirmaElectronicaRequest;
import com.gizlocorp.firmaelectronica.xml.message.FirmaElectronicaResponse;

@WebService()
public class FirmaElectronicaWs {

	public static final Logger log = Logger.getLogger(FirmaElectronicaWs.class);

	@WebMethod()
	public FirmaElectronicaResponse firmarDocumento(
			FirmaElectronicaRequest request) {
		FirmaElectronicaResponse response = new FirmaElectronicaResponse();

		try {
			byte[] document = FileUtil.readFile(request.getRutaArchivo());
			StringBuilder documentFirmadoStr = new StringBuilder();
			documentFirmadoStr.append(request.getRutaArchivo().replace(".xml",
					""));
			documentFirmadoStr.append("signedGNVOICE.xml");

			byte[] documentofirmado = ElectronicSignature.signDocumentXMLDoc(
					DOMUtil.byteArrayToXml(document), request.getRutaFirma(),
					request.getClave(), request.getLote() != null ? request
							.getLote().booleanValue() : false);

			FileUtil.saveDocumentToFile(
					DOMUtil.byteArrayToXml(documentofirmado),
					documentFirmadoStr.toString());

			response.setEstado("OK");
			response.setMensajeSistema("Documento guardado en:"
					+ documentFirmadoStr.toString());
			response.setRutaArchivoFirmado(documentFirmadoStr.toString());
			log.info(documentFirmadoStr.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setEstado("ERROR");
			response.setMensajeSistema(ex.getMessage());
		}

		return response;
	}

}
