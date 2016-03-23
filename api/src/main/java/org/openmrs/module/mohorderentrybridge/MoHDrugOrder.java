package org.openmrs.module.mohorderentrybridge;

import org.openmrs.DrugOrder;

/**
 * This class extends {@link DrugOrder} and is meant to provide DrugOrder method returns which are needed at client level as variables instead
 * to support Tomcat6 container which can't call the methods themselves
 */
public class MoHDrugOrder {

	private boolean isActive;

	private DrugOrder drugOrder;

	private String doseUnitsName;

	private String quantityUnitsName;

	private String routeName;

	public String getDoseUnitsName() {
		return doseUnitsName;
	}

	public void setDoseUnitsName(String doseUnitsName) {
		this.doseUnitsName = doseUnitsName;
	}

	public String getQuantityUnitsName() {
		return quantityUnitsName;
	}

	public void setQuantityUnitsName(String quantityUnitsName) {
		this.quantityUnitsName = quantityUnitsName;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public MoHDrugOrder(DrugOrder dos) {
		setDrugOrder(dos);
		setActive(dos
				.isActive()/*
							 * || (dos.isStarted() && !dos.isExpired() &&
							 * !dos.isVoided() &&
							 * dos.getEffectiveStartDate().before(new Date()))
							 */);
		setDoseUnitsName(dos.getDoseUnits() != null ? dos.getDoseUnits().getName().getName() : null);
		setQuantityUnitsName(dos.getQuantityUnits() != null ? dos.getQuantityUnits().getName().getName() : null);
		setRouteName(dos.getRoute() != null ? dos.getRoute().getName().getName() : null);
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public DrugOrder getDrugOrder() {
		return drugOrder;
	}

	public void setDrugOrder(DrugOrder drugOrder) {
		this.drugOrder = drugOrder;
	}
}
