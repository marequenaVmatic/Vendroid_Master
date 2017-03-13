package com.vendomatica.vendroid.connectivity.protocols.statemachine;


public abstract class StateBaseStream<AI> extends StateBase<AI> {
    //protected final static AuditDataRoutine logger = AuditDataRoutine.getAudiDataLogger();


    public StateBaseStream(AI automation, IEventSync eventSync){
        super(automation, eventSync);
    }
/*
    public void write(byte[] b) throws IOException{
        ReferencesStorage.getInstance().btPort.write(b);
    }

    public int read(byte[] buf, int offset, int count)throws  IOException{
        return ReferencesStorage.getInstance().btPort.read(buf, offset, count);
    }

    public int available()throws IOException{
        return ReferencesStorage.getInstance().btPort.available();
    }
*/
}
