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

import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.h2.jdbc.JdbcSQLException;
import org.hibernate.cfg.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

/**
 * Tests {@link ${MoHOrderEntryBridgeService}}.
 * 
 * Uses OpenMRS 1.11.x API and TEST
 */
public class MoHOrderEntryBridgeServiceTest extends BaseModuleContextSensitiveTest {

	private OrderService orderService = null;
	private ConceptService conceptService = null;
	private EncounterService encounterService = null;
	private ProviderService providerService = null;
	private PatientService patientService = null;

	/**
	 * Supposed to Fix; "Caused by: org.h2.jdbc.JdbcSQLException: Timeout trying to lock table ; SQL statement:"
	 * @see org.openmrs.test.BaseContextSensitiveTest#getRuntimeProperties()
	 */
	@Override
	public Properties getRuntimeProperties() {
	    Properties props = super.getRuntimeProperties();
	    String url = props.getProperty(Environment.URL);
	    
	    if (url.contains("jdbc:h2:") && !url.contains(";MVCC=TRUE")) {
	        props.setProperty(Environment.URL, url + ";MVCC=TRUE");
	    }
	    /*if (url.contains("jdbc:h2:") && !url.contains(";MULTI_THREADED=")) {
	        props.setProperty(Environment.URL, url + ";MULTI_THREADED=0");
	    }
	    url = props.getProperty(Environment.URL);
	    if (url.contains("jdbc:h2:") && url.contains(";LOCK_TIMEOUT=")) {
	    	String newUrl = "";
	    	List<String> urlPortions = Arrays.asList(url.split(";"));
	        
	    	for(int i = 0; i < urlPortions.size(); i++) {
	    		String urlPortion = urlPortions.get(i);
	    		if(urlPortion.startsWith("LOCK_TIMEOUT=")) {
	    			newUrl += "LOCK_TIMEOUT=" + Integer.MAX_VALUE;
	    		} else {
	    			newUrl += urlPortion;
	    		}
	    		if(i != urlPortions.size() - 1) {
	    			newUrl += ";";
	    		}
	    	}
	    	props.setProperty(Environment.URL, newUrl);
	    }*/
	    url = props.getProperty(Environment.URL);
	    return props;
	}
	
	@Before
	public void setupDatabase() throws Exception {
		orderService = Context.getOrderService();
		patientService = Context.getPatientService();
		providerService = Context.getProviderService();
		encounterService = Context.getEncounterService();
		conceptService = Context.getConceptService();

		initializeInMemoryDatabase();
		authenticate();
		executeDataSet("org/openmrs/include/standardTestDataset.xml");
	}

	@Test
	public void shouldSetupContext() {
		assertNotNull(Context.getService(MoHOrderEntryBridgeService.class));
	}

	@Test
	public void testOpenMRSVersion() {// should be 1.11.5 and not 1.6.6
		Assert.assertTrue(OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.11.5"));
		Assert.assertFalse(OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.6.6"));
	}

	@Test
	public void theTwoOrderServicesMustBeAccessible() {
		Assert.assertNotNull(orderService);
		Assert.assertNotNull(OrderContextVersion6.getAPI6ContextOrderService());
	}

	@Test(expected = JdbcSQLException.class)
	public void failLoadingAPI6DataSet() throws Exception {
		executeDataSet("DrugOrders-1.6.x-data.xml");
	}

	/**
	 * Tests the assumptions made in upgradeAssumptions.md file
	 * 
	 * @throws Exception
	 */
	@Ignore
	public void testAlltheMoHUpgradeAssumptions_1() throws Exception {
		Patient patient2 = patientService.getPatient(2);
		List<Order> patient2Orders = orderService.getAllOrdersByPatient(patient2);
		Integer patient2OrdersOriginalCount = patient2Orders.size();
		Integer patient2OrdersFinalCount = null;
		Order order22 = orderService.getOrder(22);// DISCONTINUED
		
		DrugOrder newDrugOrder = new DrugOrder();//All the fields bellow should be set for a new order by MoH modules
		newDrugOrder.setOrderType(orderService.getOrderTypeByUuid("dd3fb1d0-ae06-11e3-a5e2-0800200c9a77"));
		newDrugOrder.setDateActivated(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);// expires the next day
		//newDrugOrder.setAction(Action.NEW);
		newDrugOrder.setAutoExpireDate(cal.getTime());
		newDrugOrder.setOrderer(providerService.getProvider(1));
		newDrugOrder.setEncounter(encounterService.getEncounter(6));
		newDrugOrder.setOrderReasonNonCoded("REASON");
		newDrugOrder.setPatient(patient2);
		newDrugOrder.setRoute(conceptService.getConcept(22));
		newDrugOrder.setFrequency(orderService.getOrderFrequency(1));
		newDrugOrder.setDose(2d);
		newDrugOrder.setDoseUnits(conceptService.getConcept(50));
		newDrugOrder.setDrug(conceptService.getDrug(3));
		newDrugOrder.setCareSetting(orderService.getCareSetting(2));
		newDrugOrder.setPreviousOrder(orderService.getOrderByOrderNumber("111"));

		Order savedOrder = orderService.saveOrder(newDrugOrder, null);
		Assert.assertFalse(order22.isActive());// isDiscontinued
		Assert.assertTrue(savedOrder.isActive());// is not discontinue
		
		//Order discontinuedOrder = orderService.discontinueOrder(savedOrder, savedOrder.getConcept(), new Date(),
				//savedOrder.getOrderer(), savedOrder.getEncounter());

		//Assert.assertFalse(discontinuedOrder.isActive());
		Assert.assertFalse(savedOrder.isActive());// is discontinue
		patient2OrdersFinalCount = orderService.getAllOrdersByPatient(patient2).size();

		Assert.assertTrue(patient2OrdersFinalCount - patient2OrdersOriginalCount == 1);
	}
}
