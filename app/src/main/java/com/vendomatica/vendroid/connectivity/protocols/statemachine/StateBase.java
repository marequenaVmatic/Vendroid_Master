package com.vendomatica.vendroid.connectivity.protocols.statemachine;


public abstract class StateBase<AI> {
    protected final AI automation;
    protected final IEventSync eventSync;
    protected int timeOut;

    public StateBase(AI automation, IEventSync eventSync){
        this.automation = automation;
        this.eventSync = eventSync;
    }

    protected void castEvent(Event event){
        eventSync.castEvent(event);
    }

    protected void setTimeOut(int timeOut){
        this.timeOut = timeOut;
    }

}


