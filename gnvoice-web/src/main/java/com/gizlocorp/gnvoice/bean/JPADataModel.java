package com.gizlocorp.gnvoice.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
//import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
//import org.richfaces.model.FilterField;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
//import org.richfaces.model.SortField;
//import org.richfaces.component.SortOrder;
import org.richfaces.model.Arrangeable;
import org.richfaces.model.ArrangeableState;

import com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal;
import com.gizlocorp.gnvoice.enumeracion.TipoEjecucion;
import com.gizlocorp.gnvoice.enumeracion.TipoGeneracion;
import com.gizlocorp.gnvoice.modelo.Factura;
import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//import com.lowagie.text.pdf.PRAcroForm;

public abstract class JPADataModel<T> extends ExtendedDataModel<T> implements Arrangeable {
	private EntityManager entityManager;
	private Object rowKey;
	// private ArrangeableState arrangeableState;
	private Class<T> entityClass;
	
	private Map<String, Object> parametros;
	
	private List<Factura> facturas; 
	
	
	
	@EJB(lookup = "java:global/adm-ejb/ServicioOrganizacionImpl!com.gizlocorp.adm.servicio.local.ServicioOrganizacionLocal")
	ServicioOrganizacionLocal servicioOrganizacion;

	public JPADataModel(EntityManager entityManager, Class<T> entityClass,Map<String, Object> parametros) {
		super();
		this.entityManager = entityManager;
		this.entityClass = entityClass;
		this.parametros = parametros;
	}

	public void arrange(FacesContext context, ArrangeableState state) {
		// arrangeableState = state;
	}

	@Override
	public void setRowKey(Object key) {
		rowKey = key;
	}

	@Override
	public Object getRowKey() {
		return rowKey;
	}

