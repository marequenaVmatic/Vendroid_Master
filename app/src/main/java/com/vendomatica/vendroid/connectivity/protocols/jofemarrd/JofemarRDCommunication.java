package com.vendomatica.vendroid.connectivity.protocols.jofemarrd;

import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.protocols.Communication;

import java.io.IOException;

public class JofemarRDCommunication extends Communication{
    @Override
    public void onData() {
    }

    @Override
    public void write(byte[] data) {
        try {
            ReferencesStorage.getInstance().btPort.write(data);
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
