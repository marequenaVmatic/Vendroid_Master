package com.vendomatica.vendroid.connectivity.protocols.ddcmp;

import android.os.Bundle;
import android.util.Log;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.helpers.ArraysHelper;
import com.vendomatica.vendroid.connectivity.protocols.IProtocolsDataManagement;
import com.vendomatica.vendroid.connectivity.protocols.ProtocolsConstants;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.Event;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.IEventSync;
import com.vendomatica.vendroid.connectivity.protocols.statemachine.StateBase;

import java.io.IOException;
import java.util.Arrays;

public class DDCMPCollectState <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement{

    public static final Event UPDATE_STATE = new Event("UPDATE_STATE", 4000);
    public static final Event DONE_STATE = new Event("DONE_STATE", 0);

    enum states {
        INITIAL,
        CONTROL,
        CONTROL_REPLY,
        WHOAREYOU,
        WHOAREYOU_REPLY,
        WHOAREYOU_REPLY2,
        READDATA_START,
        READDATA_START_REPLY,
        READDATA_START_REPLY2,
        READDATA_CONTINUE,
        READDATA_FINISH,
        DONE,
        TIMEOUT
    }
    private states currentState = states.INITIAL;

    enum reading{
        ERROR,
        NORMAL
    }

    private reading readState = reading.ERROR;

    private byte counter = 0;
    private int dataLength = 0;

    private static byte msgstart[] = {0x05, 0x06, 0x40, 0x00, 0x00, 0x01};
    private static byte msgstack[] = {0x05, 0x07, 0x40, 0x00, 0x00, 0x01, 0x00, 0x00};
    private static byte msgack[] =   {0x05, 0x01, 0x40, 0x00, 0x00, 0x01};
    private static byte msgnack[] =  {0x05, 0x02, 0x40, 0x00, 0x00, 0x01};

