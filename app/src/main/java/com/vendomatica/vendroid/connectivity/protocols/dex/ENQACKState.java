package com.vendomatica.vendroid.connectivity.protocols.dex;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBaseStream;

import java.io.IOException;

public class ENQACKState<AI extends IProtocolsDataManagement>
        extends StateBaseStream<AI> implements IProtocolsDataManagement {

    private static int attemps = 0;
    private static final int MAX_ATTEMPS = 10;

    public final static Event MASTER_HANDSHAKE = new Event("MASTER_HANDSHAKE", 2000);
    public final static Event SLAVE_HANDSHAKE = new Event("SLAVE_HANDSHAKE", 0);
    public final static Event ENQ_STATE = new Event("ENQ_STATE", 0);
    public final static Event STOP = new Event("STOP", 0);



    private DexCommunication comm;

    private byte buff[] = new byte[100];
	
    public ENQACKState(AI automation, IEventSync eventSync) {
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
        if (timeOut<0){
/*
            if (attemps == MAX_ATTEMPS){
                attemps = 0;
                castEvent();
            }
*/
            castEvent(ENQ_STATE);
            return;
        }

        final int bytes = comm.available();

        if (bytes != 0){
            comm.read(buff, bytes);

            attemps = 0;
            
            if (bytes == 1 && buff[0] == ProtocolsConstants.ENQ) {
                castEvent(SLAVE_HANDSHAKE);
                //logger.log("Received<-ENQ\n");
                logger.log("Slave Handshake\n");
            }

            if (bytes == 2 && buff[0] == ProtocolsConstants.DLE && buff[1] == '0') {
                castEvent(MASTER_HANDSHAKE);
                //logger.log("Received<-DLE\n");
                logger.log("Master Handshake\n");
            }
        }

        timeOut = timeOut-delta;
    }

}
