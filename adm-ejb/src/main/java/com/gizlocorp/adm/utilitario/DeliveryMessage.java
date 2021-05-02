package com.gizlocorp.adm.utilitario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class DeliveryMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3230614431386115308L;

	protected String from;

	protected List<String> to;
	
	protected String sto;

	protected List<String> cc;

	protected List<String> bcc;

	protected String subject;

	protected String body;

	protected List<String> attachment;

	public DeliveryMessage() {
		this.to = new ArrayList<String>();
		this.cc = new ArrayList<String>();
		this.bcc = new ArrayList<String>();
		this.attachment = new ArrayList<String>();
	}

	public void addTo(final String to) {
		this.to.add(to);
	}

	public void addCc(final String cc) {
		this.cc.add(cc);
	}

	public void addBcc(final String bcc) {
		this.bcc.add(bcc);
	}

	/**
	 * @return el from
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * @param from
	 *            el from a establecer
	 */
	public void setFrom(final String from) {
		this.from = from;
	}

	/**
	 * @return el to
	 */
	public List<String> getTo() {
		return this.to;
	}

	/**
	 * @param to
	 *            el to a establecer
	 */
	public void setTo(final List<String> to) {
		this.to = to;
	}

	/**
	 * @return el cc
	 */
	public List<String> getCc() {
		return this.cc;
	}

	/**
	 * @param cc
	 *            el cc a establecer
	 */
	public void setCc(final List<String> cc) {
		this.cc = cc;
	}

	/**
	 * @return el bcc
	 */
	public List<String> getBcc() {
		return this.bcc;
	}

	/**
	 * @param bcc
	 *            el bcc a establecer
	 */
	public void setBcc(final List<String> bcc) {
		this.bcc = bcc;
	}

	/**
	 * @return el subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject
	 *            el subject a establecer
	 */
	public void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return el body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * @param body
	 *            el body a establecer
	 */
	public void setBody(final String body) {
		this.body = body;
	}

	public List<String> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<String> attachment) {
		this.attachment = attachment;
	}

	public String getSto() {
		return sto;
	}

	public void setSto(String sto) {
		this.sto = sto;
	}

}
