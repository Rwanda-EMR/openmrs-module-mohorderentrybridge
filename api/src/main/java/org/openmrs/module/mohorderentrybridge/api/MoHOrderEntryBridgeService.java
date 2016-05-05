/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.mohorderentrybridge.api;

import java.util.List;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.mohorderentrybridge.MoHConcept;
import org.openmrs.module.mohorderentrybridge.MoHDrugOrder;
import org.openmrs.module.mohorderentrybridge.MoHOrderFrequency;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean which is configured in moduleApplicationContext.xml.
 * <p>
 * It can be accessed only via Context:<br>
 * <code>
 * Context.getService(MoHOrderEntryBridgeService.class).someMethod();
 * </code>
 * 
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface MoHOrderEntryBridgeService extends OpenmrsService {

	/**
     * Excludes DISCONTINUATION AND REVISION orders
     */
	public List<MoHDrugOrder> getMoHDrugOrdersByPatient(Patient patient);

	/**
     * Excludes DISCONTINUATION order
     */
	public List<DrugOrder> getDrugOrdersByPatient(Patient patient);

	/**
	 * TODO, this is probably not good at-all for sites where data entry is done afterwards by a data clerk
	 */
	public Provider getFirstCurrentProvider();

	public List<MoHConcept> convertConceptsListToMoHConceptsList(List<Concept> concepts);

	public List<MoHOrderFrequency> getMoHOrderFrequencies(boolean includeRetired);

}