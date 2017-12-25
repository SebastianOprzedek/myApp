package pl.polsl.student.sebastianoprzedek.myapp.net;

/**
 * Created by Sebastian Oprzędek on 14.12.2017.
 */

public class Dictionary {
    public static final int MESSAGE_LENGTH = 4;
    public static final byte[] STOP = {(byte) 0 ,(byte) 0 ,(byte) 0 ,(byte) 0};
    public static final byte[] NAME = {(byte) 0 ,(byte) 0 ,(byte) 0 ,(byte) 1};
    public static final byte[] JPEG_HEADER = {(byte) -1 ,(byte) -40 ,(byte) -1 ,(byte) -32};
    public static final byte[] FILE_HEADER = {(byte) 1 ,(byte) 20 ,(byte) 41 ,(byte) 3};
    public static final byte[] CONFIRM = {(byte) 1 ,(byte) 1 ,(byte) 0 ,(byte) 3};
}
