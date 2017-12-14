package pl.polsl.student.sebastianoprzedek.myapp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by sebas on 14.12.2017.
 */

public class Client {

    public void test() {
        try {
            Socket socket = new Socket("192.168.1.68", 4444);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String textLine = in.readLine();
            do {
                System.out.println(textLine);

                textLine = in.readLine();
            } while (textLine != null);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("No I/O");
            e.printStackTrace();
        }
    }
}
