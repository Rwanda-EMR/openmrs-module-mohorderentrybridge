package org.openmrs.module.mohorderentrybridge;

import org.openmrs.Concept;

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
