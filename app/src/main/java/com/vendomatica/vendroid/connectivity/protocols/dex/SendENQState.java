package com.vendomatica.vendroid.connectivity.protocols.dex;


import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBaseStream;

import java.io.IOException;


public class SendENQState<AI extends IProtocolsDataManagement>
        extends StateBaseStream<AI> implements IProtocolsDataManagement {

    public static final Event CHECK_ENQ = new Event("CHECK_ENQ", 10000);
    public static final Event STOP = new Event("STOP", 0);
    public static final Event ENQ_STATE = new Event("ENQ_STATE", 0);

    private final static byte[] ENQ = new byte[]{ProtocolsConstants.ENQ};

    private DexCommunication comm;


    public SendENQState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        castEvent(ENQ_STATE);
        
        return true;
    }

    @Override
    public void stopAudit() {
        castEvent(STOP);
    }

    @Override
    public void update(int delta) throws IOException {
        
        comm = (DexCommunication) ReferencesStorage.getInstance().comm;
        comm.write(ENQ);

        logger.log("Send->ENQ\n");
        castEvent(CHECK_ENQ);
    }

}
