package org.openmrs.module.mohorderentrybridge;

import org.openmrs.Concept;

/**
 * This class extends {@link Concept} and is meant to provide Concept method returns which are needed at client level as variables instead
 * to support Tomcat6 container which can't invoke the methods themselves
 */
public class MoHConcept {
	
	private Concept concept;
	
	private String name;
	
	public MoHConcept(Concept concept) {
		setConcept(concept);
		setName(concept.getName().getName());
	}

	public Concept getConcept() {
		return concept;
	}

	public void setConcept(Concept concept) {
		this.concept = concept;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
