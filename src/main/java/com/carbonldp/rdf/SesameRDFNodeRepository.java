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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public abstract class SesameRDFNodeRepository<T extends Resource> extends AbstractSesameRepository {

	public SesameRDFNodeRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public boolean hasProperty( T subject, URI pred, URI documentURI ) {
		return statementExists( connection -> connection.getStatements( subject, pred, null, false, documentURI ) );
	}

	public boolean hasProperty( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	public boolean contains( T subject, URI pred, Value obj, URI documentURI ) {
		return statementExists( connection -> connection.getStatements( subject, pred, obj, false, documentURI ) );
	}

	public boolean contains( T subject, RDFNodeEnum pred, Value obj, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( subject, predURI, obj, false, documentURI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	public boolean contains( T subject, RDFNodeEnum pred, RDFNodeEnum obj, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			for ( URI objValue : obj.getURIs() ) {
				boolean hasProperty = statementExists(
					connection -> connection.getStatements( subject, predURI, objValue, false, documentURI )
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

	public Value getProperty( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getObject();
			}
		);
	}

	public Value getProperty( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Value object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
				statements -> {
					if ( ! statements.hasNext() ) return null;
					return statements.next().getObject();
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	public Set<Value> getProperties( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				Set<Value> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					properties.add( statements.next().getObject() );
				}
				return properties;
			}
		);
	}

	public Set<Value> getProperties( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Value> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public URI getURI( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI( value ) ) return ValueUtil.getURI( value );
				}
				return null;
			}
		);
	}

	public URI getURI( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			URI object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<URI> getURIs( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<URI> getURIs( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<URI> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Boolean getBoolean( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) return Boolean.parseBoolean( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Boolean getBoolean( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Boolean object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Boolean> getBooleans( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Boolean> getBooleans( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Boolean> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Byte getByte( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) return Byte.parseByte( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Byte getByte( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Byte object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Byte> getBytes( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Byte> getBytes( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Byte> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public DateTime getDate( T subject, URI pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) ) return ( parser.parseDateTime( value.stringValue() ) );
				}
				return null;
			}
		);
	}

	public DateTime getDate( T subject, RDFNodeEnum pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		for ( URI predURI : pred.getURIs() ) {
			DateTime object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<DateTime> getDates( T subject, URI pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<DateTime> getDates( T subject, RDFNodeEnum pred, URI documentURI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		Set<DateTime> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Double getDouble( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) return Double.parseDouble( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Double getDouble( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Double object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Double> getDoubles( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Double> getDoubles( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Double> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Float getFloat( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) return Float.parseFloat( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Float getFloat( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Float object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Float> getFloats( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Float> getFloats( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Float> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Integer getInteger( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) return Integer.parseInt( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Integer getInteger( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Integer object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Integer> getIntegers( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Integer> getIntegers( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Integer> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Long getLong( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) return Long.parseLong( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Long getLong( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Long object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Long> getLongs( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Long> getLongs( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Long> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Short getShort( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) return Short.parseShort( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Short getShort( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			Short object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<Short> getShorts( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<Short> getShorts( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<Short> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public String getString( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) return value.stringValue();
				}
				return null;
			}
		);
	}

	public String getString( T subject, RDFNodeEnum pred, URI documentURI ) {
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<String> getStrings( T subject, URI pred, URI documentURI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<String> getStrings( T subject, RDFNodeEnum pred, URI documentURI ) {
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public String getString( T subject, URI pred, URI documentURI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public String getString( T subject, RDFNodeEnum pred, URI documentURI, Set<String> languages ) {
		for ( URI predURI : pred.getURIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public Set<String> getStrings( T subject, URI pred, URI documentURI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentURI ),
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

	public Set<String> getStrings( T subject, RDFNodeEnum pred, URI documentURI, Set<String> languages ) {
		Set<String> properties = new HashSet<>();
		for ( URI predURI : pred.getURIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predURI, null, false, documentURI ),
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

	public void add( T subject, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> connection.add( subject, pred, obj, documentURI ) );
	}

	public void add( T subject, URI predicate, Collection<Value> values, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.add( subject, predicate, value, documentURI );
			}
		} );
	}

	public void add( T subject, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void add( T subject, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, URI documentURI ) {
		connectionTemplate.write( connection -> connection.remove( subject, pred, null, documentURI ) );
	}

	public void remove( T subject, RDFNodeEnum pred, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				connection.remove( subject, predURI, null, documentURI );
			}
		} );
	}

	public void remove( T subject, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> connection.remove( subject, pred, obj, documentURI ) );
	}

	public void remove( T subject, URI predicate, Set<Value> values, URI documentURI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.remove( subject, predicate, value, documentURI );
			}
		} );
	}

	public void remove( T subject, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void remove( T subject, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentURI ) );
	}

	public void set( T subject, URI pred, Value obj, URI documentURI ) {
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, obj, documentURI );
		} );
	}

	public void set( T subject, URI pred, boolean obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, byte obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, DateTime obj, URI documentURI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, double obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, float obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, int obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, long obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, short obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, String obj, URI documentURI ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public void set( T subject, URI pred, String obj, URI documentURI, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentURI );
			connection.add( subject, pred, literal, documentURI );
		} );
	}

	public boolean hasType( T subject, URI type, URI documentURI ) {
		return contains( subject, RDFResourceDescription.Property.TYPE, type, documentURI );
	}

	public boolean hasType( T subject, RDFNodeEnum type, URI documentURI ) {
		return contains( subject, RDFResourceDescription.Property.TYPE, type, documentURI );
	}

	public Set<URI> getTypes( T subject, URI documentURI ) {
		return getURIs( subject, RDFResourceDescription.Property.TYPE, documentURI );
	}

	public void addType( T subject, URI type, URI documentURI ) {
		add( subject, RDFResourceDescription.Property.TYPE.getURI(), type, documentURI );
	}

	public void removeType( T subject, URI type, URI documentURI ) {
		for ( URI predURI : RDFResourceDescription.Property.TYPE.getURIs() ) {
			remove( subject, predURI, type, documentURI );
		}
	}

	public void setType( T subject, URI type, URI documentURI ) {
		remove( subject, RDFResourceDescription.Property.TYPE, documentURI );
		addType( subject, type, documentURI );
	}

}
