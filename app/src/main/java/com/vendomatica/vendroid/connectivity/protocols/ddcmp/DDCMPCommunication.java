package com.vendomatica.vendroid.connectivity.protocols.ddcmp;

import android.util.Log;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.bluetooth.BTPortCommunication;
import com.vendomatica.vendroid.connectivity.protocols.Communication;

import java.io.IOException;
import java.util.Vector;

public class DDCMPCommunication extends Communication{

    private final Vector<byte[]> msgQueue = new Vector<>(0);

    public byte[] getMessage(){
        if (!msgQueue.isEmpty()) {
            final byte[] m = msgQueue.get(0);
            msgQueue.remove(0);
            return m;
        }
        return null;
    }


    private void setMessage(byte[] msg){
        msgQueue.add(msg);
    }

    public DDCMPCommunication(){
        super();
    }

    @Override
    public void onData() {
        byte type = buf.get(0);

        if (type == 0x05) {
            parseControl();
            return;
        }


        if (type == (byte) 0x81) {
            parseData();
            return;
        }

        int i;
        for (i = 0; i < buf.size(); i++) {
            byte b = buf.get(i);
            if (b == 0x05 || b == (byte) 0x81) {
                buf.removeFromStart(i);
                break;
            }
        }
    }

    private void parseControl(){
        if (buf.size() < 8) return;

        byte b[] = new byte[6];

        for (int i = 0; i<6; i++){
            b[i] = buf.get(i);
        }
        byte crc1 = buf.get(6);
        byte crc2 = buf.get(7);


        if (compareCrc(b, crc1, crc2)){
            buf.removeFromStart(8);
            setMessage(b);
        }
    }

    private void parseData(){
        if (buf.size()<8) return;

        int size = (0x00FF & buf.get(1));
        if(buf.size() < size + 10){
            return;//size + header + crc
        }

        byte crc1 = buf.get(size + 8);
        byte crc2 = buf.get(size + 9);

        byte[] b = new byte[size + 8];
        for (int i = 0 ; i < size + 8; i++){
            b[i] = buf.get(i);
        }

        if(compareCrc(b, crc1, crc2)){
            setMessage(b);
        }
        buf.removeFromStart(size + 10);
    }

    private boolean compareCrc(final byte[] b, final byte c1, final byte c2){
        byte crc[] = Crc16.crc16byte(b);

        return c1==crc[0] && c2==crc[1];
    }


    @Override
    public void write(byte[] data) {
        try {
            final BTPortCommunication btPort = ReferencesStorage.getInstance().btPort;
            btPort.write(data);
            Log.d("Write", Integer.toString(data.length));
        }catch (IOException e){
            e.printStackTrace();
            setStop(true);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        setStop(true);
    }

    public int read(byte buf[], int size){
        int i = 0;

        synchronized (this.buf) {
            if (this.buf.size() < size) {
                return -1;
            }

            for (; i < size; i++) {
                buf[i] = this.buf.get(i);
            }
            this.buf.removeFromStart(i);
        }
        return i;
    }

    public int available(){
        return buf.size();
    }
}
