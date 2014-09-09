package com.base22.carbon.sparql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.base22.carbon.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.hp.hpl.jena.rdf.model.Model;

public class SPARQLExecutor {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected void enterModelCriticalSection(Model domainModel, boolean read) throws CarbonException {
		try {
			domainModel.enterCriticalSection(read);
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx enterModelCriticalSection() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- enterModelCriticalSection() > The Model couldn't be locked.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}

	protected void leaveModelCriticalSection(Model domainModel) throws CarbonException {
		try {
			domainModel.leaveCriticalSection();
		} catch (Exception e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx enterModelCriticalSection() > Exception Stacktrace:", e);
			}

			if ( LOG.isErrorEnabled() ) {
				LOG.error("-- enterModelCriticalSection() > The Model couldn't be locked.");
			}
			String friendlyMessage = "An unexpected error ocurred.";

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			throw new CarbonException(errorObject);
		}
	}
}
