package com.gizlocorp.adm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.gizlocorp.adm.enumeracion.Estado;
import com.gizlocorp.adm.enumeracion.Logico;
import com.gizlocorp.adm.enumeracion.TipoParametro;

/**
 * Entidad que representa la tabla CW_PARAMETRO de la base de datos.
 * 
 * @author gizlo
 * @revision $Revision: 1.4 $
 */

// @Audited
@Entity
@Table(name = "tb_parametro")
public class Parametro implements Serializable {

	private static final long serialVersionUID = 7680538347715861330L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_parametro", sequenceName = "seq_parametro", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_parametro")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "codigo", length = 20)
	private String codigo;

	@Column(name = "descripcion", length = 500)
	private String descripcion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "estado", length = 3)
	private Estado estado;

	@Column(nullable = true, name = "valor", length = 500)
	private String valor;

	@Column(name = "id_aplicacion")
	private Long idAplicacion;

	@Column(name = "id_organizacion", nullable = true)
	private Long idOrganizacion;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "tipo_parametro", length = 3)
	private TipoParametro tipoParametro;

	@Column(name = "encriptado", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private Logico encriptado;

	@Transient
	private String valorDesencriptado;

	@Transient
	private boolean esEncriptado;

	public boolean isEsEncriptado() {
		return Logico.S.equals(encriptado);
	}

	public void setEsEncriptado(boolean esEncriptado) {
		this.esEncriptado = esEncriptado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return el descripcion
	 */
	public String getDescripcion() {
		return this.descripcion;
	}

	/**
	 * @param descripcion
	 *            el descripcion a establecer
	 */
	public void setDescripcion(final String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @return el estado
	 */
	public Estado getEstado() {
		return this.estado;
	}

	/**
	 * @param estado
	 *            el estado a establecer
	 */
	public void setEstado(final Estado estado) {
		this.estado = estado;
	}

	/**
	 * @return el valor
	 */
	public String getValor() {
		return this.valor;
	}

	/**
	 * @param valor
	 *            el valor a establecer
	 */
	public void setValor(final String valor) {
		this.valor = valor;
	}

	public Long getIdAplicacion() {
		return idAplicacion;
	}

	public void setIdAplicacion(Long idAplicacion) {
		this.idAplicacion = idAplicacion;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public TipoParametro getTipoParametro() {
		return tipoParametro;
	}

	public void setTipoParametro(TipoParametro tipoParametro) {
		this.tipoParametro = tipoParametro;
	}

	public Logico getEncriptado() {
		return encriptado;
	}

	public void setEncriptado(Logico encriptado) {
		this.encriptado = encriptado;
	}

	public String getValorDesencriptado() {
		return valorDesencriptado;
	}

	public void setValorDesencriptado(String valorDesencriptado) {
		this.valorDesencriptado = valorDesencriptado;
	}

	public String toString() {
		StringBuilder entidad = new StringBuilder();

		entidad.append("Codigo: ");
		entidad.append(this.getCodigo());
		entidad.append(", ");
		entidad.append("Descripcion: ");
		entidad.append(this.getDescripcion());
		entidad.append(", ");
		entidad.append("Valor: ");
		entidad.append(this.getValor());

		return entidad.toString();
	}
}