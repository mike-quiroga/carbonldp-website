package com.carbonldp.rdf;

import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.*;
import org.openrdf.model.impl.SimpleValueFactory;
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

	public boolean hasProperty( T subject, IRI pred, IRI documentIRI ) {
		return statementExists( connection -> connection.getStatements( subject, pred, null, false, documentIRI ) );
	}

	public boolean hasProperty( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	public boolean contains( T subject, IRI pred, Value obj, IRI documentIRI ) {
		return statementExists( connection -> connection.getStatements( subject, pred, obj, false, documentIRI ) );
	}

	public boolean contains( T subject, RDFNodeEnum pred, Value obj, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			boolean hasProperty = statementExists(
				connection -> connection.getStatements( subject, predIRI, obj, false, documentIRI )
			);
			if ( hasProperty ) return true;
		}
		return false;
	}

	public boolean contains( T subject, RDFNodeEnum pred, RDFNodeEnum obj, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			for ( IRI objValue : obj.getIRIs() ) {
				boolean hasProperty = statementExists(
					connection -> connection.getStatements( subject, predIRI, objValue, false, documentIRI )
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

	public Value getProperty( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				if ( ! statements.hasNext() ) return null;
				return statements.next().getObject();
			}
		);
	}

	public Value getProperty( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Value object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					if ( ! statements.hasNext() ) return null;
					return statements.next().getObject();
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	public Set<Value> getProperties( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				Set<Value> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					properties.add( statements.next().getObject() );
				}
				return properties;
			}
		);
	}

	public Set<Value> getProperties( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Value> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public IRI getIRI( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isIRI( value ) ) return ValueUtil.getIRI( value );
				}
				return null;
			}
		);
	}

	public IRI getIRI( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			IRI object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isIRI( value ) ) return ValueUtil.getIRI( value );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	public Set<IRI> getIRIs( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				Set<IRI> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isIRI( value ) ) properties.add( ValueUtil.getIRI( value ) );
				}
				return properties;
			}
		);
	}

	public Set<IRI> getIRIs( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<IRI> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isIRI( value ) ) properties.add( ValueUtil.getIRI( value ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	public BNode getBNode( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isBNode( value ) ) return ValueUtil.getBNode( value );
				}
				return null;
			}
		);
	}

	public BNode getBNode( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			BNode object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isBNode( value ) ) return ValueUtil.getBNode( value );
					}
					return null;
				}
			);
			if ( object != null ) return object;
		}
		return null;
	}

	public Set<BNode> getBNodes( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				Set<BNode> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isBNode( value ) ) properties.add( ValueUtil.getBNode( value ) );
				}
				return properties;
			}
		);
	}

	public Set<BNode> getBNodes( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<BNode> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isBNode( value ) ) properties.add( ValueUtil.getBNode( value ) );
					}
					return null;
				}
			);
		}
		return properties;
	}

	public Boolean getBoolean( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) ) return Boolean.parseBoolean( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Boolean getBoolean( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Boolean object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Boolean> getBooleans( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Boolean> getBooleans( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Boolean> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Byte getByte( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) ) return Byte.parseByte( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Byte getByte( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Byte object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Byte> getBytes( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Byte> getBytes( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Byte> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public DateTime getDate( T subject, IRI pred, IRI documentIRI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) ) return ( parser.parseDateTime( value.stringValue() ) );
				}
				return null;
			}
		);
	}

	public DateTime getDate( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		for ( IRI predIRI : pred.getIRIs() ) {
			DateTime object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<DateTime> getDates( T subject, IRI pred, IRI documentIRI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<DateTime> getDates( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		Set<DateTime> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Double getDouble( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) ) return Double.parseDouble( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Double getDouble( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Double object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Double> getDoubles( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Double> getDoubles( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Double> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Float getFloat( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) ) return Float.parseFloat( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Float getFloat( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Float object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Float> getFloats( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Float> getFloats( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Float> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Integer getInteger( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) ) return Integer.parseInt( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Integer getInteger( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Integer object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Integer> getIntegers( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Integer> getIntegers( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Integer> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Long getLong( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) ) return Long.parseLong( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Long getLong( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Long object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Long> getLongs( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Long> getLongs( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Long> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Short getShort( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) ) return Short.parseShort( value.stringValue() );
				}
				return null;
			}
		);
	}

	public Short getShort( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			Short object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<Short> getShorts( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<Short> getShorts( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<Short> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public String getString( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) return value.stringValue();
				}
				return null;
			}
		);
	}

	public String getString( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public Set<String> getStrings( T subject, IRI pred, IRI documentIRI ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
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

	public Set<String> getStrings( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		Set<String> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
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

	public String getString( T subject, IRI pred, IRI documentIRI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage().orElse( null );
						if ( languages == null || languages.isEmpty() ) {
							if ( language == null ) return literal.stringValue();
						} else if ( languages.contains( language ) ) return ( value.stringValue() );
					}
				}
				return null;
			}
		);
	}

	public String getString( T subject, RDFNodeEnum pred, IRI documentIRI, Set<String> languages ) {
		for ( IRI predIRI : pred.getIRIs() ) {
			String object = connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage().orElse( null );
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

	public Set<String> getStrings( T subject, IRI pred, IRI documentIRI, Set<String> languages ) {
		return connectionTemplate.readStatements(
			connection -> connection.getStatements( subject, pred, null, false, documentIRI ),
			statements -> {
				Set<String> properties = new HashSet<>();
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
						Literal literal = (Literal) value;
						String language = literal.getLanguage().orElse( null );
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

	public Set<String> getStrings( T subject, RDFNodeEnum pred, IRI documentIRI, Set<String> languages ) {
		Set<String> properties = new HashSet<>();
		for ( IRI predIRI : pred.getIRIs() ) {
			connectionTemplate.readStatements(
				connection -> connection.getStatements( subject, predIRI, null, false, documentIRI ),
				statements -> {
					while ( statements.hasNext() ) {
						Value value = statements.next().getObject();
						if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) ) {
							Literal literal = (Literal) value;
							String language = literal.getLanguage().orElse( null );
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

	public void add( T subject, IRI pred, Value obj, IRI documentIRI ) {
		connectionTemplate.write( connection -> connection.add( subject, pred, obj, documentIRI ) );
	}

	public void add( T subject, IRI predicate, Collection<Value> values, IRI documentIRI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.add( subject, predicate, value, documentIRI );
			}
		} );
	}

	public void add( T subject, IRI pred, boolean obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, byte obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, DateTime obj, IRI documentIRI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, double obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, float obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, int obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, long obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, short obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, String obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void add( T subject, IRI pred, String obj, IRI documentIRI, String language ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.add( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, IRI documentIRI ) {
		connectionTemplate.write( connection -> connection.remove( subject, pred, null, documentIRI ) );
	}

	public void remove( T subject, RDFNodeEnum pred, IRI documentIRI ) {
		connectionTemplate.write( connection -> {
			for ( IRI predIRI : pred.getIRIs() ) {
				connection.remove( subject, predIRI, null, documentIRI );
			}
		} );
	}

	public void remove( T subject, IRI pred, Value obj, IRI documentIRI ) {
		connectionTemplate.write( connection -> connection.remove( subject, pred, obj, documentIRI ) );
	}

	public void remove( T subject, IRI predicate, Set<Value> values, IRI documentIRI ) {
		connectionTemplate.write( connection -> {
			for ( Value value : values ) {
				connection.remove( subject, predicate, value, documentIRI );
			}
		} );
	}

	public void remove( T subject, IRI pred, boolean obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, byte obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, DateTime obj, IRI documentIRI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, double obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, float obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, int obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, long obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, short obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, String obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void remove( T subject, IRI pred, String obj, IRI documentIRI, String language ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> connection.remove( subject, pred, literal, documentIRI ) );
	}

	public void set( T subject, IRI pred, Value obj, IRI documentIRI ) {
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, obj, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, boolean obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, byte obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, DateTime obj, IRI documentIRI ) {
		Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, double obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, float obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, int obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, long obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, short obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, String obj, IRI documentIRI ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public void set( T subject, IRI pred, String obj, IRI documentIRI, String language ) {
		ValueFactory factory = SimpleValueFactory.getInstance();
		Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> {
			connection.remove( subject, pred, null, documentIRI );
			connection.add( subject, pred, literal, documentIRI );
		} );
	}

	public boolean hasType( T subject, IRI type, IRI documentIRI ) {
		return contains( subject, RDFResourceDescription.Property.TYPE, type, documentIRI );
	}

	public boolean hasType( T subject, RDFNodeEnum type, IRI documentIRI ) {
		return contains( subject, RDFResourceDescription.Property.TYPE, type, documentIRI );
	}

	public Set<IRI> getTypes( T subject, IRI documentIRI ) {
		return getIRIs( subject, RDFResourceDescription.Property.TYPE, documentIRI );
	}

	public void addType( T subject, IRI type, IRI documentIRI ) {
		add( subject, RDFResourceDescription.Property.TYPE.getIRI(), type, documentIRI );
	}

	public void removeType( T subject, IRI type, IRI documentIRI ) {
		for ( IRI predIRI : RDFResourceDescription.Property.TYPE.getIRIs() ) {
			remove( subject, predIRI, type, documentIRI );
		}
	}

	public void setType( T subject, IRI type, IRI documentIRI ) {
		remove( subject, RDFResourceDescription.Property.TYPE, documentIRI );
		addType( subject, type, documentIRI );
	}

}
