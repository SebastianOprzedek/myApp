package pl.polsl.student.sebastianoprzedek.myapp.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;

/**
 * Created by sebas on 13.12.2017.
 */

public class MJPEGFrameService implements FrameService {
    public static final byte[] JPEG_HEADER = {(byte) -1 ,(byte) -40 ,(byte) -1 ,(byte) -32};

    BufferedInputStream bufferedReader;
    File file;

    public MJPEGFrameService(File file) throws Exception {
        FileInputStream fileReader = new FileInputStream(file);
        bufferedReader = new BufferedInputStream(fileReader);
        testService(file.getName());
        this.file = file;
    }

    private void testService(String fileName) throws Exception{
        byte[] byteArray = new byte[4];
        bufferedReader.read(byteArray, 0, 4);
        if(!ByteHelper.equal(byteArray, JPEG_HEADER)) {
            closeService();
            throw new Exception("Error during creation frame service from file: " + fileName);
        }
    }

    public void closeService() throws IOException {
        bufferedReader.close();
    }

    public byte[] getFrameBytes() throws Exception{
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.addAll(ByteHelper.byteArrayToList(JPEG_HEADER));

        while(true){
            byte[] byteArray = new byte[4];
            bufferedReader.read(byteArray, 0, 4);
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

    public Bitmap getFrame() throws Exception {
        byte[] bytes = getFrameBytes();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public String getFileName(){
        return file.getName();
    }
}
