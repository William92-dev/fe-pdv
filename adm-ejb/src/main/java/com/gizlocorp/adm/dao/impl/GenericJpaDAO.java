/**
 * 
 */
package com.gizlocorp.adm.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.gizlocorp.adm.dao.GenericDAO;
import com.gizlocorp.adm.excepcion.GizloDeleteException;
import com.gizlocorp.adm.excepcion.GizloPersistException;
import com.gizlocorp.adm.excepcion.GizloUpdateException;

/**
 * 
 * @version $Revision: 1.0 $
 */
public class GenericJpaDAO<T, ID extends Serializable> implements
		GenericDAO<T, ID> {

	private final Class<T> persistentClass;

	@PersistenceContext
	protected EntityManager em;

	@SuppressWarnings("unchecked")
	public GenericJpaDAO() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T findById(ID id) {
		return em.find(persistentClass, id);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return em.createQuery(
				"select o from " + persistentClass.getCanonicalName() + " o")
				.getResultList();
	}

	public T update(T entity) throws GizloUpdateException {
		try {
			return em.merge(entity);
		} catch (Throwable ex) {
			throw new GizloUpdateException(entity, ex);
		}
	}

	public void delete(T entity) throws GizloDeleteException {
		try {
			entity = em.merge(entity);
			em.remove(entity);
		} catch (Throwable ex) {
			throw new GizloDeleteException(entity, ex);
		}
	}

	public void delete(ID id) throws GizloDeleteException {
		T obj = findById(id);
		delete(obj);
	}

	public T persist(T entity) throws GizloPersistException {
		try {
			em.persist(entity);
			return entity;
		} catch (Throwable ex) {
			throw new GizloPersistException(entity, ex);
		}
	}

}
