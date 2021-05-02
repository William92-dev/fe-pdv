/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gizlocorp.gnvoice.json.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author GIZLO
 */
public class Dato {
	@JsonProperty("httpStatus")
	private String httpStatus;
	@JsonProperty("mensaje")
	private String mensaje;
	@JsonProperty("respuesta")
	private List<Respuesta> respuestas;

	public Dato() {
	}

	public Dato(String httpStatus, String mensaje) {
		this.httpStatus = httpStatus;
		this.mensaje = mensaje;
	}

	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public List<Respuesta> getRespuestas() {
		return respuestas;
	}

	public void setRespuestas(List<Respuesta> respuestas) {
		this.respuestas = respuestas;
	}

	@Override
	public String toString() {
		String retorna = "httpStatus = " + httpStatus + "\n";
		retorna += "mensaje = "+ mensaje + "\n";
		if(respuestas!=null && !respuestas.isEmpty()){
			for (Respuesta respuesta : respuestas) {
				retorna += respuesta.toString() + "\n";
			}
		}
		return retorna;
	}
	@SuppressWarnings({ "rawtypes", "resource" })
	public static void readXLSFile() throws IOException {
		InputStream ExcelFileToRead = new FileInputStream("C:\\Users\\Usuario\\Desktop\\Libro1.xls");
		HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow row;
		HSSFCell cell;

		Iterator rows = sheet.rowIterator();

		while (rows.hasNext()) {
			row = (HSSFRow) rows.next();
			Iterator cells = row.cellIterator();

			while (cells.hasNext()) {
				cell = (HSSFCell) cells.next();
				
				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
					System.out.print(cell.getStringCellValue() + " ");
				} 
			}
			System.out.println();
		}

	}
	
	public static void main(String[] args) throws Exception {
//		readXLSXFile();
		System.out.println("888");
		readXLSFile();
	}
}
