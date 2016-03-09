package org.openmrs.module.mohorderentrybridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;

public class RegimenHistory implements Serializable {

    private static final long serialVersionUID = 1L;
    protected final Log log = LogFactory.getLog(getClass());
    
    private List<RegimenComponent> components;
    private transient ListMap<Date, RegimenComponent[]> events;
    private transient Map<Collection<Drug>, ListMap<Date, RegimenComponent[]>> eventsByRegimenType;
    
    public RegimenHistory() {
        components = new ArrayList<RegimenComponent>();
    }
    
    public RegimenHistory(Collection<DrugOrder> orders) {
        this();
        for (DrugOrder o : orders) {
            if (!o.isVoided())
                add(new RegimenComponent(o));
        }
    }
    
    public void add(RegimenComponent component) {
        components.add(component);
    }
    
    /**
     * Reorganizes this as a List<Regimen>. Requires some calculation, but no database hits.
     * 
     * @return
     * 
     * @should get correct regimen list
     */
    public List<Regimen> getRegimenList() {
        return getRegimenList((Collection<Drug>) null);
    }
        
    /**
     * Reorganizes this as a List<Regimen>. Requires some calculation, but no database hits.
     * 
     * @param relevantDrugs ignore regimen components whose drugs aren't in this collection
     * @return
     * 
     * @should get correct regimen list
     */
    public List<Regimen> getRegimenList(Collection<Drug> relevantDrugs) {
        ListMap<Date, RegimenComponent[]> events = calculateEvents(relevantDrugs);
        
        List<Regimen> ret = new ArrayList<Regimen>();
        Set<RegimenComponent> runningList = new HashSet<RegimenComponent>();
        
        Regimen lastRegimen = null;
        for (Map.Entry<Date, List<RegimenComponent[]>> e : events.entrySet()) {
            Concept reason = null;
            for (RegimenComponent[] ev : e.getValue()) {
                if (ev[0] != null) {
                    if (!runningList.remove(ev[0])) {
                    	
                    	log.info("Running list: " + runningList.size());
                    	log.info(ev[0] + "");
                        throw new RuntimeException("Didn't remove anything");
                    }
                    if (ev[0].getStopReason() != null)
                        reason = ev[0].getStopReason();
                }
                if (ev[1] != null) {
                    runningList.add(ev[1]);
                }
            }
            if (lastRegimen != null) {
                lastRegimen.setEndDate(e.getKey());
                lastRegimen.setEndReason(reason);
            }
            Regimen thisRegimen = new Regimen(e.getKey(), null, reason, null, new HashSet<RegimenComponent>(runningList));
            ret.add(thisRegimen);
            lastRegimen = thisRegimen;
        }
        if (ret.size() > 0 && ret.get(ret.size() - 1).getComponents().isEmpty()) {
            ret.remove(ret.size() - 1);
        }
        return ret;
    }
    
    public Regimen getRegimen(Date date) {
        List<Regimen> regs = getRegimenList();
        for (Regimen reg : regs) {
            if (reg.isActive(date))
                return reg;
        }
        return null;
    }

    public List<Regimen> getRegimensAfter(Date effectiveDate) {
        List<Regimen> regs = getRegimenList();
        int startFrom = Integer.MAX_VALUE;
        if (regs.size() > 0 && effectiveDate.before(regs.get(0).getStartDate()))
            return regs;
        for (int i = 0; i < regs.size(); i++) {
            if (regs.get(i).isActive(effectiveDate)) {
                startFrom = i + 1;
                break;
            }
        }
        if (startFrom < regs.size())
            return regs.subList(startFrom, regs.size());
        else
            return new ArrayList<Regimen>();
    }

    /**
     * Builds up events, as a map from date to all events that happen on that date.
     * An event is represented by a RegimenComponent[] where:
     *     the 0th element is a RegimenComponent that was stopped
     *     the 1st element is a RegimenComponent that was started
     */
    @SuppressWarnings("unused")
    private synchronized void calculateEvents() {
        if (events == null) {
            events = new ListMap<Date, RegimenComponent[]>();
            for (RegimenComponent c : components) {
                if (c.getStartDate() == null)
                    throw new IllegalArgumentException("Found null startDate in " + c);
                events.add(c.getStartDate(), new RegimenComponent[] { null, c });
                if (c.getStopDate() != null)
                    events.add(c.getStopDate(), new RegimenComponent[] { c, null });
            }
        }
    }
    
    /**
     * Builds up events whose drug is in the given collection, as a map from date to
     * all events that happen on that date.
     * An event is represented by a RegimenComponent[] where:
     *     the 0th element is a RegimenComponent that was stopped
     *     the 1st element is a RegimenComponent that was started
     */
    private synchronized ListMap<Date, RegimenComponent[]> calculateEvents(Collection<Drug> relevantDrugs) {
        if (eventsByRegimenType == null) {
            eventsByRegimenType = new HashMap<Collection<Drug>, ListMap<Date, RegimenComponent[]>>();
        }
        ListMap<Date, RegimenComponent[]> ret = eventsByRegimenType.get(relevantDrugs);
        if (ret != null)
            return ret;
        ret = new ListMap<Date, RegimenComponent[]>();
        eventsByRegimenType.put(relevantDrugs, ret);
        for (RegimenComponent c : getComponents(relevantDrugs)) {
            if (c.getStartDate() == null)
                throw new IllegalArgumentException("Found null startDate in " + c);
            ret.add(c.getStartDate(), new RegimenComponent[] { null, c });
            if (c.getStopDate() != null)
                ret.add(c.getStopDate(), new RegimenComponent[] { c, null });
        }
        return ret;
    }

    public List<RegimenComponent> getComponents(Collection<Drug> relevantDrugs) {
        List<RegimenComponent> ret = new ArrayList<RegimenComponent>();
        for (RegimenComponent c : components)
            if (relevantDrugs == null || relevantDrugs.contains(c.getDrug()))
                ret.add(c);
        return ret;
    }

    
    /**
     * Returns a list of events equivalent to the list passed in, but with all events
     * where the same drug is stopped and started on the same day removed.
     * (These would generally be dose-change-only events.)
     */
    public static void removeDoseChangeEvents(ListMap<Date, RegimenComponent[]> events) {
         @SuppressWarnings("unused")
        ListMap<Date, RegimenComponent[]> ret = new ListMap<Date, RegimenComponent[]>();
         for (Map.Entry<Date, List<RegimenComponent[]>> e : events.entrySet()) {
             throw new RuntimeException("Not Yet Implemented!");
         }
    }
    
}
