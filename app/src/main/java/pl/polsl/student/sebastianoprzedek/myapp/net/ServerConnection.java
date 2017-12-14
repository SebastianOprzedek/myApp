package pl.polsl.student.sebastianoprzedek.myapp.net;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;
import pl.polsl.student.sebastianoprzedek.myapp.Dictionary;

/**
 * Created by Sebastian Oprzędek on 14.12.2017.
 */

public class ServerConnection {
    public static final String HOST = "192.168.1.68";
    public static final int PORT = 4444;

    Socket socket;
    protected BufferedInputStream in;
    protected DataOutputStream out;

    public ServerConnection(String name) throws Exception {
        establish();
        setName(name);
        waitForConfirmation();
    }

    public void establish() throws Exception {
        socket = new Socket(HOST, PORT);
        in = new BufferedInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void setName(String name) throws Exception{
        byte[] utf8Bytes = name.getBytes("UTF-8");
        writeMessage(Dictionary.NAME);
        writeInt(utf8Bytes.length);
        out.write(utf8Bytes);
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
