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
@Table(name = "tb_relacion_organizacion")
public class RelacionOrganizacion implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @GeneratedValue(strategy = GenerationType.IDENTITY)
	 * 
	 * @Column(name = "id", nullable = false)
	 */
	@Id
	@SequenceGenerator(name = "seq_relacion_organizacion", sequenceName = "seq_persona", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_relacion_organizacion")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "tipo", nullable = true, length = 3)
	private String tipoRelacion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_persona", insertable = true, updatable = true)
	private Persona persona;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_organizacion", insertable = true, updatable = true)
	private Organizacion organizacion;

	@Column(name = "id_org_relacion", insertable = true, updatable = true)
	private Long idOrganizacionRelacion;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_org_relacion", insertable = false, updatable = false)
	private Organizacion organizacionRelacion;

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

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Organizacion getOrganizacion() {
		return organizacion;
	}

	public void setOrganizacion(Organizacion organizacion) {
		this.organizacion = organizacion;
	}

	public Organizacion getOrganizacionRelacion() {
		return organizacionRelacion;
	}

	public void setOrganizacionRelacion(Organizacion organizacionRelacion) {
		this.organizacionRelacion = organizacionRelacion;
	}

	public Long getIdOrganizacionRelacion() {
		return idOrganizacionRelacion;
	}

	public void setIdOrganizacionRelacion(Long idOrganizacionRelacion) {
		this.idOrganizacionRelacion = idOrganizacionRelacion;
	}

}
