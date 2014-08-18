package com.base22.carbon.utils;

import com.base22.carbon.constants.Carbon;
import com.hp.hpl.jena.rdf.model.Model;
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
}
