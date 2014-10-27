package com.base22.carbon.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.base22.carbon.Carbon;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
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

		model.remove(statementsToRemove);
		model.add(statementsToAdd);

		statementsToRemove = new ArrayList<Statement>();
		statementsToAdd = new ArrayList<Statement>();

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

	// TODO: Optimization Opportunity Area
	// Is it faster to do this using SPARQL?
	public static Model renameBase(String originalBase, String newBase, Model model) {
		Set<Resource> affectedResources = getURIResourcesWithBase(originalBase, model);
		Iterator<Resource> resourcesIterator = affectedResources.iterator();
		while (resourcesIterator.hasNext()) {
			Resource affectedResource = resourcesIterator.next();
			String oldSlug = affectedResource.getURI().replace(originalBase, "");
			StringBuilder newURIBuilder = new StringBuilder();
			if ( ! (oldSlug.startsWith(Carbon.TRAILING_SLASH) || oldSlug.startsWith(Carbon.EXTENDING_RESOURCE_SIGN)) ) {
				int lastIndex = newBase.lastIndexOf(Carbon.TRAILING_SLASH);
				if ( (lastIndex + 1) == newBase.length() ) newURIBuilder.append(newBase);
				else newURIBuilder.append(newBase.substring(0, lastIndex + 1));
			} else newURIBuilder.append(newBase);
			newURIBuilder.append(oldSlug);

			renameResource(affectedResource, newURIBuilder.toString(), model);
		}

		return model;
	}

	public static Set<Resource> getURIResourcesWithBase(String base, Model model) {
		Set<Resource> nodes = new HashSet<Resource>();

		NodeIterator nodeIterator = model.listObjects();
		while (nodeIterator.hasNext()) {
			RDFNode objectNode = nodeIterator.next();
			if ( nodeStartsWith(base, objectNode) ) nodes.add(objectNode.asResource());
		}

		ResIterator subjectsIterator = model.listSubjects();
		while (subjectsIterator.hasNext()) {
			RDFNode subjectNode = subjectsIterator.next();
			if ( nodeStartsWith(base, subjectNode) ) nodes.add(subjectNode.asResource());
		}

		return nodes;
	}

	private static boolean nodeStartsWith(String base, RDFNode node) {
		if ( ! node.isURIResource() ) return false;
		String nodeURI = node.asResource().getURI();
		if ( ! nodeURI.startsWith(base) ) return false;
		if ( ! base.endsWith(Carbon.TRAILING_SLASH) ) {
			String nodeSlug = nodeURI.replace(base, "");
			if ( ! (nodeSlug.startsWith(Carbon.TRAILING_SLASH) || nodeSlug.startsWith(Carbon.EXTENDING_RESOURCE_SIGN)) ) return false;
		}
		return true;
	}
}
