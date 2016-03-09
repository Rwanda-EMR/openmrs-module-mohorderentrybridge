package org.openmrs.module.mohorderentrybridge;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;

public class Regimen {

    private Date startDate;
    private Date endDate;
    @SuppressWarnings("unused")
    private Concept startReason;
    private Concept endReason;
    private Set<RegimenComponent> components;
    
    public Regimen() {
        this.components = new HashSet<RegimenComponent>();
    }
    
    public Regimen(Date startDate, Date endDate, Concept startReason, Concept endReason, Set<RegimenComponent> components) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startReason = startReason;
        this.endReason = endReason;
        this.components = components;
    }
    
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (Concept g : getUniqueGenerics())
            ret.append(g.getBestName(Context.getLocale())).append(", ");
        ret.append(" from " + startDate);
        if (endDate != null) {
            ret.append(" to " + endDate);
            if (endReason != null)
                ret.append(" because " + endReason);
        }
        return ret.toString();
    }
    
    public Set<Concept> getUniqueGenerics() {
        Set<Concept> ret = new HashSet<Concept>();
        for (RegimenComponent c : components) {
            ret.add(c.getGeneric());
        }
        return ret;
    }

    public void add(RegimenComponent component) {
        components.add(component);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Concept getEndReason() {
        return endReason;
    }

    public void setEndReason(Concept endReason) {
        this.endReason = endReason;
    }

    public Set<RegimenComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<RegimenComponent> components) {
        this.components = components;
    }
    
    public boolean isActive() {
        return isActive(new Date());
    }

    public boolean isActive(Date date) {
        return OpenmrsUtil.compareWithNullAsEarliest(startDate, date) <= 0
                && OpenmrsUtil.compareWithNullAsLatest(date, endDate) < 0;
    }

    public boolean containsDrug(Drug drug) {
        if (components == null)
            return false;
        for (RegimenComponent c : components) {
            if (c.getDrug().equals(drug))
                return true;
        }
        return false;
    }
    
    public boolean containsDrugConcept(Concept drug) {
        if (components == null)
            return false;
        for (RegimenComponent c : components) {
            if (c.getGeneric().equals(drug))
                return true;
        }
        return false;
    }
    
    public boolean containsDrugConceptOnDate(Concept concept, Date date) {
        if (components == null)
            return false;
        for (RegimenComponent c : components) {
            if (c.getGeneric().equals(concept) && (c.getStartDate().equals(date) || c.getStartDate().before(date)) && (c.getStopDate() == null || c.getStopDate().after(date)))
                return true;
        }
        return false;
    }
    
    public Integer getDurationInDays(){
        Integer ret = null;
        if (startDate != null){
            Date endDateTmp = new Date();
            if (endDate != null)
                endDateTmp = endDate;
            
            
            Calendar calFrom = Calendar.getInstance();
            Calendar calTo = Calendar.getInstance();
            
            calFrom.setTime(startDate);
            calTo.setTime(endDateTmp);
            
            int calFromDaysOfYear = calFrom.get(Calendar.DAY_OF_YEAR);
            int calFromYear = calFrom.get(Calendar.YEAR);
            int calToDaysOfYear = calTo.get(Calendar.DAY_OF_YEAR);
            int calToYear = calTo.get(Calendar.YEAR);
            int retTmp = calToDaysOfYear - calFromDaysOfYear;
            int yearDif = calToYear - calFromYear;
            
               
            int yearDifTotal = 0;
                for (int i = 0; i < yearDif; i++){
                    int yearThisIteration = calFrom.get(Calendar.YEAR) + i;
                    Calendar yearTest = Calendar.getInstance();
                    yearTest.set(Calendar.YEAR, yearThisIteration);
                    int daysInStartYear = calFrom.getMaximum(Calendar.DAY_OF_YEAR);
                    yearDifTotal += daysInStartYear;
                }
            retTmp = retTmp + yearDifTotal;
            ret = Integer.valueOf(retTmp);
        }
        return ret;
    }
    
    public Integer getDurationInWeeks(){
        Integer ret = null;
        if (startDate != null){
            Date endDateTmp = new Date();
            if (endDate != null && endDate.before(endDateTmp))
                endDateTmp = endDate;
            long diff = endDateTmp.getTime() - startDate.getTime(); //milliseconds
            int numerator = (1000*60*60*24*7); 
            float diffF = ((float) diff / numerator);
            ret = Math.round(diffF);
        }
        return ret;
    }

}
