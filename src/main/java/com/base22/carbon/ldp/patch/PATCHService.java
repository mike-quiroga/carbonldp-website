package com.base22.carbon.ldp.patch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.TransactionNamedModelCache;
import com.base22.carbon.repository.WriteTransactionCallback;
import com.base22.carbon.repository.WriteTransactionTemplate;
import com.base22.carbon.repository.services.RepositoryService;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@Service("patchService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PATCHService {

	@Autowired
	protected RepositoryService repositoryService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public void executePATCHRequest(final URIObject uriObject, final PATCHRequest patchRequest, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		executePATCHRequest(uriObject, patchRequest, datasetName, template);
		template.execute();
	}

	public void executePATCHRequest(final URIObject uriObject, final PATCHRequest patchRequest, String datasetName, WriteTransactionTemplate template)
			throws CarbonException {
		template.addCallback(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception {
				Model model = namedModelCache.getNamedModel(uriObject.getURI());

				executeAddActions(uriObject, patchRequest, model);
				executeSetActions(uriObject, patchRequest, model);
				executeDeleteActions(uriObject, patchRequest, model);
			}
		});
	}

	protected void executeAddActions(URIObject uriObject, PATCHRequest patchRequest, Model domainModel) throws CarbonException {
		List<Statement> statementsToAdd = new ArrayList<Statement>();
		for (AddAction action : patchRequest.getAddActions()) {
			String realURI = action.getSubjectURI();
			Resource realSubject = ResourceFactory.createResource(realURI);

			StmtIterator iterator = action.getResource().listProperties();
			while (iterator.hasNext()) {
				Statement statement = changeSubject(realSubject, iterator.next());
				Property predicate = statement.getPredicate();

				validateStatement(uriObject, statement);

				com.base22.carbon.ldp.patch.AddActionClass.Properties actionProperty = AddActionClass.Properties.findByURI(predicate.getURI());
				if ( actionProperty != null ) {
					executeSpecialAddActionProperty(action, actionProperty, domainModel, statement);
				} else {
					statementsToAdd.add(statement);
				}
			}
		}
		domainModel.add(statementsToAdd);
	}

	private void executeSpecialAddActionProperty(AddAction action, com.base22.carbon.ldp.patch.AddActionClass.Properties actionProperty, Model domainModel,
			Statement statement) {
		switch (actionProperty) {

		}
	}

	protected void executeSetActions(URIObject uriObject, PATCHRequest patchRequest, Model domainModel) throws CarbonException {
		List<Statement> statementsToAdd = new ArrayList<Statement>();
		for (SetAction action : patchRequest.getSetActions()) {
			String realURI = action.getSubjectURI();
			Resource realSubject = ResourceFactory.createResource(realURI);

			Set<Property> propertiesDeleted = new HashSet<Property>();
			StmtIterator iterator = action.getResource().listProperties();
			while (iterator.hasNext()) {
				Statement statement = changeSubject(realSubject, iterator.next());
				Resource resourceToModify = domainModel.getResource(realURI);
				Property predicate = statement.getPredicate();

				validateStatement(uriObject, statement);

				com.base22.carbon.ldp.patch.SetActionClass.Properties actionProperty = SetActionClass.Properties.findByURI(predicate.getURI());
				if ( actionProperty != null ) {
					executeSpecialSetActionProperty(action, actionProperty, domainModel, statement);
				} else {
					Property propertyToSet = statement.getPredicate();
					if ( ! propertiesDeleted.contains(propertyToSet) ) {
						resourceToModify.removeAll(propertyToSet);
						propertiesDeleted.add(propertyToSet);
					}

					statementsToAdd.add(statement);
				}
			}
		}
		domainModel.add(statementsToAdd);
	}

	private void executeSpecialSetActionProperty(SetAction action, com.base22.carbon.ldp.patch.SetActionClass.Properties actionProperty, Model domainModel,
			Statement statement) {
		switch (actionProperty) {

		}
	}

	protected void executeDeleteActions(URIObject uriObject, PATCHRequest patchRequest, Model domainModel) throws CarbonException {
		List<Statement> statementsToDelete = new ArrayList<Statement>();
		for (DeleteAction action : patchRequest.getDeleteActions()) {
			String realURI = action.getSubjectURI();
			Resource realSubject = ResourceFactory.createResource(realURI);

			StmtIterator iterator = action.getResource().listProperties();
			while (iterator.hasNext()) {
				Statement statement = changeSubject(realSubject, iterator.next());
				Property predicate = statement.getPredicate();

				validateStatement(uriObject, statement);

				com.base22.carbon.ldp.patch.DeleteActionClass.Properties actionProperty = DeleteActionClass.Properties.findByURI(predicate.getURI());
				if ( actionProperty != null ) {
					executeSpecialDeleteActionProperty(action, actionProperty, domainModel, statement);
				} else {
					statementsToDelete.add(statement);
				}
			}
		}
		domainModel.remove(statementsToDelete);
	}

	private void executeSpecialDeleteActionProperty(DeleteAction action, com.base22.carbon.ldp.patch.DeleteActionClass.Properties actionProperty,
			Model domainModel, Statement statement) throws CarbonException {
		switch (actionProperty) {
			case ALL_VALUES_OF:
				deleteAllValues(action, domainModel, statement);
				break;
			default:
				break;

		}
	}

	private void deleteAllValues(DeleteAction action, Model domainModel, Statement statement) throws CarbonException {
		Resource subject = statement.getSubject();
		RDFNode object = statement.getObject();

		if ( ! object.isURIResource() ) {
			String friendlyMessage = "The PATCH request has an invalid statement.";
			String debugMessage = MessageFormat.format("The special property: ''{0}'', must point to an object node.",
					DeleteActionClass.Properties.ALL_VALUES_OF.getPrefixedURI().getShortVersion());

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			throw new CarbonException(errorObject);
		}

		Property property = ResourceFactory.createProperty(object.asResource().getURI());
		domainModel.getResource(subject.getURI()).removeAll(property);
	}

	private Statement changeSubject(Resource newSubject, Statement statement) {
		Property predicate = statement.getPredicate();
		RDFNode object = statement.getObject();

		return ResourceFactory.createStatement(newSubject, predicate, object);
	}

	private void validateStatement(URIObject uriObject, Statement statement) throws CarbonException {
		Resource subject = statement.getSubject();
		Property predicate = statement.getPredicate();

		if ( ! subjectBelongsToDocument(uriObject, subject) ) {
			String friendlyMessage = "The PATCH request has an invalid statement.";
			String debugMessage = MessageFormat.format("The subject: ''{0}'', doesn't belong to the RDFSource document.", subject.getURI());

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			throw new CarbonException(errorObject);
		}
	}

	private boolean subjectBelongsToDocument(URIObject uriObject, Resource subject) {
		String subjectURI = subject.getURI();

		if ( ! subjectURI.startsWith(uriObject.getURI()) ) {
			return false;
		}

		if ( subjectURI.replace(uriObject.getURI(), "").startsWith("/") ) {
			return false;
		}

		return true;
	}
}
