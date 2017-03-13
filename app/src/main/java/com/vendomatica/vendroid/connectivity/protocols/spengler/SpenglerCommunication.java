package com.vendomatica.vendroid.connectivity.protocols.spengler;



import com.vendomatica.vendroid.connectivity.ReferencesStorage;
import com.vendomatica.vendroid.connectivity.bluetooth.BTPortCommunication;
import com.vendomatica.vendroid.connectivity.protocols.Communication;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

public class SpenglerCommunication extends Communication{

    public SpenglerCommunication(){
        super();
        //backgroundThread.start();
    }

    @Override
    public void onData() {
        parser();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        setStop(true);
    }

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


    private static final byte ESC_CHAR = 0x2F;
    public  static final byte START_CHAR = 0x3A;
    public  static final byte STOP_CHAR = 0x0D;
    private static final byte START_CHAR2 = 0x3B;
    private static final byte START_CHAR3 = 0x23;

    @Override
    public void write(byte[] data){
        final BTPortCommunication btPort = ReferencesStorage.getInstance().btPort;
        try {
            btPort.write(START_CHAR);
            for (byte d : data) {
                switch (d) {
                    case ESC_CHAR:
                        btPort.write(new byte[]{ESC_CHAR, 0x30});
                        break;
                    case START_CHAR:
                        btPort.write(new byte[]{ESC_CHAR, 0x31});
                        break;
                    case STOP_CHAR:
                        btPort.write(new byte[]{ESC_CHAR, 0x32});
                        break;
                    case START_CHAR2:
                        btPort.write(new byte[]{ESC_CHAR, 0x33});
                        break;
                    case START_CHAR3:
                        btPort.write(new byte[]{ESC_CHAR, 0x34});
                        break;
                    default:
                        btPort.write(d);
                        break;
                }
            }
            btPort.write(STOP_CHAR);
        }catch (IOException e){
            e.printStackTrace();
            setStop(true);
        }
    }

    private void parser(){

        if (buf.size() < 14){
            return;
        }
//TODO Think!
        for (int i = 0; i < buf.size(); i++){
            if (buf.get(i) == START_CHAR){
                for (int j = i; j < buf.size(); j++){
                    if (buf.get(j) == STOP_CHAR){//do we have all packet

                        //remove head
                        for (int k = 0; k <= i; k++){
                            buf.popFirst();
                            j--;
                        }

                        //copy data
                        byte b[] = new byte[j];
                        for (int k = 0; k < j; k++){
                            b[k] = buf.popFirst();
                        }

                        int size = removeEscapeCharacters(b);
                        setMessage(Arrays.copyOf(b, size));

                        parser();//check do we have one more packet
                    }
                }
            }
        }

    }

    private int removeEscapeCharacters(byte[] buf) {
        int c = 0;
        for (int i=0; i<buf.length; i++, c++){
            if (buf[i] != ESC_CHAR){
                buf[c] = buf[i];
                continue;
            }

            switch (buf[i + 1]){
                case 0x30:
                    buf[c] = ESC_CHAR;
                    break;
                case 0x31:
                    buf[c] = START_CHAR;
                    break;
                case 0x32:
                    buf[c] = STOP_CHAR;
                    break;
                case 0x33:
                    buf[c] = START_CHAR2;
                    break;
                case 0x34:
                    buf[c] = START_CHAR3;
                    break;
                default:
                    //TODO !ERROR
                    return -1;
            }
            i++;
        }

        return c;
    }

}
