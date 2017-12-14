package pl.polsl.student.sebastianoprzedek.common.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sebas on 13.12.2017.
 */

public class ByteHelper {
    public static List<Byte> byteArrayToList(byte[] bytes){
        List<Byte> byteList = new ArrayList<>();
        for(int i=0; i<bytes.length; i++){
            byteList.add(bytes[i]);
        }
        return byteList;
    }

    public static Boolean equal(byte[] bytes1, byte[] bytes2){
        if(bytes1.length != bytes2.length) return false;
        for(int i=0; i< bytes1.length; i++)
            if(bytes1[i] != bytes2[i]) return false;
        return true;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
