package pl.polsl.student.sebastianoprzedek.myapp.net;

import android.graphics.Bitmap;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;

/**
 * Created by Sebastian Oprzędek on 14.12.2017.
 */

public class ServerConnection {

    Socket socket;
    protected BufferedInputStream in;
    protected DataOutputStream out;

    public ServerConnection(String host, int port, String name) throws Exception {
        establish(host, port);
        setName(name);
        //writeFile(new File("/storage/emulated/0/eye/aa.avi"));
    }

    public void establish(String host, int port) throws Exception {
        socket = new Socket(host, port);
        in = new BufferedInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws Exception{
        writeMessage(Dictionary.STOP);
        in.close();
        out.close();
        socket.close();
    }

    public void setName(String name) throws Exception{
        byte[] utf8Bytes = name.getBytes("UTF-8");
        writeMessage(Dictionary.NAME);
        writeInt(utf8Bytes.length);
        out.write(utf8Bytes);
        waitForConfirmation();
    }

    public void writeFrame(Bitmap bitmap) throws Exception{
        byte[][] batchedBytes = ByteHelper.splitToBatches(ByteHelper.bitmapToByteArray(bitmap), 10000);
        writeMessage(Dictionary.JPEG_HEADER);
        writeInt(batchedBytes.length);
        for (byte[] batchedByte : batchedBytes) writeByteArray(batchedByte);
    }

    public void writeFile(File file) throws Exception{
        byte[][] batchedBytes = ByteHelper.splitToBatches(ByteHelper.fileToByteArray(file), 10000);
        writeMessage(Dictionary.FILE_HEADER);
        writeInt(batchedBytes.length);
        for (byte[] batchedByte : batchedBytes) writeByteArray(batchedByte);
    }

    private void writeByteArray(byte[] bytes) throws Exception{
        writeInt(bytes.length);
        out.write(bytes);
        waitForConfirmation();
    }

    private void writeInt(int number) throws Exception{
        out.write(ByteHelper.intToByteArray(number), 0, 4);
        waitForConfirmation();
    }

    public void waitForConfirmation() throws Exception{
        byte[] bytes = readMessage();
        if(!ByteHelper.equal(bytes, Dictionary.CONFIRM)) {
            socket.close();
            throw new Exception("Wrong answer from server. Disconnecting.");
        }
    }

    private void writeMessage(byte[] message) throws Exception {
        out.write(message, 0, Dictionary.MESSAGE_LENGTH);
        waitForConfirmation();
    }

    private byte[] readMessage() throws IOException {
        byte[] bytes = new byte[4];
        in.read(bytes, 0, 4);
        return bytes;
    }
}
