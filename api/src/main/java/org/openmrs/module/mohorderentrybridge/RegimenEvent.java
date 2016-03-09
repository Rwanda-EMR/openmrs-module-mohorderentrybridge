package org.openmrs.module.mohorderentrybridge;

import java.util.Date;

import org.openmrs.Concept;

public class RegimenEvent {

    private Date date;
    private Concept drugStarted;
    private Concept drugStopped;
    
    public static RegimenEvent startEvent(Date date, Concept drug) {
         return new RegimenEvent(date, drug, null);
    }
    
    public static RegimenEvent stopEvent(Date date, Concept drug) {
        return new RegimenEvent(date, null, drug);
   }
    
    public RegimenEvent() { }
    
    public RegimenEvent(Date date, Concept drugStarted, Concept drugStopped) {
        this.date = date;
        this.drugStarted = drugStarted;
        this.drugStopped = drugStopped;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Concept getDrugStarted() {
        return drugStarted;
    }

    public void setDrugStarted(Concept drugStarted) {
        this.drugStarted = drugStarted;
    }

    public Concept getDrugStopped() {
        return drugStopped;
    }

    public void setDrugStopped(Concept drugStopped) {
        this.drugStopped = drugStopped;
    }
    
}
