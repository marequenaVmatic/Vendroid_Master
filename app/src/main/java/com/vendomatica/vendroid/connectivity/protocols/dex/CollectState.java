package com.vendomatica.vendroid.connectivity.protocols.dex;



import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.helpers.ArraysHelper;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBaseStream;

import java.io.IOException;
import java.util.Arrays;

public class CollectState<AI extends IProtocolsDataManagement>
        extends StateBaseStream<AI> implements IProtocolsDataManagement {

    public final static Event ENQ_STATE = new Event("ENQ_STATE", 4000);
    public final static Event STATE = new Event("STATE", 4000);
    public final static Event DONE = new Event("DONE", 0);

    //private final static int buf_size = 4096;
    //private byte[] rx = new byte[buf_size];
    //private int rx_size = 0;
    private byte[] tx = new byte[]{ProtocolsConstants.DLE, '0'};

    private boolean bToggle = false;
    private int blocksReceived = 0;

    DexCommunication comm;

    String data = "";


    private Buffer mBuffer = new Buffer(new IonCommandDexProtocol() {
        @Override
        public void onEOTCommand() {
            logger.log("Done!\n");
            logger.sendDataMessage(data);
            logger.sendDoneMessage();
            castEvent(DONE);
        }

        @Override
        public void onENQCommand() throws IOException {
            writeToggle();
            castEvent(STATE);
        }

        @Override
        public void onDataCommand(String command) throws IOException {
            //final String log = command;
            final int blockSize = command.length();

            blocksReceived++;

            logger.log("Received block=" + blocksReceived + " of " + blockSize + " bytes\n");

            data = data + command;

            logger.sendDataSize(data.length());

            writeToggle();

            mBuffer.clear();

            castEvent(STATE);

        }
    });

    public CollectState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        gotoInit(STATE);
        return true;
    }

    @Override
    public void stopAudit() {
        gotoInit(DONE);
    }

    private void gotoInit(Event event){
        bToggle = true;
        blocksReceived = 0;
        tx[1] = '0';
        castEvent(event);
    }

    @Override
    public void update(int deltaTime) throws IOException {
        if (timeOut < 0){
            data = "";
            blocksReceived = 0;
            gotoInit(ENQ_STATE);
            return;
        }

        comm = (DexCommunication) ReferencesStorage.getInstance().comm;

        final int av = comm.available();
        if (av == 0){
            timeOut = timeOut - deltaTime;
            return;
        }

        byte b[] = new byte[av];
        comm.read(b, av);


        mBuffer.add(b, av);

        timeOut = timeOut - deltaTime;
    }

    private void writeToggle() throws IOException {
        tx[1] = (byte)Toggle();
        comm.write(tx);
    }

    private char Toggle(){
        bToggle = !bToggle;
        return (bToggle) ? '1' : '0';
    }


    interface IonCommandDexProtocol{
        void onEOTCommand();
        void onENQCommand() throws IOException;
        void onDataCommand(String command) throws IOException;
    }


    private class Buffer{
        private byte[] data_buffer;
        private Integer index = 0;
        private Integer size;
        private IonCommandDexProtocol dex;


        Buffer(IonCommandDexProtocol dex){
            this.dex = dex;
        }


        void clear (){
			data_buffer = null;
        }

        void add(byte[] data, Integer size) throws IOException {

            //logger.log("Income: \n"+Audit2String.String(data, 0, size)+"\n\n");
            if (size == 1 && data[0] == ProtocolsConstants.EOT){
                clear();
                data_buffer = null;
                dex.onEOTCommand();
                return;
            }

            if (size == 1 && data[0] == ProtocolsConstants.ENQ){
                clear();
                data_buffer = null;
                dex.onENQCommand();
                return;
            }
/*
            Integer i = 0;
            while((size>0) && (index < this.size)){
                data_buffer[index++] = data[i];
                size--;
                i++;
            }
*/
			data_buffer = ArraysHelper.appendData(data_buffer, data);
			final int idx = data_buffer.length;
			
            if ( idx < 6)
                return;

            if (data_buffer[0] == ProtocolsConstants.DLE && data_buffer[1] == ProtocolsConstants.STX){
                if (data_buffer[idx - 4] == ProtocolsConstants.DLE &&
                        data_buffer[idx - 3] == ProtocolsConstants.ETB || data_buffer[idx - 3] == ProtocolsConstants.ETX){
                    dex.onDataCommand(Audit2String.String(Arrays.copyOfRange(data_buffer, 2, idx - 4)));
                    data_buffer = null;
                }
            }
        }

        String getString(){
            return Audit2String.String(data_buffer);
        }
    }


    private static class Audit2String{
        protected static String String(byte[] buf){
            String str = "";
			final int size = buf.length;

            for (int i = 0; i < size; i++){

                if (buf[i] == ProtocolsConstants.STX){
                    str = str + "STX ";
                }else if (buf[i] == ProtocolsConstants.SOH){
                    str = str + "SOH ";
                }else if (buf[i] == ProtocolsConstants.ETX){
                    str = str + "ETX ";
                }else if (buf[i] == ProtocolsConstants.ETB){
                    str = str + "ETB ";
                }else if (buf[i] == ProtocolsConstants.DLE){
                    str = str + "DLE ";
                }else if (buf[i] == ProtocolsConstants.EOT){
                    str = str + "EOT ";
                }else if (buf[i] == ProtocolsConstants.ENQ){
                    str = str + "ENQ ";
                }else if (buf[i] == ProtocolsConstants.NAK){
                    str = str + "NAK ";
                }else {
                    str = str + (char)buf[i];
                }
            }

            return str;
        }
    }
}
