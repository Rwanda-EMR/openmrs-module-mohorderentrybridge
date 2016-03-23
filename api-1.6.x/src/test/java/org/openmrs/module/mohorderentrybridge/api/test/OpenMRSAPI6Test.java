package org.openmrs.module.mohorderentrybridge.api.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.api.context.Context;
import org.openmrs.module.mohorderentrybridge.api.OrderContextVersion6;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.util.OpenmrsConstants;

/**
 * Uses OpenMRS 1.6.x API and TEST, for more 1.6.x tests, see https://github.com/openmrs/openmrs-core/tree/1.6.6/test
 */
public class OpenMRSAPI6Test extends BaseModuleContextSensitiveTest {

	@Before
	public void setupDatabase() throws Exception {
		initializeInMemoryDatabase();
		authenticate();
		executeDataSet("org/openmrs/include/standardTestDataset.xml");
		executeDataSet("DrugOrders-1.6.x-data.xml");
	}
	
	@Test
	public void testOpenMRSVersion() {//should be 1.6.6 and not 1.11.5
		Assert.assertTrue(OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.6.6"));
		Assert.assertFalse(OpenmrsConstants.OPENMRS_VERSION_SHORT.startsWith("1.11.5"));
	}
	
	@Test
	public void drugOrdersObjectMustBeAccessible() {
		Assert.assertNotNull(Context.getOrderService());
	}
	
	@Test
	public void testOldDrugOrdersBasicProperties() {
		DrugOrder do5 = Context.getOrderService().getDrugOrder(10005);
		Order order = Context.getOrderService().getOrder(do5.getOrderId());
		
		Assert.assertNotNull(Context.getOrderService().getDrugOrder(10001));
		Assert.assertNotNull(do5);
		Assert.assertTrue(do5.getDose().equals(325.0));
		Assert.assertTrue(do5.getFrequency().equals("1/day x 7 days/week"));
		Assert.assertNotNull(order.getStartDate().toString().equals("2008-08-19 00:00:00.0"));
		Assert.assertFalse(order.getDiscontinued());
		Assert.assertTrue(Context.getOrderService().getOrder(10004).getDiscontinued());
	}
	
	@Test
	public void test_API6ContextShouldBeAccessible() {
		Assert.assertNotNull(OrderContextVersion6.getAPI6ContextOrderService());
	}
}
