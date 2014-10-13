package com.base22.carbon.ldp;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.Carbon;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

public class ModelUtil {
	public static void removeServerManagedProperties(Model model) {
		// TODO: Implement
	}

	public static void removeSystemResources(Model model) {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
			.append("DELETE {")
				.append("\n\t?asSubjectS ?asSubjectP ?asSubjectO.")
				.append("\n\t?asObjectS ?asObjectP ?asObjectO.")
			.append("\n} WHERE {")
				.append("\n\tOPTIONAL {")
					.append("\n\t\t?asSubjectS ?asSubjectP ?asSubjectO.")
					.append("\n\t\tFILTER(")
						.append(" CONTAINS(STR(?asSubjectS), \"" + Carbon.SYSTEM_RESOURCE_SIGN + "\" )")
					.append(" )")
				.append("\n\t}.")
				.append("\n\tOPTIONAL {")
					.append("\n\t\t?asObjectS ?asObjectP ?asObjectO.")
					.append("\n\t\tFILTER(")
						.append(" CONTAINS(STR(?asObjectO), \"" + Carbon.SYSTEM_RESOURCE_SIGN + "\" )")
					.append(" )")
				.append("\n\t}.")
			.append("\n}")
		;
		//@formatter:on

		String query = queryBuilder.toString();

		UpdateRequest update = UpdateFactory.create(query);
		UpdateAction.execute(update, model);
	}

	public static Model createDetachedCopy(Model atachedModel) {
		return atachedModel.difference(ModelFactory.createDefaultModel());
	}

	public static Resource renameResource(Resource resource, String newURI, Model model) {
		Resource nullResource = null;
		Property nullProperty = null;

		Resource newResource = ResourceFactory.createResource(newURI);

		List<Statement> statementsToAdd = new ArrayList<Statement>();
		List<Statement> statementsToRemove = new ArrayList<Statement>();

		StmtIterator subjectIterator = model.listStatements(resource, nullProperty, nullResource);
		while (subjectIterator.hasNext()) {
			Statement statementToRemove = subjectIterator.next();
			Statement statementToAdd = ResourceFactory.createStatement(newResource, statementToRemove.getPredicate(), statementToRemove.getObject());

			statementsToRemove.add(statementToRemove);
			statementsToAdd.add(statementToAdd);
		}

		StmtIterator objectIterator = model.listStatements(nullResource, nullProperty, resource);
		while (objectIterator.hasNext()) {
			Statement statementToRemove = objectIterator.next();
			Statement statementToAdd = ResourceFactory.createStatement(statementToRemove.getSubject(), statementToRemove.getPredicate(), newResource);

			statementsToRemove.add(statementToRemove);
			statementsToAdd.add(statementToAdd);
		}

		model.remove(statementsToRemove);
		model.add(statementsToAdd);

		newResource = model.getResource(newURI);

		return newResource;
	}
}
