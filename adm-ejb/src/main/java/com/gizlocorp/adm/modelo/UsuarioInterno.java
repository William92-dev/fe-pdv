package com.gizlocorp.adm.modelo;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.gizlocorp.adm.utilitario.Constantes;

//@Audited
@Entity
@DiscriminatorValue(Constantes.USUARIO_INTERNO)
public class UsuarioInterno extends Usuario {

	private static final long serialVersionUID = 1L;

}
