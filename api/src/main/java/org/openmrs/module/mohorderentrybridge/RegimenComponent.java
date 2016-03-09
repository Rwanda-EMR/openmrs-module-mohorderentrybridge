package org.openmrs.module.mohorderentrybridge;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;

public class RegimenComponent {
    
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static final Log log = LogFactory.getLog(RegimenComponent.class);

    private DrugOrder drugOrder;
    private Drug drug;
    private Concept generic;
    private Date startDate;
    private Date stopDate;
    private Concept stopReason;
    
    public RegimenComponent() { }
    
    public RegimenComponent(DrugOrder drugOrder) {
        if (drugOrder.isVoided())
            log.warn("Likely programming error: Creating RegimenComponent for a voided DrugOrder");
        this.drugOrder = drugOrder;
        this.drug = drugOrder.getDrug();
        this.generic = drugOrder.getConcept();
        this.startDate = drugOrder.getEffectiveStartDate();
        if (drugOrder.isDiscontinued(new Date())) {
            this.stopDate = drugOrder.getEffectiveStopDate();
            this.stopReason = drugOrder.getOrderReason();
        } else {
            this.stopDate = drugOrder.getAutoExpireDate();
        }
    }
    
    public RegimenComponent(Concept drug, Date startDate, Date stopDate) {
        this.generic = drug;
        this.startDate = startDate;
        this.stopDate = stopDate;
    }
    
    public String toString() {
        StringBuilder ret = new StringBuilder();
        try
        {
        if (drug != null)
        {
            ret.append(drug.getName());
            ret.append(" from " + df.format(startDate));
        }
        else
        {
            ret.append(generic.getName());
            ret.append(" from " + df.format(startDate));
        }
        if (stopDate != null)
            ret.append(" to " + df.format(stopDate));
        if (stopReason != null)
            ret.append(" because " + stopReason.getName());
        }
        catch (Exception e) {
			// TODO: handle exception
		}
        return ret.toString();
    }

    public Concept getGeneric() {
        return generic;
    }
    public void setGeneric(Concept generic) {
        this.generic = generic;
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
    public Concept getStopReason() {
        return stopReason;
    }
    public void setStopReason(Concept stopReason) {
        this.stopReason = stopReason;
    }
    public DrugOrder getDrugOrder() {
        return drugOrder;
    }
    public void setDrugOrder(DrugOrder drugOrder) {
        this.drugOrder = drugOrder;
    }
    public Drug getDrug() {
        return drug;
    }
    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    @Override
    public int hashCode() {
        if (drugOrder != null)
            return drugOrder.hashCode();
        else
            return generic.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RegimenComponent))
            return false;
        
        RegimenComponent other = (RegimenComponent) o;
        return drugOrder.equals(other.drugOrder); 
    }
    

}