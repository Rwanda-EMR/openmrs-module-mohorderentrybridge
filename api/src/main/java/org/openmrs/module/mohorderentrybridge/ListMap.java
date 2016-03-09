package org.openmrs.module.mohorderentrybridge;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class ListMap<K, V> extends TreeMap<K, List<V>> {

    public ListMap() {
        super();
    }
    
    public void add(K key, V value) {
        List<V> list = get(key);
        if (list == null) {
            list = new ArrayList<V>();
            put(key, list);
        }
        list.add(value);
    }
    
}