	private Long createCountCriteriaQuery() {

		List<Predicate> predicates = new ArrayList<Predicate>();
		 CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<Long> query = cb.createQuery(Long.class);

	        Root<T> music = query.from(entityClass);
	        query.select(cb.count(music));
//	        query.where(cb.equal(music.<String>get("estado"),
//	                             cb.parameter(String.class,"estado")));
	       
			
	        if(parametros != null){
				if (parametros.containsKey("tipoGeneracion")) {
					Predicate ptipoGeneracion = cb.equal(music.<TipoGeneracion> get("tipoGeneracion"), cb.parameter(TipoGeneracion.class, "tipoGeneracion"));
					predicates.add(ptipoGeneracion);
				}
				if (parametros.containsKey("codSecuencial")) {
					Predicate preCodSecuencial = cb.like(music.<String> get("codSecuencial"), cb.parameter(String.class, "codSecuencial"));
					predicates.add(preCodSecuencial);
				}
				if (parametros.containsKey("estado")) {
					Predicate prEstado = cb.equal(music.<String> get("estado"), cb.parameter(String.class, "estado"));
					predicates.add(prEstado);
				}
				if (parametros.containsKey("rucComprador")) {
					Predicate prucComprador = cb.equal(music.<String> get("identificacionComprador"), cb.parameter(String.class, "rucComprador"));
					predicates.add(prucComprador);
				}
				if (parametros.containsKey("rucEmisor")) {
					Predicate prucComprador = cb.equal(music.<String> get("ruc"), cb.parameter(String.class, "rucEmisor"));
					predicates.add(prucComprador);
				}
				if (parametros.containsKey("fechaDesde")) {
					Predicate pfechaDesde = cb.greaterThanOrEqualTo(music.<Date> get("fechaEmisionBase"), cb.parameter(Date.class, "fechaDesde"));
					predicates.add(pfechaDesde);
				}
				if (parametros.containsKey("fechaHasta")) {
					Predicate pfechaHasta = cb.lessThanOrEqualTo(music.<Date> get("fechaEmisionBase"), cb.parameter(Date.class, "fechaHasta"));
					predicates.add(pfechaHasta);
				}
				if (parametros.containsKey("codigoExterno")) {
					Predicate preCodSecuencial = cb.equal(music.<String> get("claveInterna"), cb.parameter(String.class, "codigoExterno"));
					predicates.add(preCodSecuencial);
				}
				if (parametros.containsKey("identificadorUsuario")) {
					Predicate pcodigoExterno = cb.equal(music.<String> get("identificadorUsuario"), cb.parameter(String.class, "identificadorUsuario"));
					predicates.add(pcodigoExterno);
				}
				if (parametros.containsKey("agencia")) {
					Predicate preCodSecuencial = cb.equal(music.<String> get("agencia"), cb.parameter(String.class, "agencia"));
					predicates.add(preCodSecuencial);
				}
//				if (parametros.containsKey("claveContingencia")) {
//					Predicate pclaveContingencia = cb.equal(c.<String> get("claveAcceso"), cb.parameter(String.class, "claveContingencia"));
//					predicates.add(pclaveContingencia);
//				}
				if (parametros.containsKey("tipoEmision")) {
					Predicate ptipoEmision = cb.equal(music.<String> get("tipoEmision"), cb.parameter(String.class, "tipoEmision"));
					predicates.add(ptipoEmision);
				}
				if (parametros.containsKey("tipoAmbiente")) {
					Predicate ptipoAmbiente = cb.equal(music.<String> get("tipoAmbiente"), cb.parameter(String.class, "tipoAmbiente"));
					predicates.add(ptipoAmbiente);
				}
				if (parametros.containsKey("correoidentificacionComprador")) {
					Predicate pcorreoidentificacionComprador = cb.equal(music.<String> get("correoNotificacion"), cb.parameter(String.class, "correoidentificacionComprador"));
					predicates.add(pcorreoidentificacionComprador);
				}
				if (parametros.containsKey("tipoEjecucion")) {
					Predicate ptipoEjecucion = cb.equal(music.<TipoEjecucion> get("tipoEjecucion"), cb.parameter(TipoEjecucion.class, "tipoEjecucion"));
					predicates.add(ptipoEjecucion);
				}
				if (parametros.containsKey("proceso")) {
					Predicate ptipoEjecucion = cb.equal(music.<String> get("proceso"), cb.parameter(String.class, "proceso"));
					predicates.add(ptipoEjecucion);
				}
				
				if (parametros.containsKey("identificacionComprador")) {
					Predicate ptipoEjecucion = cb.equal(music.<String> get("identificacionComprador"), cb.parameter(String.class, "identificacionComprador"));
					predicates.add(ptipoEjecucion);
				}
				

			
			}
			
	        query.where(predicates.toArray(new Predicate[] {}));
	        
	        

	        TypedQuery<Long> tq = entityManager.createQuery(query);
	        
	        if(parametros != null){
				if (parametros.containsKey("tipoGeneracion")) {
					tq.setParameter("tipoGeneracion", parametros.get("tipoGeneracion"));
				}
				if (parametros.containsKey("codSecuencial")) {
					tq.setParameter("codSecuencial", parametros.get("codSecuencial"));
				}
				if (parametros.containsKey("estado")) {				
					tq.setParameter("estado", parametros.get("estado"));
				}
				if (parametros.containsKey("rucComprador")) {
					tq.setParameter("rucComprador", parametros.get("rucComprador"));
				}
				if (parametros.containsKey("fechaDesde")) {
					tq.setParameter("fechaDesde", parametros.get("fechaDesde"));
				}
				if (parametros.containsKey("fechaHasta")) {
					tq.setParameter("fechaHasta", parametros.get("fechaHasta"));
				}
				if (parametros.containsKey("codigoExterno")) {
					tq.setParameter("codigoExterno", parametros.get("codigoExterno"));
				}
				if (parametros.containsKey("identificadorUsuario")) {
					tq.setParameter("identificadorUsuario", parametros.get("identificadorUsuario"));
				}
				if (parametros.containsKey("agencia")) {
					tq.setParameter("agencia", parametros.get("agencia"));
				}
//				if (parametros.containsKey("claveContingencia")) {
//					tq.setParameter("claveContingencia", parametros.get("claveContingencia"));
//				}
				if (parametros.containsKey("tipoEmision")) {
					tq.setParameter("tipoEmision", parametros.get("tipoEmision"));
				}
				if (parametros.containsKey("tipoAmbiente")) {
					tq.setParameter("tipoAmbiente", parametros.get("tipoAmbiente"));
				}
				if (parametros.containsKey("correoidentificacionComprador")) {
					tq.setParameter("correoidentificacionComprador", parametros.get("correoidentificacionComprador"));
				}
				if (parametros.containsKey("tipoEjecucion")) {
					tq.setParameter("tipoEjecucion", parametros.get("tipoEjecucion"));
				}
				if (parametros.containsKey("proceso")) {
					tq.setParameter("proceso", parametros.get("proceso"));
				}
				if (parametros.containsKey("rucEmisor")) {
					tq.setParameter("rucEmisor", parametros.get("rucEmisor"));
				}
				
				if (parametros.containsKey("identificacionComprador")) {
					tq.setParameter("identificacionComprador", parametros.get("identificacionComprador"));
				}
				
			}

	        Long count = tq.getSingleResult();

	        System.out.println("count : "+count+entityClass.getName());

		return count;
	}

	

	protected Class<T> getEntityClass() {
		return entityClass;
	}

