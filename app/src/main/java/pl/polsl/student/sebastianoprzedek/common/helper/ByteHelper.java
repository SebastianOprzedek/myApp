package pl.polsl.student.sebastianoprzedek.common.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import pl.polsl.student.sebastianoprzedek.common.helper.exceptions.FileTooBigException;

/**
 * Created by sebas on 13.12.2017.
 */

public class ByteHelper {
    public static final int MAX_FILE_SIZE = 99999999;
    public static List<Byte> byteArrayToList(byte[] bytes){
        List<Byte> byteList = new ArrayList<>();
        for(int i=0; i<bytes.length; i++){
            byteList.add(bytes[i]);
        }
        return byteList;
    }

    public static byte[] byteListToArray(List<Byte> byteList){
        byte[] bytes = new byte[byteList.size()];
        for(int i=0; i<bytes.length; i++)
            bytes[i] = byteList.get(i);
        return bytes;
    }

    public static Boolean equal(byte[] bytes1, byte[] bytes2){
        if(bytes1.length != bytes2.length) return false;
        for(int i=0; i< bytes1.length; i++)
            if(bytes1[i] != bytes2[i]) return false;
        return true;
    }

    public static byte[] reverse(byte[] bytes){
        byte[] reversedBytes = new byte[bytes.length];
        for(int i=1; i<=bytes.length; i++)
            reversedBytes[i-1] = bytes[bytes.length-i];
        return reversedBytes;
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

    public static byte[] mergeBatches(int batchSize, int numberOfBatches, byte[][] batchedBytes){
        int totalSize = 0;
        for (byte[] batchedByte1 : batchedBytes) totalSize += batchedByte1.length;
        byte[] bytes = new byte[totalSize];
        for(int i=0; i<numberOfBatches-1; i++)
            System.arraycopy(batchedBytes[i], 0, bytes, i * batchSize, batchSize);
        System.arraycopy(batchedBytes[numberOfBatches - 1], 0, bytes, (numberOfBatches - 1) * batchSize, batchedBytes[numberOfBatches - 1].length);
        return bytes;
    }

    public static byte[] fileToByteArray(File file) throws Exception {
//        if (file.length() > MAX_FILE_SIZE) {
//            throw new FileTooBigException(file.getName());
//        }
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        }finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    public static byte[] cutArrayToLength(byte[] buffer, int length) {
        if (buffer.length == length) return buffer;
        else {
            byte[] bytes = new byte[length];
            System.arraycopy(buffer, 0, bytes, 0, length);
            if (buffer.length > length)
                return bytes;
            else{
                for(int i = buffer.length; i < length; i++)
                    bytes[i] = (byte) 0;
                return bytes;
            }
        }
    }

}
