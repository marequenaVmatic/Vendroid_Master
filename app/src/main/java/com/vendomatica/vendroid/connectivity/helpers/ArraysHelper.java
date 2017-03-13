package com.vendomatica.vendroid.connectivity.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ArraysHelper {
    public static byte[] appendData(byte firstObject,byte[] secondObject){
        byte[] byteArray= {firstObject};
        return appendData(byteArray,secondObject);
    }

    public static byte[] appendData(byte[] firstObject,byte secondByte){
        byte[] byteArray= {secondByte};
        return appendData(firstObject,byteArray);
    }

    public static byte[] appendData(byte[] firstObject,byte[] secondObject){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            if (firstObject!=null && firstObject.length!=0)
                outputStream.write(firstObject);
            if (secondObject!=null && secondObject.length!=0)
                outputStream.write(secondObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
