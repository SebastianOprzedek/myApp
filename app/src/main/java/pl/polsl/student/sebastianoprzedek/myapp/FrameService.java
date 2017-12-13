package pl.polsl.student.sebastianoprzedek.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sebas on 13.12.2017.
 */

public class FrameService {
    public static final byte[] JPG_HEADER = {(byte) -1 ,(byte) -40 ,(byte) -1 ,(byte) -32};

    BufferedInputStream bufferedReader;

    public FrameService(File file) throws Exception {
        FileInputStream fileReader = new FileInputStream(file);
        bufferedReader = new BufferedInputStream(fileReader);
        testService(file.getName());
    }

    private void testService(String fileName) throws Exception{
        byte[] byteArray = new byte[4];
        bufferedReader.read(byteArray, 0, 4);
        if(!ByteHelper.equal(byteArray, JPG_HEADER)) {
            closeService();
            throw new Exception("Error during creation frame service from file: " + fileName);
        }
    }

    public void closeService() throws IOException {
        bufferedReader.close();
    }

    public byte[] getFrameBytes() throws Exception{
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.addAll(ByteHelper.byteArrayToList(JPG_HEADER));

        while(true){
            byte[] byteArray = new byte[4];
            bufferedReader.read(byteArray, 0, 4);
            if(ByteHelper.equal(byteArray, JPG_HEADER)){
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

    public Bitmap getFrame() throws Exception {
        byte[] bytes = getFrameBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
