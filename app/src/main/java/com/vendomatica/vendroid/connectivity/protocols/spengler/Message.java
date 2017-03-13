package com.vendomatica.vendroid.connectivity.protocols.spengler;

import com.vendomatica.vendroid.connectivity.helpers.ArraysHelper;

import java.util.Arrays;

public class Message {

    Header head;
    Body   body;

    public Message(byte[] dst, byte[] bodyLength, byte[] bodyData){
        head = new Header();

        if (dst.length <= 4) {
            for (int i = 0; i < dst.length; i++) {
                head.dst[i] = dst[i];
            }
        }

        final int l = bodyLength[0]+bodyLength[1]*256;
        head.len = bodyLength;

        body = new Body();
        body.data = Arrays.copyOf(bodyData, l);

    }

    private class Header{
        byte[] dst = {0x00, 0x00, 0x00, 0x00};
        byte[] src = {(byte)0xFE, (byte)0xFF, (byte)0xFF, (byte)0xFF};
        byte tsk = 1;
        byte typ = 1;
        byte seq = 1;
        byte[] len = {0x00, 0x00};
        byte chk = 0x00;

        byte CheckSum(){
            byte sum = 0;
            sum = CheckSum.CalculateCheckSum(sum, dst);
            sum = CheckSum.CalculateCheckSum(sum, src);
            sum = CheckSum.CalculateCheckSum(sum, tsk);
            sum = CheckSum.CalculateCheckSum(sum, typ);
            sum = CheckSum.CalculateCheckSum(sum, seq);
            sum = CheckSum.CalculateCheckSum(sum, len);
            chk = sum;

            return chk;
        }

        int getBodyLength(){
            return len[0]+len[1]*256;
        }
    }

    private class Body{
        byte[] data;
        byte chk;

        public Body(Integer size){
            data = new byte[size];
        }

        public Body(){
        }

        int getLength(){
            return data.length;
        }

        byte CheckSum(){
            chk = CheckSum.CalculateCheckSum((byte) 0, data);
            return chk;
        }
    }


    public byte[] toByteArray(){

        byte[] array = null;
        array = ArraysHelper.appendData(array, head.dst);
        array = ArraysHelper.appendData(array, head.src);
        array = ArraysHelper.appendData(array, head.tsk);
        array = ArraysHelper.appendData(array, head.typ);
        array = ArraysHelper.appendData(array, head.seq);
        array = ArraysHelper.appendData(array, head.len);
        array = ArraysHelper.appendData(array, head.CheckSum());
        array = ArraysHelper.appendData(array, body.data);
        array = ArraysHelper.appendData(array, body.CheckSum());

        return array;
    }

    public void setDestinationAddr(byte[] d){
        head.dst = d;
    }

    public void setData(byte[] d){
        body.data = d;
        head.len[0] = (byte)(d.length % 256);
        head.len[1] = (byte)(d.length / 256);
    }

    public byte[] getData(){
        return body.data;
    }

    public void incSequence(){
        head.seq += 1;
    }

}

