package com.gizlocorp.adm.utilitario;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.net.InetAddress;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

/**
 * 
 * 
 * 
 * @author j
 * 
 * 
 */

public class Ftp {

	/**
	 * 
	 * Metodo para subir un archivo local a un servidor FTP
	 * 
	 * tenemos que pasarle los datos de:
	 * 
	 * ip o hostname del servidor
	 * 
	 * usuario
	 * 
	 * password
	 * */

	public static boolean uploadFileByFTP(String server, String user,
			String pass, String localPath, String remotePath, String nombreArchivo) {

		try {

			FTPClient ftpClient = new FTPClient();
	        ftpClient.connect(InetAddress.getByName(server));
	        ftpClient.login(user, pass);
	 
	        ftpClient.changeWorkingDirectory(remotePath);
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	        BufferedInputStream buffIn=null;
	        buffIn=new BufferedInputStream(new FileInputStream(localPath));
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.storeFile(nombreArchivo, buffIn);
	 
	        buffIn.close();
	        ftpClient.logout();
	        ftpClient.disconnect();

			return true;

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;

		}

	}

	

}
