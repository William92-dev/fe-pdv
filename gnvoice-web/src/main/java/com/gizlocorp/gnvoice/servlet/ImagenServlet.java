package com.gizlocorp.gnvoice.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImagenServlet extends HttpServlet {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = -1488469989081755354L;

	public void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws IOException {
		String resource = req.getParameter("resources");
		String filename = null;

		OutputStream out = resp.getOutputStream();
		ServletContext sc = getServletContext();

		if (resource == null || resource.isEmpty()) {
			resource = "/resources/images/avatar.jpg";
			filename = sc.getRealPath(resource);
		} else {
			filename = resource;
		}

		// Get the MIME type of the image
		String mimeType = sc.getMimeType(filename);
		if (mimeType == null) {
			sc.log("Could not get MIME type of " + filename);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		// Set content type
		resp.setContentType(mimeType);

		// Set content size
		File file = new File(filename);
		resp.setContentLength((int) file.length());

		// Open the file and output streams
		FileInputStream in = new FileInputStream(file);

		// Copy the contents of the file to the output stream
		byte[] buf = new byte[1024];
		int count = 0;
		while ((count = in.read(buf)) >= 0) {
			out.write(buf, 0, count);
		}
		in.close();
		out.close();
	}

}
