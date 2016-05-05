package org.openmrs.module.mohorderentrybridge.liquibase;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptSet;
import org.openmrs.GlobalProperty;
import org.openmrs.OrderFrequency;
import org.openmrs.api.context.Context;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/**
 * This class is responsible for making use of all metadata from the concept
 * dictionary CIEL to be more specific And it is ran as a liquibase changeset
 * 
 * @TODO, can't be run until https://talk.openmrs.org/t/issues-with-drug-frequency-coded-concept/6165/1 is ressolved
 *
 */
public class ConfigureMetadataFromCIEL implements CustomTaskChange {

	private Integer CIEL_DRUG_ORDER_FREQUENCY_CODED = 3320;

	private String FREQUENCIES_SET_UUID = "f73e5638-859d-4fcb-80da-82a68c90d4b5";

	@Override
	public void execute(Database database) throws CustomChangeException {
		String propertyName = "mohorderentrybridge.cielMetadata.configured";
		GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(propertyName);

		if (gp == null) {
			gp = new GlobalProperty();
			gp.setProperty(propertyName);
			gp.setPropertyValue("false");
		}
		if (configureMetadataFromCielToBeUsable()) {
			gp.setPropertyValue("true");
		}
		Context.getAdministrationService().saveGlobalProperty(gp);
	}

	/**
	 * TO BE Run once as a liquibase changeset
	 * 
	 * @return
	 */
	private boolean configureMetadataFromCielToBeUsable() {
		boolean configured = false;
		Integer originalOfreqs = Context.getOrderService().getOrderFrequencies(true).size();
		Integer finalOfreqs;

		createOrderFrequenciesFromCiel();
		finalOfreqs = Context.getOrderService().getOrderFrequencies(true).size();

		if (finalOfreqs > originalOfreqs) {
			configured = true;
		}
		return configured;
	}

	private void createOrderFrequenciesFromCiel() {
		Concept cielOrderFreqs = Context.getConceptService().getConcept(CIEL_DRUG_ORDER_FREQUENCY_CODED);

		if (cielOrderFreqs != null) {
			for (ConceptAnswer ca : cielOrderFreqs.getAnswers()) {
				Concept c = ca.getAnswerConcept();
				OrderFrequency of = new OrderFrequency();
				Concept orderFreqsSetConcept = Context.getConceptService().getConceptByUuid(FREQUENCIES_SET_UUID);
				
				if (c != null) {
					c.setDatatype(Context.getConceptService().getConceptDatatypeByName("Text"));
					c.setConceptClass(Context.getConceptService().getConceptClassByName("Frequency"));
					c = Context.getConceptService().saveConcept(c);//TODO suggest CIEL to make this Class change on talk
					of.setDateCreated(new Date());
					of.setCreator(Context.getAuthenticatedUser());
					of.setConcept(c);
					Context.getOrderService().saveOrderFrequency(of);
				}
				
				if(orderFreqsSetConcept != null && orderFreqsSetConcept.isSet()) {
					orderFreqsSetConcept = addConceptSetMember(orderFreqsSetConcept, c, 0.0);
				}
			}
		}
	}

	private Concept addConceptSetMember(Concept set, Concept member, Double sortWeight) {
		// get all concepts in a
		// set#conceptService.getConceptsByConceptSet(concept);
		if (set != null && member != null && sortWeight != null) {
			ConceptSet setMember = new ConceptSet(member, sortWeight);
			setMember.setCreator(Context.getAuthenticatedUser());
			setMember.setDateCreated(new Date());
			set.getConceptSets().add(setMember);
			set.setConceptSets(set.getConceptSets());
			Context.getConceptService().saveConcept(set);

			return Context.getConceptService().getConcept(set.getId());
		} else
			return null;
	}

	@Override
	public String getConfirmationMessage() {
		return "Finished Configuring usable metadata from the current Concept Dictionary";
	}

	@Override
	public void setUp() throws SetupException {
	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

}
