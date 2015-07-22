package com.carbonldp.web.cors;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextRepository;
import com.carbonldp.namespaces.C;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import java.util.Set;
import java.util.regex.Pattern;

public class CORSAppContextFilter extends CORSContextFilter {
	public static final String FILTER_APPLIED = "__carbon_cacf_applied";

	private AppContextRepository appContextRepository;

	//TODO use the right parameters
	public CORSAppContextFilter() {
		super( FILTER_APPLIED );
	}

	@Override
	protected boolean isOriginAllowed( String origin ) {
		Set<Value> allowedOrigins = getAllowedOrigins();
		if ( allowedOrigins.isEmpty() ) return false;

		for ( Value allowedOrigin : allowedOrigins ) {
			if ( ValueUtil.isLiteral( allowedOrigin ) ) {
				Literal allowedOriginLiteral = ValueUtil.getLiteral( allowedOrigin );
				if ( LiteralUtil.isString( allowedOriginLiteral ) ) {
					if ( origin.equals( allowedOriginLiteral.stringValue() ) ) return true;
				} else if ( LiteralUtil.isRegularExpression( allowedOriginLiteral ) ) {
					if ( Pattern.matches( allowedOriginLiteral.stringValue(), origin ) ) return true;
				}
			} else if ( ValueUtil.isURI( allowedOrigin ) ) {
				return allowedOrigin.equals( C.Class.ALL_ORIGINS.getURI() );
			}
		}

		return false;
	}

	private Set<Value> getAllowedOrigins() {
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) throw new IllegalStateException( "The filter needs to execute inside of an appContext." );
		App app = appContext.getApplication();
		return app.getDomains();
	}
}
