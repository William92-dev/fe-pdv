package com.gizlocorp.adm.modelo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.gizlocorp.adm.utilitario.Constantes;

//@Audited
@Entity
@DiscriminatorValue(Constantes.USUARIO_EXTERNO)
public class UsuarioExterno extends Usuario {

	private static final long serialVersionUID = 1L;
	@Column(name = "password", length = 200)
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
