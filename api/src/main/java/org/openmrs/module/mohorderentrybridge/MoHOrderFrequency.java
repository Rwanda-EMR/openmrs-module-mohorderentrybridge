package org.openmrs.module.mohorderentrybridge;

import org.openmrs.OrderFrequency;

public class MoHOrderFrequency {
	private OrderFrequency orderFrequency;

	private String name;

	public MoHOrderFrequency(OrderFrequency of) {
		setOrderFrequency(of);
		setName(of.getName());
	}

	public OrderFrequency getOrderFrequency() {
		return orderFrequency;
	}

	public void setOrderFrequency(OrderFrequency orderFrequency) {
		this.orderFrequency = orderFrequency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
