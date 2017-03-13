package com.vendomatica.vendroid.connectivity.protocols.statemachine;


import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public abstract class AutomationBase<AI> implements IEventSync {

    protected AI state;
    private final Map<AI, Map<Event, AI>> edges = new HashMap<AI, Map<Event, AI>>();

    protected void addEdge(AI source, Event event, AI target){
        Map<Event, AI> row = edges.get(source);
        if (null == row){
            row = new IdentityHashMap<Event, AI>();
            edges.put(source, row);
        }
        row.put(event, target);
    }

    public void castEvent(Event event){
        try {
            state = edges.get(state).get(event);
            if (state instanceof StateBase){
                ((StateBase) state).setTimeOut(event.getDeltaTime());
            }
        } catch (NullPointerException e) {
            throw new IllegalStateException("Edge is not defined");
        }
    }

}
