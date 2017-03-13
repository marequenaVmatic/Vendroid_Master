package com.vendomatica.vendroid.connectivity.protocols.statemachine;


public class Event {
    private final String name;
    private final Integer deltaTime;

    public Event(String name, Integer delta){
        this.name = name;
        deltaTime = delta;
    }

    public String getName(){
        return name;
    }

    public Integer getDeltaTime(){
        return deltaTime;
    }
}
