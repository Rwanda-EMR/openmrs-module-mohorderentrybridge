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
package org.openmrs.module.mohorderentrybridge.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Order.Action;
import org.openmrs.OrderFrequency;
import org.openmrs.Patient;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.mohorderentrybridge.MoHConcept;
import org.openmrs.module.mohorderentrybridge.MoHDrugOrder;
import org.openmrs.module.mohorderentrybridge.MoHOrderFrequency;
import org.openmrs.module.mohorderentrybridge.api.MoHOrderEntryBridgeService;
import org.openmrs.module.mohorderentrybridge.api.db.MoHOrderEntryBridgeDAO;

/**
 * It is a default implementation of {@link MoHOrderEntryBridgeService}.
 */
public class MoHOrderEntryBridgeServiceImpl extends BaseOpenmrsService implements MoHOrderEntryBridgeService {

	protected final Log log = LogFactory.getLog(this.getClass());

	private MoHOrderEntryBridgeDAO dao;

	/**
	 * @param dao
	 *            the dao to set
	 */
	public void setDao(MoHOrderEntryBridgeDAO dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public MoHOrderEntryBridgeDAO getDao() {
		return dao;
	}

	@Override
	public List<MoHDrugOrder> getMoHDrugOrdersByPatient(Patient patient) {
		List<Order> orderList = Context.getOrderService().getAllOrdersByPatient(patient);
		List<MoHDrugOrder> drugOrders = new ArrayList<MoHDrugOrder>();

		List<Patient> patients = new Vector<Patient>();
		patients.add(patient);
		for (Order order : orderList) {
			if ("org.openmrs.DrugOrder".equals(order.getOrderType().getJavaClassName()) && order instanceof DrugOrder
					&& !order.getAction().equals(Action.DISCONTINUE)) {
				MoHDrugOrder mohDOrder = new MoHDrugOrder((DrugOrder) order);

				drugOrders.add(mohDOrder);
			}
		}
		return drugOrders;
	}

	@Override
	public List<DrugOrder> getDrugOrdersByPatient(Patient patient) {
		List<MoHDrugOrder> mohDrugOrders = getMoHDrugOrdersByPatient(patient);
		List<DrugOrder> drugOrders = new ArrayList<DrugOrder>();

		for (MoHDrugOrder mohDrugOrder : mohDrugOrders) {
			drugOrders.add(mohDrugOrder.getDrugOrder());
		}
		return drugOrders;
	}

	@Override
	public Provider getFirstCurrentProvider() {
		Collection<Provider> activPproviders = Context.getProviderService()
				.getProvidersByPerson(Context.getAuthenticatedUser().getPerson());
		Provider provider = null;

		for (Provider prov : activPproviders) {
			provider = prov;
			break;
		}

		return provider;
	}

	@Override
	public List<MoHConcept> convertConceptsListToMoHConceptsList(List<Concept> concepts) {
		List<MoHConcept> mohConcepts = new ArrayList<MoHConcept>();

		for (Concept c : concepts) {
			mohConcepts.add(new MoHConcept(c));
		}

		return mohConcepts;
	}

	@Override
	public List<MoHOrderFrequency> getMoHOrderFrequencies(boolean includeRetired) {
		List<MoHOrderFrequency> mOFreqs = new ArrayList<MoHOrderFrequency>();
		List<OrderFrequency> oFreqs = Context.getOrderService().getOrderFrequencies(includeRetired);
		
		for(OrderFrequency freq : oFreqs) {
			if(freq != null) {
				mOFreqs.add(new MoHOrderFrequency(freq));
			}
		}
		return mOFreqs;
	}
}