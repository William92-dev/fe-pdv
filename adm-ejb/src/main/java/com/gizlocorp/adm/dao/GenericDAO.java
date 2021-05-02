/**
 * 
 */
package com.gizlocorp.adm.dao;

import java.io.Serializable;
import java.util.List;

import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;

/**
 * 
 * @version $Revision: 1.0 $
 */
public interface GenericDAO<T, ID extends Serializable> {

	public T findById(ID id);

	public List<T> findAll();

	public T update(T entity) throws GizloUpdateException;

	public void delete(T entity) throws GizloDeleteException;

	public void delete(ID id) throws GizloDeleteException;

	public T persist(T entity) throws GizloPersistException;

}
