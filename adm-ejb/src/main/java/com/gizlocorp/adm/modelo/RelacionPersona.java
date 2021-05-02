/**
 * 
 */
package com.gizlocorp.adm.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
//import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * @author gizloCorp
 * @version $Revision: 1.0 $
 */

// @Audited
@Entity
@Table(name = "tb_relacion_persona")
public class RelacionPersona implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_relacion_persona", sequenceName = "seq_persona", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_relacion_persona")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "tipo", nullable = true, length = 3)
	private String tipoRelacion;

	@Column(name = "id_persona", insertable = true, updatable = true)
	private Long idPersona;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_persona", insertable = false, updatable = false)
	private Persona persona;

	@Column(name = "id_organizacion", insertable = true, updatable = true)
	private Long idOrganizacion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_organizacion", insertable = false, updatable = false)
	private Organizacion organizacion;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTipoRelacion() {
		return tipoRelacion;
	}

	public void setTipoRelacion(String tipoRelacion) {
		this.tipoRelacion = tipoRelacion;
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Long getIdOrganizacion() {
		return idOrganizacion;
	}

	public void setIdOrganizacion(Long idOrganizacion) {
		this.idOrganizacion = idOrganizacion;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

}