	protected Expression<Boolean> createFilterCriteriaForField(
			String propertyName, Object filterValue, Root<T> root,
			CriteriaBuilder criteriaBuilder) {
		String stringFilterValue = (String) filterValue;
		if (Strings.isNullOrEmpty(stringFilterValue)) {
			return null;
		}

		// stringFilterValue =
		// stringFilterValue.toLowerCase(arrangeableState.getLocale());

		Path<String> expression = root.get(propertyName);
		Expression<Integer> locator = criteriaBuilder.locate(
				criteriaBuilder.lower(expression), stringFilterValue, 1);
		return criteriaBuilder.gt(locator, 0);
	}



	@Override
	public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<T> q = cb.createQuery(entityClass);
		Root<T> c = q.from(entityClass);
		q.select(c);

		ParameterExpression<String> p = cb.parameter(String.class);
		ParameterExpression<String> j = cb.parameter(String.class);

		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate firstNameEqual = cb.equal(p, j);
		predicates.add(firstNameEqual);
		
		
		if(parametros != null){
			if (parametros.containsKey("tipoGeneracion")) {
				Predicate ptipoGeneracion = cb.equal(c.<TipoGeneracion> get("tipoGeneracion"), cb.parameter(TipoGeneracion.class, "tipoGeneracion"));
				predicates.add(ptipoGeneracion);
			}
			if (parametros.containsKey("codSecuencial")) {
				Predicate preCodSecuencial = cb.like(c.<String> get("codSecuencial"), cb.parameter(String.class, "codSecuencial"));
				predicates.add(preCodSecuencial);
			}
			if (parametros.containsKey("estado")) {
				Predicate prEstado = cb.equal(c.<String> get("estado"), cb.parameter(String.class, "estado"));
				predicates.add(prEstado);
			}
			if (parametros.containsKey("rucComprador")) {
				Predicate prucComprador = cb.equal(c.<String> get("identificacionComprador"), cb.parameter(String.class, "rucComprador"));
				predicates.add(prucComprador);
			}
			if (parametros.containsKey("rucEmisor")) {
				Predicate prucComprador = cb.equal(c.<String> get("ruc"), cb.parameter(String.class, "rucEmisor"));
				predicates.add(prucComprador);
			}
			if (parametros.containsKey("fechaDesde")) {
				Predicate pfechaDesde = cb.greaterThanOrEqualTo(c.<Date> get("fechaEmisionBase"), cb.parameter(Date.class, "fechaDesde"));
				predicates.add(pfechaDesde);
			}
			if (parametros.containsKey("fechaHasta")) {
				Predicate pfechaHasta = cb.lessThanOrEqualTo(c.<Date> get("fechaEmisionBase"), cb.parameter(Date.class, "fechaHasta"));
				predicates.add(pfechaHasta);
			}
			if (parametros.containsKey("codigoExterno")) {
				Predicate preCodSecuencial = cb.equal(c.<String> get("claveInterna"), cb.parameter(String.class, "codigoExterno"));
				predicates.add(preCodSecuencial);
			}
			if (parametros.containsKey("identificadorUsuario")) {
				Predicate pcodigoExterno = cb.equal(c.<String> get("identificadorUsuario"), cb.parameter(String.class, "identificadorUsuario"));
				predicates.add(pcodigoExterno);
			}
			if (parametros.containsKey("agencia")) {
				Predicate preCodSecuencial = cb.equal(c.<String> get("agencia"), cb.parameter(String.class, "agencia"));
				predicates.add(preCodSecuencial);
			}
//			if (parametros.containsKey("claveContingencia")) {
//				Predicate pclaveContingencia = cb.equal(c.<String> get("claveAcceso"), cb.parameter(String.class, "claveContingencia"));
//				predicates.add(pclaveContingencia);
//			}
			if (parametros.containsKey("tipoEmision")) {
				Predicate ptipoEmision = cb.equal(c.<String> get("tipoEmision"), cb.parameter(String.class, "tipoEmision"));
				predicates.add(ptipoEmision);
			}
			if (parametros.containsKey("tipoAmbiente")) {
				Predicate ptipoAmbiente = cb.equal(c.<String> get("tipoAmbiente"), cb.parameter(String.class, "tipoAmbiente"));
				predicates.add(ptipoAmbiente);
			}
			if (parametros.containsKey("correoidentificacionComprador")) {
				Predicate pcorreoidentificacionComprador = cb.equal(c.<String> get("correoNotificacion"), cb.parameter(String.class, "correoidentificacionComprador"));
				predicates.add(pcorreoidentificacionComprador);
			}
			if (parametros.containsKey("tipoEjecucion")) {
				Predicate ptipoEjecucion = cb.equal(c.<TipoEjecucion> get("tipoEjecucion"), cb.parameter(TipoEjecucion.class, "tipoEjecucion"));
				predicates.add(ptipoEjecucion);
			}
			
		
			if (parametros.containsKey("proceso")) {
				Predicate ptipoEjecucion = cb.equal(c.<String> get("proceso"), cb.parameter(String.class, "proceso"));
				predicates.add(ptipoEjecucion);
			}
			
			
			if (parametros.containsKey("identificacionComprador")) {
				Predicate ptipoEjecucion = cb.equal(c.<String> get("identificacionComprador"), cb.parameter(String.class, "identificacionComprador"));
				predicates.add(ptipoEjecucion);
			}
			
			
			
		}
		

