package org.openmrs.module.mohorderentrybridge.api;

import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;

public class OrderContextVersion6 {
	
	public static OrderService getAPI6ContextOrderService() {
		return Context.getOrderService();
	}
}
