package com.vendomatica.vendroid.connectivity.protocols.dex;


import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBaseStream;

import java.io.IOException;

public class SlaveState<AI extends IProtocolsDataManagement> extends StateBaseStream<AI> implements IProtocolsDataManagement  {

    private enum AState {SDLE0, SDLE0_ACK, SDLE1, SDLE1_ACK, MDLE_ENQ, MDLE_ENQ_ACK, MDLE, MDLE_ACK }

    private AState currentState = AState.SDLE0;

    public final static Event ENQ_STATE= new Event("ENQ_STATE",0);
    public final static Event STATE = new Event("STATE", 4000);
    public final static Event COLLECT = new Event("COLLECT", 2000);
    public final static Event STOP= new Event("STOP",0);

    byte[] rx = new byte[100];

    //("001234567890RR01L01"+ProtocolConstants.DLE + ProtocolConstants.ETX + "╜╘").getBytes());/*0xbdd4*/
    private final static byte[] SLAVE_MDLE_REQUEST = new byte[] {0x30,0x30,0x31,0x32,0x33,0x34,0x35,
	0x36,0x37,0x38,0x39,0x30,0x52,0x52,0x30,0x31,
	0x4c,0x30,0x31,0x10,0x03,(byte)0xd4,(byte)0xbd};
	
    private final static byte[] SLAVE_SDLE0 = new byte[]{ProtocolsConstants.DLE, 0x30};
    private final static byte[] SLAVE_SDLE1 = new byte[]{ProtocolsConstants.DLE,'1'};
    private final static byte[] DLE_SOH     = new byte[]{ProtocolsConstants.DLE, ProtocolsConstants.SOH};
    private final static byte[] EOT         = new byte[]{ProtocolsConstants.EOT};
    private final static byte[] ENQ         = new byte[]{ProtocolsConstants.ENQ};

    DexCommunication comm;

    public SlaveState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        gotoInit(ENQ_STATE);
        return true;
    }

    @Override
    public void stopAudit() {
        gotoInit(STOP);
    }

    private void gotoInit(Event event){
        currentState = AState.SDLE0;
        castEvent(event);
    }

    @Override
    public void update(int delta) throws IOException {
        comm = (DexCommunication) ReferencesStorage.getInstance().comm;
        if (timeOut < 0){
            gotoInit(ENQ_STATE);
            return;
        }

        int rx_size;

        switch (currentState){
            case SDLE0:
                //logger.log("Slave Handshake\n");
                //logger.log("Slave-SDLE0\n");
                
                comm.write(SLAVE_SDLE0);

                currentState = AState.SDLE0_ACK;
                castEvent(STATE);
                break;
            case SDLE0_ACK:
                //logger.log("Slave-SDLE0_ACK\n");

                if (comm.available() < 23)
                    break;

                rx_size = comm.read(rx, 23);

                if(rx_size == 23){
                    if (rx[0] == ProtocolsConstants.DLE && rx[1] == ProtocolsConstants.SOH){
                        currentState = AState.SDLE1;
                        castEvent(STATE);
                    }
                }
                break;
            case SDLE1:
                //logger.log("Slave-SDLE1\n");

                comm.write(SLAVE_SDLE1);

                currentState = AState.SDLE1_ACK;
                castEvent(STATE);
                break;
            case SDLE1_ACK:
                //logger.log("Slave-SDLE1_ACK\n");

                if (comm.available() < 1)
                    break;

                comm.read(rx, 1);

                if(rx[0] == ProtocolsConstants.EOT){
                    //logger.log("Slave Handshake Done!\n");
                    currentState = AState.MDLE_ENQ;
                    castEvent(STATE);
                }
                break;
            case MDLE_ENQ:
				//logger.log("Slave-MDLE_ENQ\n");
                comm.write(ENQ);

                currentState = AState.MDLE_ENQ_ACK;
                castEvent(STATE);
                break;
            case MDLE_ENQ_ACK:

                if (comm.available() < 2){
                    break;
                }

                comm.read(rx, 2);

                if (rx[0] == ProtocolsConstants.DLE) {
                    currentState = AState.MDLE;
                    castEvent(STATE);
                }
                break;
            case MDLE:
                //logger.log("Master Handshake\n");

                comm.write(DLE_SOH);
                comm.write(SLAVE_MDLE_REQUEST);

                currentState = AState.MDLE_ACK;
                castEvent(STATE);
                break;
            case MDLE_ACK:
                //logger.log("Slave-MDLE_ACK\n");

                if (comm.available() < 2)
                    break;

                comm.read(rx, 2);

                if (rx[0] == ProtocolsConstants.DLE ){
                    comm.write(EOT);

                    //logger.log("Master Handshake Done!\n");
                    currentState = AState.SDLE0;
//TODO                    logger.clearAduitData();
                    logger.log("Start collecting data...");
                    castEvent(COLLECT);
                }
                break;
            default:
                gotoInit(ENQ_STATE);
        }


        timeOut = timeOut - delta;
    }

    private String buf2string(byte[] buf, int size){
        String compose = "";
        for (int i = 0; i<size; i++){
            compose += ",x"+ Integer.toHexString(buf[i]);
        }

        return compose+"\n";
    }
}