		q.where(predicates.toArray(new Predicate[] {}));
		TypedQuery<T> query = entityManager.createQuery(q);
		query.setParameter(p, "1");
		query.setParameter(j, "1");
		
		System.out.println(query.toString());
		if(parametros != null){
			if (parametros.containsKey("tipoGeneracion")) {
				query.setParameter("tipoGeneracion", parametros.get("tipoGeneracion"));
			}
			if (parametros.containsKey("codSecuencial")) {
				query.setParameter("codSecuencial", parametros.get("codSecuencial"));
			}
			if (parametros.containsKey("estado")) {				
				query.setParameter("estado", parametros.get("estado"));
			}
			if (parametros.containsKey("rucComprador")) {
				query.setParameter("rucComprador", parametros.get("rucComprador"));
			}
			if (parametros.containsKey("fechaDesde")) {
				query.setParameter("fechaDesde", parametros.get("fechaDesde"));
			}
			if (parametros.containsKey("fechaHasta")) {
				query.setParameter("fechaHasta", parametros.get("fechaHasta"));
			}
			if (parametros.containsKey("codigoExterno")) {
				query.setParameter("codigoExterno", parametros.get("codigoExterno"));
			}
			if (parametros.containsKey("identificadorUsuario")) {
				query.setParameter("identificadorUsuario", parametros.get("identificadorUsuario"));
			}
			if (parametros.containsKey("agencia")) {
				query.setParameter("agencia", parametros.get("agencia"));
			}
//			if (parametros.containsKey("claveContingencia")) {
//				query.setParameter("claveContingencia", parametros.get("claveContingencia"));
//			}
			if (parametros.containsKey("tipoEmision")) {
				query.setParameter("tipoEmision", parametros.get("tipoEmision"));
			}
			if (parametros.containsKey("tipoAmbiente")) {
				query.setParameter("tipoAmbiente", parametros.get("tipoAmbiente"));
			}
			if (parametros.containsKey("correoidentificacionComprador")) {
				query.setParameter("correoidentificacionComprador", parametros.get("correoidentificacionComprador"));
			}
			if (parametros.containsKey("tipoEjecucion")) {
				query.setParameter("tipoEjecucion", parametros.get("tipoEjecucion"));
			}
			if (parametros.containsKey("proceso")) {
				query.setParameter("proceso", parametros.get("proceso"));
			}
			if (parametros.containsKey("rucEmisor")) {
				query.setParameter("rucEmisor", parametros.get("rucEmisor"));
			}
			
			if (parametros.containsKey("identificacionComprador")) {
				query.setParameter("identificacionComprador", parametros.get("identificacionComprador"));
			}
		
			
		}
		
		
		
		
//		List<Organizacion> organizaciones = null;
//		try {
//			organizaciones = servicioOrganizacion.listarOrganizaciones(null,null, null, null);
//		} catch (GizloException e) {
//			e.printStackTrace();
//		}
		
		SequenceRange sequenceRange = (SequenceRange) range;
		if (sequenceRange.getFirstRow() >= 0 && sequenceRange.getRows() > 0) {
			query.setFirstResult(sequenceRange.getFirstRow());
			query.setMaxResults(sequenceRange.getRows());
		}

		List<T> data = query.getResultList();
		
		setFacturas((List<Factura>) data);
		
		for (T t : data) {
			
//			Factura factura = (Factura) t;			
//			if (organizaciones != null && !organizaciones.isEmpty()) {	
//				System.out.println("***consultando portal externo33333****");
//				for (Organizacion organizacion : organizaciones) {
//					if (organizacion.getRuc().equals(factura.getRuc())) {
//						factura.setRazonSocial(organizacion.getNombre());
//					}
//				}				
//			}
			visitor.process(context, getId(t), argument);
		}
	}
	

	    

	@Override
	public boolean isRowAvailable() {
		return rowKey != null;
	}

	@Override
	public int getRowCount() {
		Long criteriaQuery = createCountCriteriaQuery();	
		Integer i = (int) (long) criteriaQuery;
		System.out.println("i"+i);
		return i;
	}

	@Override
	public T getRowData() {
		return entityManager.find(entityClass, rowKey);
	}

	@Override
	public int getRowIndex() {
		return -1;
	}

	@Override
	public void setRowIndex(int rowIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getWrappedData() {
		return getFacturas();
		
	}

	@Override
	public void setWrappedData(Object data) {
		throw new UnsupportedOperationException();
	}

	// TODO - implement using metadata
	protected abstract Object getId(T t);

	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}
	
	

}
