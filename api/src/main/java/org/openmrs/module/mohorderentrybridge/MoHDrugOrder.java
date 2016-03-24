package org.openmrs.module.mohorderentrybridge;

import java.util.Date;

import org.openmrs.DrugOrder;

/**
 * This class extends {@link DrugOrder} and is meant to provide DrugOrder method
 * returns which are needed at client level as variables instead to support
 * Tomcat6 container which can't call the methods themselves
 */
public class MoHDrugOrder {

	private boolean isActive;

	private DrugOrder drugOrder;

	private String doseUnitsName;

	private String quantityUnitsName;

	private String routeName;
	
	private Date startDate;
	
	private Date stopDate;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStopDate() {
		return stopDate;
	}

	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}

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
		setActive(isActive());
		setDoseUnitsName(dos.getDoseUnits() != null ? dos.getDoseUnits().getName().getName() : null);
		setQuantityUnitsName(dos.getQuantityUnits() != null ? dos.getQuantityUnits().getName().getName() : null);
		setRouteName(dos.getRoute() != null ? dos.getRoute().getName().getName() : null);
		setStartDate(dos.getEffectiveStartDate());
		setStopDate(dos.getEffectiveStopDate());
	}

	public boolean getIsActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/*
	 * overwrites Order#isActive
	 */
	private boolean isActive() {
		if (getDrugOrder().isActive() && getDrugOrder().isStarted() && !getDrugOrder().isExpired()
				&& !getDrugOrder().isVoided()) {
			return true;
		} else
			return false;
	}

	public DrugOrder getDrugOrder() {
		return drugOrder;
	}

	public void setDrugOrder(DrugOrder drugOrder) {
		this.drugOrder = drugOrder;
	}
}
