package com.carbonldp.repository;

import info.aduna.iteration.Iterations;

import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.descriptions.RDFResourceDescription;
import com.carbonldp.models.PrefixedURI;
import com.carbonldp.models.RDFResource;
import com.carbonldp.repository.txn.RepositoryRuntimeException;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;

@Transactional
public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	private final ConnectionActionTemplate actionTemplate;

	public SesameRDFResourceRepository(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
		actionTemplate = new ConnectionActionTemplate(connectionFactory);
	}

	@Override
	public boolean hasProperty(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				return statements.hasNext();
			}
		});
	}

	@Override
	public boolean hasProperty(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (PrefixedURI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					if ( statements.hasNext() ) return statements.hasNext();
				}
				return false;
			}
		});
	}

	@Override
	public boolean contains(final URI resourceURI, final URI pred, final Value obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, obj, false, documentURI);
				return statements.hasNext();
			}
		});
	}

	@Override
	public boolean contains(final URI resourceURI, final RDFNodeEnum pred, final Value obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (PrefixedURI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, obj, false, documentURI);
					if ( statements.hasNext() ) return statements.hasNext();
				}
				return false;
			}
		});
	}

	@Override
	public boolean contains(final URI resourceURI, final RDFNodeEnum pred, final RDFNodeEnum obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (PrefixedURI predURI : pred.getURIs()) {
					for (PrefixedURI objValue : obj.getURIs()) {
						RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, objValue, false, documentURI);
						if ( statements.hasNext() ) return statements.hasNext();
					}
				}
				return false;
			}
		});
	}

	@Override
	public Value getProperty(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Value>() {
			@Override
			public Value doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				if ( statements.hasNext() ) {
					return statements.next().getObject();
				}
				return null;
			}
		});
	}

	@Override
	public Value getProperty(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Value>() {
			@Override
			public Value doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					if ( statements.hasNext() ) {
						return (statements.next().getObject());
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Value> getProperties(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Value>>() {
			@Override
			public Set<Value> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Value> properties = new LinkedHashSet<Value>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					properties.add(statements.next().getObject());
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Value> getProperties(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Value>>() {
			@Override
			public Set<Value> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Value> properties = new LinkedHashSet<Value>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						properties.add(statements.next().getObject());
					}
				}
				return properties;
			}
		});
	}

	@Override
	public URI getURI(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<URI>() {
			@Override
			public URI doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI(value) ) return ValueUtil.getURI(value);
				}
				return null;
			}
		});
	}

	@Override
	public URI getURI(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<URI>() {
			@Override
			public URI doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isURI(value) ) return ValueUtil.getURI(value);
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<URI> getURIs(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<URI>>() {
			@Override
			public Set<URI> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<URI> properties = new LinkedHashSet<URI>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI(value) ) properties.add(ValueUtil.getURI(value));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<URI> getURIs(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<URI>>() {
			@Override
			public Set<URI> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<URI> properties = new LinkedHashSet<URI>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isURI(value) ) properties.add(ValueUtil.getURI(value));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Boolean getBoolean(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isBoolean((Literal) value) ) return Boolean.parseBoolean(value.stringValue());
				}
				return null;
			}
		});
	}

	@Override
	public Boolean getBoolean(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isBoolean((Literal) value) ) return Boolean.parseBoolean(value.stringValue());
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Boolean> getBooleans(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Boolean>>() {
			@Override
			public Set<Boolean> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Boolean> properties = new LinkedHashSet<Boolean>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isBoolean((Literal) value) ) properties.add(Boolean.parseBoolean(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Boolean> getBooleans(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Boolean>>() {
			@Override
			public Set<Boolean> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Boolean> properties = new LinkedHashSet<Boolean>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isBoolean((Literal) value) ) properties.add(Boolean.parseBoolean(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Byte getByte(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Byte>() {
			@Override
			public Byte doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isByte((Literal) value) ) return Byte.parseByte(value.stringValue());
				}
				return null;
			}
		});
	}

	@Override
	public Byte getByte(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Byte>() {
			@Override
			public Byte doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isByte((Literal) value) ) return Byte.parseByte(value.stringValue());
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Byte> getBytes(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Byte>>() {
			@Override
			public Set<Byte> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Byte> properties = new LinkedHashSet<Byte>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isByte((Literal) value) ) properties.add(Byte.parseByte(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Byte> getBytes(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Byte>>() {
			@Override
			public Set<Byte> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Byte> properties = new LinkedHashSet<Byte>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isByte((Literal) value) ) properties.add(Byte.parseByte(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public DateTime getDate(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<DateTime>() {
			@Override
			public DateTime doWithConnection(RepositoryConnection connection) throws RepositoryException {
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isDate((Literal) value) ) return (parser.parseDateTime(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public DateTime getDate(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<DateTime>() {
			@Override
			public DateTime doWithConnection(RepositoryConnection connection) throws RepositoryException {
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isDate((Literal) value) ) return (parser.parseDateTime(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<DateTime> getDates(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<DateTime>>() {
			@Override
			public Set<DateTime> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<DateTime> properties = new LinkedHashSet<DateTime>();
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isDate((Literal) value) ) properties.add(parser.parseDateTime(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<DateTime> getDates(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<DateTime>>() {
			@Override
			public Set<DateTime> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<DateTime> properties = new LinkedHashSet<DateTime>();
				DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isDate((Literal) value) ) properties.add(parser.parseDateTime(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Double getDouble(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Double>() {
			@Override
			public Double doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isDouble((Literal) value) ) return (Double.parseDouble(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public Double getDouble(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Double>() {
			@Override
			public Double doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isDouble((Literal) value) ) return (Double.parseDouble(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Double> getDoubles(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Double>>() {
			@Override
			public Set<Double> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Double> properties = new LinkedHashSet<Double>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isDouble((Literal) value) ) properties.add(Double.parseDouble(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Double> getDoubles(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Double>>() {
			@Override
			public Set<Double> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Double> properties = new LinkedHashSet<Double>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isDouble((Literal) value) ) properties.add(Double.parseDouble(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Float getFloat(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Float>() {
			@Override
			public Float doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isFloat((Literal) value) ) return (Float.parseFloat(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public Float getFloat(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Float>() {
			@Override
			public Float doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isFloat((Literal) value) ) return (Float.parseFloat(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Float> getFloats(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Float>>() {
			@Override
			public Set<Float> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Float> properties = new LinkedHashSet<Float>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isFloat((Literal) value) ) properties.add(Float.parseFloat(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Float> getFloats(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Float>>() {
			@Override
			public Set<Float> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Float> properties = new LinkedHashSet<Float>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isFloat((Literal) value) ) properties.add(Float.parseFloat(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Integer getInteger(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Integer>() {
			@Override
			public Integer doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isInteger((Literal) value) ) return (Integer.parseInt(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public Integer getInteger(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Integer>() {
			@Override
			public Integer doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isInteger((Literal) value) ) return (Integer.parseInt(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Integer> getIntegers(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Integer>>() {
			@Override
			public Set<Integer> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Integer> properties = new LinkedHashSet<Integer>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isInteger((Literal) value) ) properties.add(Integer.parseInt(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Integer> getIntegers(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Integer>>() {
			@Override
			public Set<Integer> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Integer> properties = new LinkedHashSet<Integer>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isInteger((Literal) value) ) properties.add(Integer.parseInt(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Long getLong(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Long>() {
			@Override
			public Long doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isLong((Literal) value) ) return (Long.parseLong(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public Long getLong(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Long>() {
			@Override
			public Long doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isLong((Literal) value) ) return (Long.parseLong(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Long> getLongs(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Long>>() {
			@Override
			public Set<Long> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Long> properties = new LinkedHashSet<Long>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isLong((Literal) value) ) properties.add(Long.parseLong(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Long> getLongs(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Long>>() {
			@Override
			public Set<Long> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Long> properties = new LinkedHashSet<Long>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isLong((Literal) value) ) properties.add(Long.parseLong(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Short getShort(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Short>() {
			@Override
			public Short doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isShort((Literal) value) ) return (Short.parseShort(value.stringValue()));
				}
				return null;
			}
		});
	}

	@Override
	public Short getShort(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Short>() {
			@Override
			public Short doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isShort((Literal) value) ) return (Short.parseShort(value.stringValue()));
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<Short> getShorts(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Short>>() {
			@Override
			public Set<Short> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Short> properties = new LinkedHashSet<Short>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isShort((Literal) value) ) properties.add(Short.parseShort(value.stringValue()));
				}
				return properties;
			}
		});
	}

	@Override
	public Set<Short> getShorts(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<Short>>() {
			@Override
			public Set<Short> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<Short> properties = new LinkedHashSet<Short>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isShort((Literal) value) ) properties.add(Short.parseShort(value.stringValue()));

					}
				}
				return properties;
			}
		});
	}

	@Override
	public String getString(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<String>() {
			@Override
			public String doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) return (value.stringValue());
				}
				return null;
			}
		});
	}

	@Override
	public String getString(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<String>() {
			@Override
			public String doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					if ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) return (value.stringValue());
					}
				}
				return null;
			}
		});
	}

	@Override
	public String getString(final URI resourceURI, final URI pred, final Set<String> languages) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<String>() {
			@Override
			public String doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage();
						if ( languages == null || languages.isEmpty() ) {
							if ( language == null ) return literal.stringValue();
						} else if ( languages.contains(language) ) return (value.stringValue());
					}
				}
				return null;
			}
		});
	}

	@Override
	public String getString(final URI resourceURI, final RDFNodeEnum pred, final Set<String> languages) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<String>() {
			@Override
			public String doWithConnection(RepositoryConnection connection) throws RepositoryException {
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage();
							if ( languages == null || languages.isEmpty() ) {
								if ( language == null ) return literal.stringValue();
							}
							return (value.stringValue());
						}
					}
				}
				return null;
			}
		});
	}

	@Override
	public Set<String> getStrings(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<String>>() {
			@Override
			public Set<String> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<String> properties = new LinkedHashSet<String>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) properties.add(value.stringValue());
				}
				return properties;
			}
		});
	}

	@Override
	public Set<String> getStrings(final URI resourceURI, final RDFNodeEnum pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<String>>() {
			@Override
			public Set<String> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<String> properties = new LinkedHashSet<String>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) properties.add(value.stringValue());

					}
				}
				return properties;
			}
		});
	}

	@Override
	public Set<String> getStrings(final URI resourceURI, final URI pred, final Set<String> languages) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<String>>() {
			@Override
			public Set<String> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<String> properties = new LinkedHashSet<String>();
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, pred, null, false, documentURI);
				while (statements.hasNext()) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage();
						if ( languages == null || languages.isEmpty() ) {
							if ( language == null ) properties.add(literal.stringValue());
						}
						properties.add(value.stringValue());
					}
				}
				return properties;
			}
		});
	}

	@Override
	public Set<String> getStrings(final URI resourceURI, final RDFNodeEnum pred, final Set<String> languages) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Set<String>>() {
			@Override
			public Set<String> doWithConnection(RepositoryConnection connection) throws RepositoryException {
				Set<String> properties = new LinkedHashSet<String>();
				for (URI predURI : pred.getURIs()) {
					RepositoryResult<Statement> statements = connection.getStatements(resourceURI, predURI, null, false, documentURI);
					while (statements.hasNext()) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral(value) && LiteralUtil.isString((Literal) value) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage();
							if ( languages == null || languages.isEmpty() ) {
								if ( language == null ) properties.add(value.stringValue());
							} else if ( languages.contains(language) ) {
								properties.add(literal.stringValue());
							}
						}

					}
				}
				return properties;
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, Value obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = obj;
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, boolean obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, byte obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
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
	public void add(final URI resourceURI, final URI pred, double obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, float obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, int obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, long obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, short obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, String obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void add(final URI resourceURI, final URI pred, String obj, String language) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj, language);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred) {
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
			}
		});
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
	public void remove(final URI resourceURI, final URI pred, final Value obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, obj, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, final boolean obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, byte obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, DateTime obj) {
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = LiteralUtil.get(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, double obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, float obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, int obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, long obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, short obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, String obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void remove(final URI resourceURI, final URI pred, String obj, String language) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI(resourceURI);
		final Value literal = factory.createLiteral(obj, language);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, Value obj) {
		final Value literal = obj;
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, boolean obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, byte obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, DateTime obj) {
		final Value literal = LiteralUtil.get(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, double obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, float obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, int obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, long obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, short obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, String obj) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public void set(final URI resourceURI, final URI pred, String obj, String language) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral(obj, language);
		final URI documentURI = getDocumentURI(resourceURI);
		actionTemplate.execute(new EmptyConnectionActionCallback() {
			@Override
			public void doWithConnection(RepositoryConnection connection) throws RepositoryException {
				connection.remove(resourceURI, pred, null, documentURI);
				connection.add(resourceURI, pred, literal, documentURI);
			}
		});
	}

	@Override
	public boolean hasType(final URI resourceURI, final URI type) {
		final URI documentURI = getDocumentURI(resourceURI);
		return actionTemplate.execute(new ConnectionActionCallback<Boolean>() {
			@Override
			public Boolean doWithConnection(RepositoryConnection connection) throws RepositoryException {
				RepositoryResult<Statement> statements = connection.getStatements(resourceURI, null, type, false, documentURI);
				return statements.hasNext();
			}
		});
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
