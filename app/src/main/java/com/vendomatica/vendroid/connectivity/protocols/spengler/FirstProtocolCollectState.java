package com.vendomatica.vendroid.connectivity.protocols.spengler;

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
import java.util.ArrayList;
import java.util.Arrays;

public class FirstProtocolCollectState <AI extends IProtocolsDataManagement>
        extends StateBase<AI> implements IProtocolsDataManagement{

    enum states {
        INITIAL,
        GET_ADDR,
        GET_ADDR_REPLY1,
        GET_ADDR_REPLY2,
        GET_FILE_HANDLE,
        GET_FILE_HANDLE_REPLY1,
        GET_FILE_HANDLE_REPLY2,
        GET_FILE_OPEN,
        GET_FILE_OPEN_REPLY1,
        GET_FILE_OPEN_REPLY2,
        GET_FILE_READ,
        GET_FILE_READ_REPLY1,
        GET_FILE_READ_REPLY2,
        GET_FILE_CLOSE,
        GET_FILE_CLOSE_REPLY1,
        GET_FILE_CLOSE_REPLY2,
        TIMEOUT,
        ERROR
    }
    private states currentState = states.INITIAL;

    private SpenglerCommunication comm;
    private byte[] ref;
    private byte[] size;
    private byte[] handle;
    private byte[] data;
    Message messageSequence;
    private boolean firstRead;

    ArrayList<String> fList = SpenglerInternalStorage.getInstance().fileList;
    private Integer fIndex = 0;


    public FirstProtocolCollectState(AI automation, IEventSync eventSync) {
        super(automation, eventSync);
    }


    public static final Event UPDATE_STATE = new Event("UPDATE_STATE", 4000);
    public static final Event DONE = new Event("DONE", 0);

    @Override
    public boolean startAudit() {
        return true;
    }

    @Override
    public void stopAudit() {
    }

    @Override
    public void update(int deltaTime) throws IOException {

        if (timeOut <0){
            final String s = currentState.toString();
            currentState = states.TIMEOUT;
        }

        switch (currentState) {
            case INITIAL:
                comm =(SpenglerCommunication) ReferencesStorage.getInstance().comm;
                fList = SpenglerInternalStorage.getInstance().fileList;
                if (fList != null && fIndex < fList.size()){
                    Log.d("Vending", "File open:"+fList.get(fIndex));

                    currentState = states.GET_ADDR;
                    castEvent(UPDATE_STATE);
                    firstRead = true;
                    data = null;
                }else{
                    sendDoneMessage();
                    castEvent(DONE);
                }
                break;
            case GET_ADDR: {
                Message msg = new Message(new byte[]{0x0, 0x0, 0x0, 0x0},
                        new byte[]{0x6, 0x0},
                        new byte[]{0x3, (byte) 0xFF, 0x0, 0x0, 0x0, 0x0});

                comm.write(msg.toByteArray());
                castEvent(UPDATE_STATE);

                currentState = states.GET_ADDR_REPLY1;
                messageSequence = msg;
                break;
            }
            case GET_ADDR_REPLY1: {
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.GET_ADDR_REPLY2;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_ADDR_REPLY2: {
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                //Formally we must parse incoming data
                ref = Arrays.copyOfRange(msg, 4, 8);

                currentState = states.GET_FILE_HANDLE;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_HANDLE: {
                messageSequence.incSequence();
                messageSequence.setDestinationAddr(ref);

                final String fName = fList.get(fIndex);
                byte[] b = new byte[5 + fName.length()];
                Arrays.fill(b, (byte)0x00);
                b[1] = (byte)0x4E;

                for (int i = 0; i < fName.length(); i++){
                    b[i+4] = fName.getBytes()[i];
                }

                messageSequence.setData(b);

                comm.write(messageSequence.toByteArray());

                currentState = states.GET_FILE_HANDLE_REPLY1;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_HANDLE_REPLY1: {
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.GET_FILE_HANDLE_REPLY2;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_HANDLE_REPLY2:{
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                //Get size of the file
                size = Arrays.copyOfRange(msg, 45, 49);

                currentState = states.GET_FILE_OPEN;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_OPEN: {
                messageSequence.incSequence();

                final String fName = fList.get(fIndex);
                byte[] b = new byte[3 + fName.length()];
                Arrays.fill(b, (byte) 0x00);
                b[1] = (byte) 0x3D;

                for (int i = 0; i < fName.length(); i++) {
                    b[i+2] = fName.getBytes()[i];
                }

                messageSequence.setData(b);

                comm.write(messageSequence.toByteArray());

                currentState = states.GET_FILE_OPEN_REPLY1;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_OPEN_REPLY1: {
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.GET_FILE_OPEN_REPLY2;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_OPEN_REPLY2: {
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                //read file handle
                handle = Arrays.copyOfRange(msg, 18, 20);

                //Check that this is file available
                if( handle[0] == (byte)0xff && handle[1] == 0x00){
                    currentState = states.GET_FILE_CLOSE;
                    castEvent(UPDATE_STATE);
                    break;
                }

                currentState = states.GET_FILE_READ;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_READ: {
                byte[] b = new byte[6];
                Arrays.fill(b, (byte) 0x00);
                b[1] = (byte) 0x3F;
                //cambio fName
                final String fName = fList.get(fIndex);
                for(int i=0; i<fName.length(); i++){
                    b[i+2] = handle[i];
                }

//                for(int i=0; i<2; i++){
//                    b[i+4] = size[i];
//                }
                b[4] = (byte)0xff; // always read 255 bytes

                messageSequence.incSequence();
                messageSequence.setData(b);

                comm.write(messageSequence.toByteArray());
                currentState = states.GET_FILE_READ_REPLY1;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_READ_REPLY1:{
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.GET_FILE_READ_REPLY2;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_READ_REPLY2:{
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                if (msg.length > 21) {
                    data = ArraysHelper.appendData(data, Arrays.copyOfRange(msg, 20, msg.length - 1));

                    Log.d("Vending" , "size:" + data.length);
                    sendDataSize(data.length);

                    firstRead = false;
                    //try to read more data
                    currentState = states.GET_FILE_READ;
                    castEvent(UPDATE_STATE);
                    break;
                }

                if(firstRead){
                    fIndex--; //Error on previous read so we must close the file and try to it read again.
                }else{
                    sendDataMessage(data);
                }
                currentState = states.GET_FILE_CLOSE;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_CLOSE:{

                byte[] b = new byte[4];
                Arrays.fill(b, (byte) 0x00);
                b[1] = (byte) 0x3E;

                final String fName = fList.get(fIndex);
                for(int i=0; i<fName.length(); i++){
                    b[i+2] = handle[i];
                }

                messageSequence.incSequence();
                messageSequence.setData(b);
                comm.write(messageSequence.toByteArray());

                currentState = states.GET_FILE_CLOSE_REPLY1;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_CLOSE_REPLY1:{
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.GET_FILE_CLOSE_REPLY2;
                castEvent(UPDATE_STATE);
                break;
            }
            case GET_FILE_CLOSE_REPLY2:{
                byte[] msg = comm.getMessage();
                if (msg == null) break;

                currentState = states.INITIAL;
                fIndex += 1;
                castEvent(UPDATE_STATE);

                break;
            }
            case TIMEOUT:{
                sendTimeOutMessage("");
                castEvent(DONE);
                break;
            }
            case ERROR:{
                sendErrorMessage("Error Occured!");
                castEvent(DONE);
                break;
            }

        }

        timeOut -= deltaTime;
    }

    void sendDataSize(Integer size){
        final Bundle b = new Bundle();
        b.putInt("Message", size);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA_READ, b);
    }

    void sendErrorMessage(String msg){
        final Bundle b = new Bundle();
        b.putString("Message", msg);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_ERROR, b);
    }

    void sendDataMessage(byte[] data){
        final Bundle b = new Bundle();
        b.putString("FileName", fList.get(fIndex));
        b.putByteArray("Data", data);

        sendMessage(ProtocolsConstants.MSG_ACTION_AUDIT_DATA, b);
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

    void sendMessage(int what, Bundle b){
        b.putString("Protocol", "Spengler");

        final android.os.Handler h = SpenglerInternalStorage.getInstance().handler;
        android.os.Message m = h.obtainMessage();
        m.what = what;
        m.setData(b);
        h.sendMessage(m);
    }


}
