package com.vendomatica.vendroid.connectivity.protocols.dex;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.helpers.ArraysHelper;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBaseStream;

import java.io.IOException;

public class MasterState<AI extends IProtocolsDataManagement>
        extends StateBaseStream<AI> implements IProtocolsDataManagement  {

    public final static Event ENQ_STATE = new Event("ENQ_STATE", 0);
    public final static Event STATE = new Event("STATE", 4000);
    public final static Event COLLECT = new Event("COLLECT", 3000);
    public final static Event STOP = new Event("STOP", 0);

    byte[] rx;
    int rx_size = 0;
    byte rx_index = 0;

    private DexCommunication comm;

    private enum AState {MDLE, MDLE_ACK, MEOT, SENQ, SDLE0, SDLE0_ACK, SDLE1, SDLE1_ACK}
    private AState state = AState.MDLE;

    private final static byte[] DLE_SOH = new byte[]{ProtocolsConstants.DLE, ProtocolsConstants.SOH};
    final byte[] MASTER_REQUEST =  new byte[]{0x31, 0x32, 0x33, 0x34, 0x35, 0x36,
            0x37, 0x38, 0x39, 0x30, 0x52, 0x52, 0x30,
            0x31, 0x4c, 0x30, 0x31, 0x10, 0x03, (byte)0xde, (byte) 0x4d};
    //"1234567890RR01L01"+ProtocolConstants.DLE + ProtocolConstants.ETX +"M▐";  ;/*0x4dde*/

    private final static byte[] DLE_0 = new byte[]{ProtocolsConstants.DLE, '0'};
    private final static byte[] DLE_1 = new byte[]{ProtocolsConstants.DLE, '1'};
    private final static byte[] EOT = new byte[]{ProtocolsConstants.EOT};


    public MasterState(AI automation, IEventSync eventSync) {
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
        state = AState.MDLE;
        castEvent(event);
    }

    @Override
    public void update(int deltaTime) throws IOException {

        comm = (DexCommunication) ReferencesStorage.getInstance().comm;

        if (timeOut < 0){
            gotoInit(ENQ_STATE);
            return;
        }

 //       final String dateFormat = "HH:mm:ss:.SSSZ";
 //       final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        rx_size = 0;

        switch (state){
            case MDLE:
                //logger.log("MASTER-MDLE\n");
                comm.write(DLE_SOH);
                comm.write(MASTER_REQUEST);

                state = AState.MDLE_ACK;
                castEvent(STATE);
                break;
            case MDLE_ACK:
                //logger.log("MASTER-MDLE_ACK\n");
                if (comm.available() < 2)
                    break;

                rx = new byte[2];
                comm.read(rx, 2);

                if(rx[0] != ProtocolsConstants.DLE){
					break;
                }
				
				state = AState.MEOT;
                castEvent(STATE);
                break;
            case MEOT:
                //logger.log("MASTER-EOT\n");

                comm.write(EOT);

                //logger.log("Master Handshake Done!\n");
                state = AState.SENQ;
                castEvent(STATE);
                break;
            case SENQ:
                //logger.log("MASTER-SENQ\n");
               
                if (comm.available() < 1)
                    break;

                rx = new byte[1];
                comm.read(rx, 1);

                if(rx[0] == ProtocolsConstants.ENQ){
                    state = AState.SDLE0;
                    //logger.log("Slave Handshake\n");
                    castEvent(STATE);
                }
                break;
            case SDLE0:
                //logger.log("MASTER-SDLE0\n");

                comm.write(DLE_0);

                rx = null;
                state = AState.SDLE0_ACK;
                castEvent(STATE);

                break;
            case SDLE0_ACK:
                //logger.log("MASTER-SDLE0_ACK\n");

				final int s = comm.available();
                if (s == 0){
                    break;
                }
				
				byte b[] = new byte[s];
                comm.read(b, s);
                rx = ArraysHelper.appendData(rx, b);

                rx_index = (byte)rx.length;

                if (rx_index >=4 && rx[0] == ProtocolsConstants.DLE && rx[1] == ProtocolsConstants.SOH &&
                     rx[rx_index - 4] == ProtocolsConstants.DLE && rx[rx_index - 3] == ProtocolsConstants.ETX){
                     state = AState.SDLE1;
                     castEvent(STATE);
                }
                break;
            case SDLE1:
                //logger.log("MASTER-SDLE1\n");

                comm.write(DLE_1);

                state = AState.SDLE1_ACK;
                castEvent(STATE);
                break;
            case SDLE1_ACK:
                //logger.log("MASTER-SDLE_1 ACK\n");
                if (comm.available() < 1)
                    break;

                rx = new byte[1];
                rx_size = comm.read(rx, 1);

                if(rx[0] == ProtocolsConstants.EOT){
                    //logger.log("Slave Handshake Done!\n");
                    state = AState.MDLE;

                    logger.log("Start collecting data...");
                    castEvent(COLLECT);//We go to the Collect state
                }

                break;
            default:
                gotoInit(ENQ_STATE);
        }

        timeOut = timeOut - deltaTime;
    }

    private String buf2string(byte[] buf, int size){
        String compose = "";
        for (int i = 0; i<size; i++){
            compose += ",x"+ Integer.toHexString(buf[i]);
        }

        return compose+"\n";
    }

}
