package pl.polsl.student.sebastianoprzedek.myapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import pl.polsl.student.sebastianoprzedek.common.files.ReverseInputStream;
import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;

/**
 * Created by sebas on 13.12.2017.
 */

public class MJPEGFrameService implements FrameService {
    public static final byte[] JPEG_HEADER = {(byte) -1 ,(byte) -40 ,(byte) -1 ,(byte) -32};

    BufferedInputStream normalBufferedReader;
    BufferedInputStream lastFrameBufferedReader;
    ReverseInputStream lastFrameFileReader;
    File file;

    public MJPEGFrameService(File file) throws Exception {
        lastFrameFileReader = new ReverseInputStream(file);
        FileInputStream fileReader = new FileInputStream(file);
        normalBufferedReader = new BufferedInputStream(fileReader);
        lastFrameBufferedReader = new BufferedInputStream(lastFrameFileReader);
        testService(file.getName());
        this.file = file;
    }

    private void testService(String fileName) throws Exception{
        byte[] byteArray = new byte[4];
        normalBufferedReader.read(byteArray, 0, 4);
        if(!ByteHelper.equal(byteArray, JPEG_HEADER)) {
            closeService();
            throw new Exception("Error during creation frame service from file: " + fileName);
        }
    }

    public void closeService() throws IOException {
        lastFrameBufferedReader.close();
        lastFrameFileReader.close();
        normalBufferedReader.close();
    }

    private byte[] getBytesFormLastFrameReader() throws IOException{
        byte[] bytes = new byte[4];
        for(int i=0; i<4; i++)
            bytes[i] = (byte) lastFrameBufferedReader.read();
        return bytes;
    }

    public byte[] getLastFrameBytes() throws Exception{
        lastFrameFileReader.moveToEnd();
        Boolean error = false;
        ArrayList<Byte> bytes = new ArrayList<>();
        while(true){
            byte[] byteArray = getBytesFormLastFrameReader();
            bytes.addAll(ByteHelper.byteArrayToList(byteArray));
            if(byteArray[3] == (byte) -1 && byteArray[2] == (byte) -1 && byteArray[1] == (byte) -1 && byteArray[0] == (byte) -1){
                error = true;
                break;
            }
            if(byteArray[3] == (byte) -1 && byteArray[2] == (byte) -40 && byteArray[1] == (byte) -1 && byteArray[0] == (byte) -32)
                break;
        }
        if(error) return null;
        byte[] finalArray = new byte[bytes.size()];
        for(int i=0; i<bytes.size(); i++)
            finalArray[i] = bytes.get(i);
        finalArray = ByteHelper.reverse(finalArray);
        return finalArray;
    }

    public byte[] getNextFrameBytes() throws Exception{
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.addAll(ByteHelper.byteArrayToList(JPEG_HEADER));

        while(true){
            byte[] byteArray = new byte[4];
            normalBufferedReader.read(byteArray, 0, 4);
            if(ByteHelper.equal(byteArray, JPEG_HEADER)){
                break;
            }
            else{
                bytes.addAll(ByteHelper.byteArrayToList(byteArray));
            }
        }
        byte[] finalArray = new byte[bytes.size()];
        for(int i=0; i<bytes.size(); i++){
            finalArray[i] = bytes.get(i);
        }
        return finalArray;
    }

    public Bitmap getNextFrame() throws Exception {
        byte[] bytes = getNextFrameBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public Bitmap getLastFrame() throws Exception {
        byte[] bytes = getLastFrameBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String getFileName(){
        return file.getName();
    }
}
