package com.carbonldp.rdf;

import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.descriptions.RDFResourceDescription;
import com.carbonldp.models.PrefixedURI;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Transactional
public class SesameRDFResourceRepository extends AbstractSesameRepository implements RDFResourceRepository {

	public SesameRDFResourceRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	@Override
	public boolean hasProperty( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			return statements.hasNext();
		} );
	}

	@Override
	public boolean hasProperty( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( PrefixedURI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				if ( statements.hasNext() ) return statements.hasNext();
			}
			return false;
		} );
	}

	@Override
	public boolean contains( final URI resourceURI, final URI pred, final Value obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, obj, false, documentURI );
			return statements.hasNext();
		} );
	}

	@Override
	public boolean contains( final URI resourceURI, final RDFNodeEnum pred, final Value obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( PrefixedURI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, obj, false, documentURI );
				if ( statements.hasNext() ) return statements.hasNext();
			}
			return false;
		} );
	}

	@Override
	public boolean contains( final URI resourceURI, final RDFNodeEnum pred, final RDFNodeEnum obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( PrefixedURI predURI : pred.getURIs() ) {
				for ( PrefixedURI objValue : obj.getURIs() ) {
					RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, objValue, false, documentURI );
					if ( statements.hasNext() ) return statements.hasNext();
				}
			}
			return false;
		} );
	}

	@Override
	public Value getProperty( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			if ( statements.hasNext() ) {
				return statements.next().getObject();
			}
			return null;
		} );
	}

	@Override
	public Value getProperty( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				if ( statements.hasNext() ) {
					return ( statements.next().getObject() );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Value> getProperties( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Value> properties = new LinkedHashSet<Value>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				properties.add( statements.next().getObject() );
			}
			return properties;
		} );
	}

	@Override
	public Set<Value> getProperties( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Value> properties = new LinkedHashSet<Value>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					properties.add( statements.next().getObject() );
				}
			}
			return properties;
		} );
	}

	@Override
	public URI getURI( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isURI( value ) ) return ValueUtil.getURI( value );
			}
			return null;
		} );
	}

	@Override
	public URI getURI( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI( value ) ) return ValueUtil.getURI( value );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<URI> getURIs( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<URI> properties = new LinkedHashSet<URI>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isURI( value ) ) properties.add( ValueUtil.getURI( value ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<URI> getURIs( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<URI> properties = new LinkedHashSet<URI>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isURI( value ) ) properties.add( ValueUtil.getURI( value ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Boolean getBoolean( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) )
					return Boolean.parseBoolean( value.stringValue() );
			}
			return null;
		} );
	}

	@Override
	public Boolean getBoolean( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) )
						return Boolean.parseBoolean( value.stringValue() );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Boolean> getBooleans( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Boolean> properties = new LinkedHashSet<Boolean>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) )
					properties.add( Boolean.parseBoolean( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Boolean> getBooleans( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Boolean> properties = new LinkedHashSet<Boolean>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isBoolean( (Literal) value ) )
						properties.add( Boolean.parseBoolean( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Byte getByte( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) )
					return Byte.parseByte( value.stringValue() );
			}
			return null;
		} );
	}

	@Override
	public Byte getByte( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) )
						return Byte.parseByte( value.stringValue() );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Byte> getBytes( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Byte> properties = new LinkedHashSet<Byte>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) )
					properties.add( Byte.parseByte( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Byte> getBytes( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Byte> properties = new LinkedHashSet<Byte>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isByte( (Literal) value ) )
						properties.add( Byte.parseByte( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public DateTime getDate( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) )
					return ( parser.parseDateTime( value.stringValue() ) );
			}
			return null;
		} );
	}

	@Override
	public DateTime getDate( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) )
						return ( parser.parseDateTime( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<DateTime> getDates( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<DateTime> properties = new LinkedHashSet<DateTime>();
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) )
					properties.add( parser.parseDateTime( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<DateTime> getDates( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<DateTime> properties = new LinkedHashSet<DateTime>();
			DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDate( (Literal) value ) )
						properties.add( parser.parseDateTime( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Double getDouble( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) )
					return ( Double.parseDouble( value.stringValue() ) );
			}
			return null;
		} );
	}

	@Override
	public Double getDouble( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) )
						return ( Double.parseDouble( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Double> getDoubles( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Double> properties = new LinkedHashSet<Double>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) )
					properties.add( Double.parseDouble( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Double> getDoubles( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Double> properties = new LinkedHashSet<Double>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isDouble( (Literal) value ) )
						properties.add( Double.parseDouble( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Float getFloat( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) )
					return ( Float.parseFloat( value.stringValue() ) );
			}
			return null;
		} );
	}

	@Override
	public Float getFloat( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) )
						return ( Float.parseFloat( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Float> getFloats( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Float> properties = new LinkedHashSet<Float>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) )
					properties.add( Float.parseFloat( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Float> getFloats( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Float> properties = new LinkedHashSet<Float>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isFloat( (Literal) value ) )
						properties.add( Float.parseFloat( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Integer getInteger( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) )
					return ( Integer.parseInt( value.stringValue() ) );
			}
			return null;
		} );
	}

	@Override
	public Integer getInteger( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) )
						return ( Integer.parseInt( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Integer> getIntegers( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Integer> properties = new LinkedHashSet<Integer>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) )
					properties.add( Integer.parseInt( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Integer> getIntegers( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Integer> properties = new LinkedHashSet<Integer>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isInteger( (Literal) value ) )
						properties.add( Integer.parseInt( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Long getLong( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) )
					return ( Long.parseLong( value.stringValue() ) );
			}
			return null;

		} );
	}

	@Override
	public Long getLong( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) )
						return ( Long.parseLong( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Long> getLongs( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Long> properties = new LinkedHashSet<Long>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) )
					properties.add( Long.parseLong( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Long> getLongs( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Long> properties = new LinkedHashSet<Long>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isLong( (Literal) value ) )
						properties.add( Long.parseLong( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public Short getShort( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) )
					return ( Short.parseShort( value.stringValue() ) );
			}
			return null;
		} );
	}

	@Override
	public Short getShort( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) )
						return ( Short.parseShort( value.stringValue() ) );
				}
			}
			return null;
		} );
	}

	@Override
	public Set<Short> getShorts( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Short> properties = new LinkedHashSet<Short>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) )
					properties.add( Short.parseShort( value.stringValue() ) );
			}
			return properties;
		} );
	}

	@Override
	public Set<Short> getShorts( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<Short> properties = new LinkedHashSet<Short>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isShort( (Literal) value ) )
						properties.add( Short.parseShort( value.stringValue() ) );

				}
			}
			return properties;
		} );
	}

	@Override
	public String getString( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) )
					return ( value.stringValue() );
			}
			return null;
		} );
	}

	@Override
	public String getString( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				if ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) )
						return ( value.stringValue() );
				}
			}
			return null;
		} );
	}

	@Override
	public String getString( final URI resourceURI, final URI pred, final Set<String> languages ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
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
		} );
	}

	@Override
	public String getString( final URI resourceURI, final RDFNodeEnum pred, final Set<String> languages ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
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
			}
			return null;
		} );
	}

	@Override
	public Set<String> getStrings( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<String> properties = new LinkedHashSet<String>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
			while ( statements.hasNext() ) {
				Value value = statements.next().getObject();
				if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) )
					properties.add( value.stringValue() );
			}
			return properties;
		} );
	}

	@Override
	public Set<String> getStrings( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<String> properties = new LinkedHashSet<String>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
				while ( statements.hasNext() ) {
					Value value = statements.next().getObject();
					if ( ValueUtil.isLiteral( value ) && LiteralUtil.isString( (Literal) value ) )
						properties.add( value.stringValue() );

				}
			}
			return properties;
		} );
	}

	@Override
	public Set<String> getStrings( final URI resourceURI, final URI pred, final Set<String> languages ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<String> properties = new LinkedHashSet<String>();
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, pred, null, false, documentURI );
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
		} );
	}

	@Override
	public Set<String> getStrings( final URI resourceURI, final RDFNodeEnum pred, final Set<String> languages ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<String> properties = new LinkedHashSet<String>();
			for ( URI predURI : pred.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predURI, null, false, documentURI );
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
			}
			return properties;
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, Value obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = obj;
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, DateTime obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void add( final URI resourceURI, final URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final RDFNodeEnum pred ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			for ( URI predURI : pred.getURIs() ) {
				connection.remove( resourceURI, predURI, null, documentURI );
			}
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, final Value obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, obj, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, final boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, DateTime obj ) {
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = LiteralUtil.get( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void remove( final URI resourceURI, final URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final URI documentURI = getDocumentURI( resourceURI );
		final Value literal = factory.createLiteral( obj, language );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, Value obj ) {
		final Value literal = obj;
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, boolean obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, byte obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, DateTime obj ) {
		final Value literal = LiteralUtil.get( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, double obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, float obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, int obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, long obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, short obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, String obj ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public void set( final URI resourceURI, final URI pred, String obj, String language ) {
		ValueFactory factory = ValueFactoryImpl.getInstance();
		final Value literal = factory.createLiteral( obj, language );
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, pred, null, documentURI );
			connection.add( resourceURI, pred, literal, documentURI );
		} );
	}

	@Override
	public boolean hasType( final URI resourceURI, final URI type ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			RepositoryResult<Statement> statements = connection.getStatements( resourceURI, null, type, false, documentURI );
			return statements.hasNext();
		} );
	}

	@Override
	public boolean hasType( final URI resourceURI, final RDFNodeEnum type ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			for ( URI typeURI : type.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, null, typeURI, false, documentURI );
				if ( statements.hasNext() ) return true;
			}
			return false;
		} );
	}

	@Override
	public Set<URI> getTypes( final URI resourceURI ) {
		final URI documentURI = getDocumentURI( resourceURI );
		return connectionTemplate.read( connection -> {
			Set<URI> types = new HashSet<URI>();
			for ( URI predicate : RDFResourceDescription.Property.TYPE.getURIs() ) {
				RepositoryResult<Statement> statements = connection.getStatements( resourceURI, predicate, null, false, documentURI );
				while ( statements.hasNext() ) {
					Statement statement = statements.next();
					Value object = statement.getObject();
					if ( ValueUtil.isURI( object ) ) types.add( ValueUtil.getURI( object ) );
				}
			}
			return types;
		} );
	}

	@Override
	public void addType( final URI resourceURI, final URI type ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.add( resourceURI, RDFResourceDescription.Property.TYPE.getURI(), type, documentURI );
		} );
	}

	@Override
	public void removeType( final URI resourceURI, final URI type ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, null, type, documentURI );
		} );
	}

	@Override
	public void setType( final URI resourceURI, final URI type ) {
		final URI documentURI = getDocumentURI( resourceURI );
		connectionTemplate.write( connection -> {
			connection.remove( resourceURI, null, type, documentURI );
			connection.add( resourceURI, RDFResourceDescription.Property.TYPE.getURI(), type, documentURI );
		} );
	}

	private URI getDocumentURI( URI resourceURI ) {
		if ( ! URIUtil.hasFragment( resourceURI ) ) return resourceURI;
		return new URIImpl( URIUtil.getDocumentURI( resourceURI.stringValue() ) );
	}

}
