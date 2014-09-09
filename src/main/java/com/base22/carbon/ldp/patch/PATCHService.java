package com.base22.carbon.ldp.patch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.repository.services.RepositoryService;
import com.base22.carbon.repository.services.WriteTransactionCallback;
import com.base22.carbon.repository.services.WriteTransactionTemplate;
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

	@PreAuthorize("hasPermission(#uriObject, 'EXTEND')")
	public void extendRDFSource(final URIObject uriObject, final PATCHRequest patchRequest, String datasetName) throws CarbonException {
		WriteTransactionTemplate template = repositoryService.getWriteTransactionTemplate(datasetName);
		//@formatter:off
		template.execute(new WriteTransactionCallback() {
			//@formatter:on
			@Override
			public void executeInTransaction(Dataset dataset) throws Exception {
				Model model = dataset.getNamedModel(uriObject.getURI());

				executeAddActions(patchRequest, model);
				executeSetActions(patchRequest, model);
				executeDeleteActions(patchRequest, model);
			}
		});
	}

	protected void executeAddActions(PATCHRequest patchRequest, Model domainModel) {
		List<Statement> statementsToAdd = new ArrayList<Statement>();
		for (AddAction action : patchRequest.getAddActions()) {
			String realURI = action.getSubjectURI();
			Resource realSubject = ResourceFactory.createResource(realURI);

			StmtIterator iterator = action.getResource().listProperties();
			while (iterator.hasNext()) {
				Statement statement = changeSubject(realSubject, iterator.next());
				Property predicate = statement.getPredicate();

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

	protected void executeSetActions(PATCHRequest patchRequest, Model domainModel) {
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

	protected void executeDeleteActions(PATCHRequest patchRequest, Model domainModel) {
		List<Statement> statementsToDelete = new ArrayList<Statement>();
		for (DeleteAction action : patchRequest.getDeleteActions()) {
			String realURI = action.getSubjectURI();
			Resource realSubject = ResourceFactory.createResource(realURI);

			StmtIterator iterator = action.getResource().listProperties();
			while (iterator.hasNext()) {
				Statement statement = changeSubject(realSubject, iterator.next());
				Property predicate = statement.getPredicate();

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
			Model domainModel, Statement statement) {
		switch (actionProperty) {
			case ALL_VALUES_OF:
				deleteAllValues(action, domainModel, statement);
				break;
			default:
				break;

		}
	}

	private void deleteAllValues(DeleteAction action, Model domainModel, Statement statement) {
		Resource subject = statement.getSubject();
		RDFNode object = statement.getObject();

		if ( ! object.isURIResource() ) {
			// TODO: Throw exception
			return;
		}

		Property property = ResourceFactory.createProperty(object.asResource().getURI());
		domainModel.getResource(subject.getURI()).removeAll(property);
	}

	private Statement changeSubject(Resource newSubject, Statement statement) {
		Property predicate = statement.getPredicate();
		RDFNode object = statement.getObject();

		return ResourceFactory.createStatement(newSubject, predicate, object);

	}
}
