package org.openmrs.module.mohorderentrybridge;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
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

	private String frequency;

	private String orderReason;

	private String concept;

	public MoHDrugOrder(DrugOrder dos) {
		setDrugOrder(dos);
		setActive(isActive());
		setDoseUnitsName(getConceptName(dos.getDoseUnits()));
		setQuantityUnitsName(dos.getQuantityUnits() != null ? dos.getQuantityUnits().getName().getName() : null);
		setRouteName(getConceptName(dos.getRoute()));
		setStartDate(dos.getEffectiveStartDate());
		setStopDate(dos.getEffectiveStopDate());
		setFrequency(dos.getFrequency() != null ? dos.getFrequency().getName() : null);
		setConcept(getConceptName(dos.getConcept()));
		setOrderReason(StringUtils.isBlank(dos.getOrderReasonNonCoded()) ? getConceptName(dos.getOrderReason())
				: dos.getOrderReasonNonCoded());
	}

	private String getConceptName(Concept c) {
		if (c != null && c.getName() != null) {
			return c.getName().getName();
		} else {
			return null;
		}
	}

	public String getOrderReason() {
		return orderReason;
	}

	public void setOrderReason(String orderReason) {
		this.orderReason = orderReason;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

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

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
}
