package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.*;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameRDFBlankNodeRepository extends AbstractSesameRepository implements RDFBlankNodeRepository {

	public BNode get( String identifier, URI documentURI ) {
		ValueFactory valueFactory = new ValueFactoryImpl();
		return (BNode) connectionTemplate.readStatements(
			connection -> connection.getStatements( null, RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getURI(), valueFactory.createLiteral( identifier ), false, documentURI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getSubject();
			}
		);
	}

	public SesameRDFBlankNodeRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public boolean hasProperty( BNode blankNode, URI pred, URI documentURI ) {
		return statementExists( connection -> connection.getStatements( blankNode, pred, null, false, documentURI ) );
	}

	@Override
	public boolean hasProperty( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	@Override
	public boolean contains( BNode blankNode, URI pred, Value obj, URI documentURI ) {
		return statementExists( connection -> connection.getStatements( blankNode, pred, obj, false, documentURI ) );
	}

	@Override
	public boolean contains( BNode blankNode, RDFNodeEnum pred, Value obj, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( blankNode, predURI, obj, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	@Override
	public boolean contains( BNode blankNode, RDFNodeEnum pred, RDFNodeEnum obj, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			for ( URI objValue : obj.getURIs() ) {
				boolean hasProperty = statementExists(
					connection -> connection.getStatements( blankNode, predURI, objValue, false, documentURI )
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
	public Value getProperty( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getObject();
			}
		);
	}

	@Override
	public Value getProperty( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Value object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Value> getProperties( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Value> getProperties( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Value> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public URI getURI( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public URI getURI( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			URI object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<URI> getURIs( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<URI> getURIs( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<URI> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Boolean getBoolean( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Boolean getBoolean( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Boolean object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Boolean> getBooleans( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Boolean> getBooleans( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Boolean> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Byte getByte( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Byte getByte( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Byte object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Byte> getBytes( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Byte> getBytes( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Byte> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public DateTime getDate( BNode blankNode, URI pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public DateTime getDate( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		for ( URI predURI : pred.getURIs() ) {
			DateTime object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<DateTime> getDates( BNode blankNode, URI pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<DateTime> getDates( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		Set<DateTime> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Double getDouble( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Double getDouble( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Double object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Double> getDoubles( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Double> getDoubles( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Double> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Float getFloat( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Float getFloat( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Float object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Float> getFloats( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Float> getFloats( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Float> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Integer getInteger( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Integer getInteger( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Integer object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Integer> getIntegers( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Integer> getIntegers( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Integer> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Long getLong( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Long getLong( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Long object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Long> getLongs( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Long> getLongs( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Long> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Short getShort( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Short getShort( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Short object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<Short> getShorts( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<Short> getShorts( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<Short> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public String getString( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public String getString( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<String> getStrings( BNode blankNode, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<String> getStrings( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public String getString( BNode blankNode, URI pred, URI documentURI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public String getString( BNode blankNode, RDFNodeEnum pred, URI documentURI, Set<String> languages ) {
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public Set<String> getStrings( BNode blankNode, URI pred, URI documentURI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( blankNode, pred, null, false, documentURI ),
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
	public Set<String> getStrings( BNode blankNode, RDFNodeEnum pred, URI documentURI, Set<String> languages ) {
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( blankNode, predURI, null, false, documentURI ),
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
	public void add( BNode blankNode, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> connection.add( blankNode, pred, obj, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI predicate, Collection<Value> values, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.add( blankNode, predicate, value, documentURI );
			}
		} );
	}

	@Override
	public void add( BNode blankNode, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void add( BNode blankNode, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, URI documentURI ) {
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, null, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, RDFNodeEnum pred, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				connection.remove( blankNode, predURI, null, documentURI );
			}
		} );
	}

	@Override
	public void remove( BNode blankNode, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, obj, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI predicate, Set<Value> values, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.remove( blankNode, predicate, value, documentURI );
			}
		} );
	}

	@Override
	public void remove( BNode blankNode, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void remove( BNode blankNode, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.remove( blankNode, pred, literal, documentURI ) );
	}

	@Override
	public void set( BNode blankNode, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, obj, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( BNode blankNode, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> {
			connection.remove( blankNode, pred, null, documentURI );
			connection.add( blankNode, pred, literal, documentURI );
		} );
	}

}
