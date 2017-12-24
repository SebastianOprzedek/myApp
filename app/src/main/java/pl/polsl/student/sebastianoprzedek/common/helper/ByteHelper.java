package pl.polsl.student.sebastianoprzedek.common.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
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

    public static byte[] bitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static byte[][] splitToBatches(byte[] bytes, int batchSize) throws Exception{
        int numberOfBatches = (int) Math.ceil(bytes.length / (double) batchSize);
        if(numberOfBatches < 1) throw new Exception("number of batches smaller than 1");
        byte[][] batchedBytes = new byte[numberOfBatches][];
        for(int i = 0; i < numberOfBatches-1; i++){
            batchedBytes[i] = new byte[batchSize];
            System.arraycopy(bytes, i * batchSize, batchedBytes[i], 0, batchSize);
        }
        int lastBatchSize = bytes.length - (numberOfBatches-1) * batchSize;
        batchedBytes[numberOfBatches-1] = new byte[lastBatchSize];
        System.arraycopy(bytes, (numberOfBatches - 1) * batchSize, batchedBytes[numberOfBatches - 1], 0, lastBatchSize);
        return batchedBytes;
    }

    public static byte[] mergeBatches(byte[][] batchedBytes){
        int totalSize = 0;
        for (byte[] batchedByte1 : batchedBytes) totalSize += batchedByte1.length;
        List<Byte> bytes = new ArrayList<>();
        for (byte[] batchedByte : batchedBytes)
            for (byte aBatchedByte : batchedByte) bytes.add(aBatchedByte);
        byte[] ret = new byte[bytes.size()];
        int i = 0;
        for (Byte e : bytes)
            ret[i++] = e.byteValue();
        return ret;
    }
}
