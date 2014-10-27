package com.base22.carbon.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Random;
import java.util.Set;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ModelUtilTest {

	@Test
	public void testRemoveServerManagedProperties() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveSystemResources() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCreateDetachedCopy() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRenameResource() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRenameBase() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetURIResourcesWithBase() {
		Resource[] resourcesWithTheBase = new Resource[10], resourcesWithAnotherBase = new Resource[15];
		String base = "http://example.org/baseToSearchFor/";
		String anotherBase = "http://example.org/another/base";
		Property exampleProperty = ResourceFactory.createProperty("http://example.org/ns#property");
		Random randomGenerator = new Random();

		Model model = ModelFactory.createDefaultModel();

		// Create the resources with the base
		for (int i = 0; i < resourcesWithTheBase.length; i++) {
			StringBuilder uriBuilder = new StringBuilder();
			//@formatter:off
			uriBuilder
				.append(base)
				.append(String.valueOf(randomGenerator.nextLong()))
			;
			//@formatter:on
			resourcesWithTheBase[i] = model.createResource(uriBuilder.toString());
		}

		// Create the resources with the other base
		for (int i = 0; i < resourcesWithAnotherBase.length; i++) {
			StringBuilder uriBuilder = new StringBuilder();
			//@formatter:off
			uriBuilder
				.append(anotherBase)
				.append(String.valueOf(randomGenerator.nextLong()))
			;
			//@formatter:on
			resourcesWithAnotherBase[i] = model.createResource(uriBuilder.toString());
		}

		int resourcesAdded = 0;
		// Create random relations
		for (int i = 0; i < resourcesWithTheBase.length; i++) {
			Resource resourceWithTheBase = resourcesWithTheBase[i];
			boolean added = false;
			for (int j = 0; j < resourcesWithAnotherBase.length; j++) {
				Resource resourceWithAnotherBase = resourcesWithAnotherBase[j];

				if ( randomGenerator.nextBoolean() ) {
					resourceWithAnotherBase.addProperty(exampleProperty, resourceWithTheBase);
					added = true;
				}
				if ( randomGenerator.nextBoolean() ) {
					resourceWithTheBase.addProperty(exampleProperty, resourceWithAnotherBase);
					added = true;
				}
			}
			if ( added ) resourcesAdded++;
		}

		Set<Resource> resourcesReturned = ModelUtil.getURIResourcesWithBase(base, model);

		assertEquals(resourcesReturned.size(), resourcesAdded);
	}
}