    private static byte whoareyou[] = {(byte)0x81,(byte)0x10,(byte)0x40,(byte)0x00,(byte)0x01,
            (byte)0x01,(byte)0x0A,(byte)0x42,(byte)0x77,(byte)0xE0,(byte)0x00,(byte)0x00,
            (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x17,(byte)0x02,(byte)0x16,(byte)0x11,
            (byte)0x13,(byte)0x32,(byte)0x00,(byte)0x00,(byte)0x0C};

    private static byte read_start[] = {(byte)0x81,(byte)0x09,(byte)0x40,(byte)0x01,(byte)0x02,
            (byte)0x01,(byte)0x46,(byte)0xB0,(byte)0x77,(byte)0xE2,(byte)0x00,(byte)0x01,
            (byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

    private byte data[];


    public DDCMPCollectState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }

    @Override
    public boolean startAudit() {
        return false;
    }

    @Override
    public void stopAudit() {

    }

    @Override
    public void update(int deltaTime) throws IOException {
        DDCMPCommunication comm = (DDCMPCommunication) ReferencesStorage.getInstance().comm;
        if (timeOut <0){
            final String s = currentState.toString();
            currentState = states.TIMEOUT;
        }

        switch (currentState) {
            case INITIAL:
                counter = 1;
                dataLength = 0;
                readState = reading.ERROR;
                currentState = states.CONTROL;
                data = null;

                castEvent(UPDATE_STATE);
                break;
            case CONTROL:
                comm.write(ArraysHelper.appendData(msgstart, Crc16.crc16byte(msgstart)));

                log("Start");

                castEvent(UPDATE_STATE);
                currentState = states.CONTROL_REPLY;
                break;
            case CONTROL_REPLY: {
                byte buf[] = comm.getMessage();
                if (buf == null)
                    break;

                if (buf[0] == 0x5) {
                    castEvent(UPDATE_STATE);
                    currentState = states.WHOAREYOU;
                }
            }
                break;
            case WHOAREYOU:
                comm.write(ArraysHelper.appendData(whoareyou, Crc16.crc16byte(whoareyou)));
                currentState = states.WHOAREYOU_REPLY;
                castEvent(UPDATE_STATE);
                break;
            case WHOAREYOU_REPLY: {
                byte buf[] = comm.getMessage();
                if (buf == null)
                    break;

                if (buf[0] == 0x5) {
                    castEvent(UPDATE_STATE);
                    currentState = states.WHOAREYOU_REPLY2;
                }
                break;
            }
            case WHOAREYOU_REPLY2:{
                byte buf[] = comm.getMessage();
                if (buf == null)
                    break;

                if (buf[0] == (byte)0x81) {
                    castEvent(UPDATE_STATE);
                    Log.d("WHOAREYOU_REPLY2", "");
                    currentState = states.READDATA_START;
                }
                break;
            }
            case READDATA_START: {
                sendACK();
                comm.write(ArraysHelper.appendData(read_start, Crc16.crc16byte(read_start)));
                castEvent(UPDATE_STATE);
                currentState = states.READDATA_START_REPLY;
                break;
            }

            case READDATA_START_REPLY:{
                byte buf[] = comm.getMessage();
                if (buf == null)
                    break;

                if (buf[0] == 0x5) {
                    castEvent(UPDATE_STATE);
                    currentState = states.READDATA_START_REPLY2;
                }
                break;
            }
            case READDATA_START_REPLY2:{
                byte buf[] = comm.getMessage();
                if (buf == null)
                    break;

                if (buf[0] == (byte)0x81) {
                    castEvent(UPDATE_STATE);
                    Log.d("READDATA_START_REPLY2", "");
                    dataLength = 256 * (0x00FF&buf[buf.length - 1]) + (0x00FF&buf[buf.length - 2]);
                    if (dataLength==0) {
                        log("Error occured, try to read it again!");
                        currentState = states.INITIAL;
                    }else{
                        currentState = states.READDATA_CONTINUE;
                        sendACK();
                    }
                }
                break;
            }

            case READDATA_CONTINUE:{
                byte buf[] = comm.getMessage();
                if (buf == null){
                    break;
                }

                dataLength -= buf.length - 10;
                byte d[] = Arrays.copyOfRange(buf, 10, buf.length);
                Log.d("BLOCK#" + Integer.toString(buf[9]), new String(d));
                sendACK();

                if (readState == reading.ERROR && buf[9]==0){
                    readState = reading.NORMAL;
                }


                if (readState == reading.NORMAL) {
                    log("Received block#" + Integer.toString(buf[9]));
                    data = ArraysHelper.appendData(data, d);

                    sendDataSize(data.length);

                    if (dataLength <= 0) {
                        currentState = states.READDATA_FINISH;
                    }
                }

                castEvent(UPDATE_STATE);
                break;
            }

            case READDATA_FINISH:{
                byte b[] = {(byte)0x81,(byte)0x02,(byte)0x40,(byte)0x19,(byte)0x03,
                        (byte)0x01,(byte)0x62,(byte)0xE6,(byte)0x77,(byte)0xFF};

                b[3] = (byte)(counter - 1);

                comm.write(ArraysHelper.appendData(b, Crc16.crc16byte(b)));

                if ((readState == reading.ERROR)){
                    currentState = states.INITIAL;
                    break;
                }

                sendDataMessage(data);
                sendDoneMessage();

                currentState = states.DONE;
                break;
            }
            case DONE:{
                castEvent(UPDATE_STATE);
                break;
            }

            case TIMEOUT:
                castEvent(UPDATE_STATE);
                currentState = states.INITIAL;
                break;
            default:
                break;
        }

        timeOut = timeOut-deltaTime;
    }

    private void sendACK(){
        msgack[3] = counter;
        ReferencesStorage.getInstance().comm.write(ArraysHelper.appendData(msgack, Crc16.crc16byte(msgack)));
        counter++;
    }

    void sendErrorMessage(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_ERROR, b);
    }

    void sendDataMessage(byte[] data){
        final Bundle b = new Bundle();
        b.putByteArray("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
    }

    void sendDataSize(Integer size){
        final Bundle b = new Bundle();
        b.putInt("Message", size);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ, b);
    }

    void sendTimeOutMessage(String s){
        final Bundle b = new Bundle();
        b.putString("Message", s);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_TIMEOUT, b);
    }

    void sendDoneMessage(){
        final Bundle b = new Bundle();
        b.putString("Message", "Done.");

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DONE, b);
    }

    void log(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);
        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_LOG, b);
    }

    void sendMessage(int what, Bundle b){
        b.putString("Protocol", "DDCMP");

        final android.os.Handler h = DDCMPInternalStorage.getInstance().handler;
        android.os.Message m = h.obtainMessage();
        m.what = what;
        m.setData(b);
        h.sendMessage(m);
    }

}
