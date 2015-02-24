package com.carbonldp.repository;

import info.aduna.iteration.Iterations;

import java.util.Set;

import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.descriptions.RDFResourceDescription;
import com.carbonldp.models.RDFResource;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.URIUtil;

@Transactional
public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	private final ConnectionActionTemplate actionTemplate;

	public SesameRDFResourceRepository(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
		actionTemplate = new ConnectionActionTemplate(connectionFactory);
	}

	@Override
	public boolean hasProperty(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasProperty(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(URI resourceURI, URI pred, Value obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(URI resourceURI, RDFNodeEnum pred, Value obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(URI resourceURI, RDFNodeEnum pred, RDFNodeEnum obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Value getProperty(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value getProperty(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Value> getProperties(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Value> getProperties(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getURI(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getURI(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<URI> getURIs(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<URI> getURIs(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getBoolean(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getBoolean(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Boolean> getBooleans(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Boolean> getBooleans(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Byte getByte(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Byte getByte(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Byte> getBytes(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Byte> getBytes(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getDate(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime getDate(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<DateTime> getDates(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<DateTime> getDates(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDouble(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDouble(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Double> getDoubles(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Double> getDoubles(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Float getFloat(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Float getFloat(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Float> getFloats(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Float> getFloats(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getInteger(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getInteger(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> getIntegers(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Integer> getIntegers(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLong(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLong(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getLongs(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Long> getLongs(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Short getShort(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Short getShort(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getShorts(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Short> getShorts(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(URI resourceURI, URI pred, Set<String> languages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(URI resourceURI, RDFNodeEnum pred, Set<String> languages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getStrings(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getStrings(URI resourceURI, RDFNodeEnum pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getStrings(URI resourceURI, URI pred, Set<String> languages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getStrings(URI resourceURI, RDFNodeEnum pred, Set<String> languages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(URI resourceURI, URI pred, Value obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, boolean obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, byte obj) {
		// TODO
	}

	@Override
	public void add(final URI resourceURI, final URI pred, DateTime obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = LiteralUtil.get(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(URI resourceURI, URI pred, double obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, float obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, int obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, long obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, short obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, String obj) {
		// TODO
	}

	@Override
	public void add(URI resourceURI, URI pred, String obj, String language) {
		// TODO
	}

	@Override
	public void remove(URI resourceURI, URI pred) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					connection.remove(resourceURI, predURI, null, documentURI);
				}
			}
		});
	}

	@Override
	public void remove(URI resourceURI, URI pred, Value obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, boolean obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, byte obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, DateTime obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, double obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, float obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, int obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, long obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, short obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, String obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void remove(URI resourceURI, URI pred, String obj, String language) {
		// TODO Auto-generated method stub
	}

	@Override
	public void set(URI resourceURI, URI pred, Value obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, boolean obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, byte obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, DateTime obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, double obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, float obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, int obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, long obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, short obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, String obj) {
		// TODO
	}

	@Override
	public void set(URI resourceURI, URI pred, String obj, String language) {
		// TODO
	}

	@Override
	public boolean hasType(URI resourceURI, URI type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasType(URI resourceURI, RDFNodeEnum type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<URI> getTypes(URI resourceURI) {
		RepositoryConnection connection = connectionFactory.getConnection();
		AbstractModel model = new LinkedHashModel();
		for (URI predicate : RDFResourceDescription.Property.TYPE.getURIs()) {
			RepositoryResult<Statement> statements;
			try {
				statements = connection.getStatements(resourceURI, predicate, null, false, resourceURI);
				model.addAll(Iterations.asSet(statements));
			} catch (RepositoryException e) {
				// TODO: Add error code
				throw new RepositoryRuntimeException(e);
			}

		}
		RDFResource resource = new RDFResource(model, resourceURI);
		return resource.getTypes();
	}

	@Override
	public void addType(URI resourceURI, URI type) {
		// TODO
	}

	@Override
	public void removeType(URI resourceURI, URI type) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setType(URI resourceURI, URI type) {
		// TODO
	}

	private URI getDocumentURI(URI resourceURI) {
		if ( ! URIUtil.hasFragment(resourceURI) ) return resourceURI;
		return new URIImpl(URIUtil.getDocumentURI(resourceURI.stringValue()));
	}

}
