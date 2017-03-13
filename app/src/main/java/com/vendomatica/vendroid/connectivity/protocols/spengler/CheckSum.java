package com.vendomatica.vendroid.connectivity.protocols.spengler;

class CheckSum {
    static byte CalculateCheckSum(byte sum, byte[] data){
        for(byte d: data){
            sum = CalculateCheckSum(sum, d);
        }

        return sum;
    }

    static byte CalculateCheckSum(byte sum, byte data){
        sum ^= data;

        if ((sum & 0x1) == 0x1){
            sum = (byte)((sum <<24)>>>25 | 0x80);
        }else{
            sum = (byte)((sum<<24)>>>25);
        }
        return sum;
    }
}
