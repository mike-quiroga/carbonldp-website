package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Transactional
public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	public SesameRDFResourceRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public boolean hasProperty( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return statementExists( connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ) );
	}

	@Override
	public boolean hasProperty( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( PrefixedURI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	@Override
	public boolean contains( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		return statementExists( connection -> connection.getStatements( resourceURI, pred, obj, false, documentURI ) );
	}

	@Override
	public boolean contains( URI resourceURI, RDFNodeEnum pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( PrefixedURI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( resourceURI, predURI, obj, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	@Override
	public boolean contains( URI resourceURI, RDFNodeEnum pred, RDFNodeEnum obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( PrefixedURI predURI : pred.getURIs() ) {
			for ( PrefixedURI objValue : obj.getURIs() ) {
				boolean hasProperty = statementExists(
					connection -> connection.getStatements( resourceURI, predURI, objValue, false, documentURI )
				);
				if ( hasProperty ) return true;
			}
		}
		return false;
	}

	private boolean statementExists( ConnectionRWTemplate.RepositoryResultRetriever<Statement> retriever ) {
		return connectionTemplate.readStatements(
			retriever,
			repositoryResult -> repositoryResult.hasNext()
		);
	}

	@Override
	public Value getProperty( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getObject();
			}
		);
	}

	@Override
	public Value getProperty( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Value object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					if ( ! statements.hasNext() ) return null;
					return statements.next().getObject();
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Value> getProperties( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Value> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					properties.add( statements.next().getObject() );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Value> getProperties( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Value> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						properties.add( statements.next().getObject() );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public URI getURI( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI( value ) ) return ValueUtil.getURI( value );
				}
				return null;
			}
		);
	}

	@Override
	public URI getURI( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			URI object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isURI( value ) ) return ValueUtil.getURI( value );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<URI> getURIs( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<URI> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI( value ) ) properties.add( ValueUtil.getURI( value ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<URI> getURIs( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<URI> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isURI( value ) ) properties.add( ValueUtil.getURI( value ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Boolean getBoolean( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) return Boolean.parseBoolean( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Boolean getBoolean( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Boolean object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) return Boolean.parseBoolean( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Boolean> getBooleans( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Boolean> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) properties.add( Boolean.parseBoolean( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Boolean> getBooleans( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Boolean> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) properties.add( Boolean.parseBoolean( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Byte getByte( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) return Byte.parseByte( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Byte getByte( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Byte object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) return Byte.parseByte( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Byte> getBytes( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Byte> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) properties.add( Byte.parseByte( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Byte> getBytes( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Byte> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) properties.add( Byte.parseByte( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public DateTime getDate( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) ) return ( parser.parseDateTime( value.stringValue() ) );
				}
				return null;
			}
		);
	}

	@Override
	public DateTime getDate( URI resourceURI, RDFNodeEnum pred ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			DateTime object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) ) return ( parser.parseDateTime( value.stringValue() ) );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<DateTime> getDates( URI resourceURI, URI pred ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<DateTime> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) )
						properties.add( parser.parseDateTime( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<DateTime> getDates( URI resourceURI, RDFNodeEnum pred ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		URI documentURI = getDocumentURI( resourceURI );
		Set<DateTime> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) ) properties.add( parser.parseDateTime( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Double getDouble( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) return Double.parseDouble( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Double getDouble( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Double object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) return Double.parseDouble( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Double> getDoubles( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Double> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) properties.add( Double.parseDouble( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Double> getDoubles( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Double> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) properties.add( Double.parseDouble( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Float getFloat( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) return Float.parseFloat( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Float getFloat( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Float object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) return Float.parseFloat( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Float> getFloats( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Float> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) properties.add( Float.parseFloat( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Float> getFloats( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Float> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) properties.add( Float.parseFloat( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Integer getInteger( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) return Integer.parseInt( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Integer getInteger( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Integer object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) return Integer.parseInt( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Integer> getIntegers( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Integer> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) properties.add( Integer.parseInt( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Integer> getIntegers( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Integer> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) properties.add( Integer.parseInt( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Long getLong( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) return Long.parseLong( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Long getLong( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Long object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) return Long.parseLong( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Long> getLongs( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Long> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) properties.add( Long.parseLong( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Long> getLongs( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Long> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) properties.add( Long.parseLong( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public Short getShort( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) return Short.parseShort( value.stringValue() );
				}
				return null;
			}
		);
	}

	@Override
	public Short getShort( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			Short object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) return Short.parseShort( value.stringValue() );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<Short> getShorts( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<Short> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) properties.add( Short.parseShort( value.stringValue() ) );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<Short> getShorts( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<Short> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) properties.add( Short.parseShort( value.stringValue() ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public String getString( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) return value.stringValue();
				}
				return null;
			}
		);
	}

	@Override
	public String getString( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) return value.stringValue();
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<String> getStrings( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<String> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) properties.add( value.stringValue() );
				}
				return properties;
			}
		);
	}

	@Override
	public Set<String> getStrings( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) properties.add( value.stringValue() );
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public String getString( URI resourceURI, URI pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage();
						if ( languages == null || languages.isEmpty() ) {
							if ( language == null ) return literal.stringValue();
						} else if ( languages.contains( language ) ) return ( value.stringValue() );
					}
				}
				return null;
			}
		);
	}

	@Override
	public String getString( URI resourceURI, RDFNodeEnum pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage();
							if ( languages == null || languages.isEmpty() ) {
								if ( language == null ) return literal.stringValue();
							}
							return ( value.stringValue() );
						}
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	@Override
	public Set<String> getStrings( URI resourceURI, URI pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( resourceURI, pred, null, false, documentURI ),
			statements -> {
				Set<String> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage();
						if ( languages == null || languages.isEmpty() ) {
							if ( language == null ) properties.add( literal.stringValue() );
						}
						properties.add( value.stringValue() );
					}
				}
				return properties;
			}
		);
	}

	@Override
	public Set<String> getStrings( URI resourceURI, RDFNodeEnum pred, Set<String> languages ) {
		URI documentURI = getDocumentURI( resourceURI );
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( resourceURI, predURI, null, false, documentURI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage();
							if ( languages == null || languages.isEmpty() ) {
								if ( language == null ) properties.add( value.stringValue() );
							} else if ( languages.contains( language ) ) {
								properties.add( literal.stringValue() );
							}
						}
					}
					return null;
				}
			);
		}
		return properties;
	}

	@Override
	public void add( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, obj, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI predicate, Collection<Value> values ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.add( resourceURI, predicate, value, documentURI );
			}
		} );
	}

	@Override
	public void add( URI resourceURI, URI pred, boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, DateTime obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void add( URI resourceURI, URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, null, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, RDFNodeEnum pred ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				connection.remove( resourceURI, predURI, null, documentURI );
			}
		} );
	}

	@Override
	public void remove( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, obj, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI predicate, Set<Value> values ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.remove( resourceURI, predicate, value, documentURI );
			}
		} );
	}

	@Override
	public void remove( URI resourceURI, URI pred, boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, DateTime obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void remove( URI resourceURI, URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		URI documentURI = getDocumentURI( resourceURI );
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.remove( resourceURI, pred, literal, documentURI ) );
	}

	@Override
	public void set( URI resourceURI, URI pred, Value obj ) {
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, obj, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, DateTime obj ) {
		Value literal = LiteralUtil.get( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( URI resourceURI, URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public boolean hasType( URI resourceURI, URI type ) {
		return contains( resourceURI, RDFResourceDescription.Property.TYPE, type );
	}

	@Override
	public boolean hasType( URI resourceURI, RDFNodeEnum type ) {
		return contains( resourceURI, RDFResourceDescription.Property.TYPE, type );
	}

	@Override
	public Set<URI> getTypes( URI resourceURI ) {
		return getURIs( resourceURI, RDFResourceDescription.Property.TYPE );
	}

	@Override
	public void addType( URI resourceURI, URI type ) {
		add( resourceURI, RDFResourceDescription.Property.TYPE.getURI(), type );
	}

	@Override
	public void removeType( URI resourceURI, URI type ) {
		for ( URI predURI : RDFResourceDescription.Property.TYPE.getURIs() ) {
			remove( resourceURI, predURI, type );
		}
	}

	@Override
	public void setType( URI resourceURI, URI type ) {
		remove( resourceURI, RDFResourceDescription.Property.TYPE );
		addType( resourceURI, type );
	}

	private URI getDocumentURI( URI resourceURI ) {
		if ( ! URIUtil.hasFragment( resourceURI ) ) return resourceURI;
		return new URIImpl( URIUtil.getDocumentURI( resourceURI.stringValue() ) );
	}

}
