package com.gizlocorp.adm.utilitario;

import java.io.File;

import org.apache.log4j.Logger;

public class DeleteThread implements Runnable {
	public static final Logger log = Logger.getLogger(DeleteThread.class);

	private String filedir;

	public DeleteThread(String filedir) {
		this.filedir = filedir;
	}

	public void run() {
		try {
			File file = new File(filedir);

			if (file.delete()) {
				log.debug(file.getName() + " is deleted!");
			} else {
				log.debug("Delete operation is failed.");
			}

		} catch (Exception e) {
			log.debug("Error borrar PDF", e);

		}
	}
}
