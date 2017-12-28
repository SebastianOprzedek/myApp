package pl.polsl.student.sebastianoprzedek.myapp;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pl.polsl.student.sebastianoprzedek.common.helper.FileHelper;
import pl.polsl.student.sebastianoprzedek.common.helper.exceptions.FileTooBigException;
import pl.polsl.student.sebastianoprzedek.myapp.net.ServerConnection;
import pl.polsl.student.sebastianoprzedek.myapp.service.FrameService;
import pl.polsl.student.sebastianoprzedek.myapp.service.MJPEGFrameService;
import pl.polsl.student.sebastianoprzedek.myapp.service.MP4FrameService;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_DIR_PATH = "/storage/emulated/0/eye/Pupil Mobile/local_recording";
    public static final String DEFAULT_INTERVAL = "2000";
    public static final String DEFAULT_HOST = "192.168.1.68";
    public static final String DEFAULT_PORT = "4444";
    FrameService frameService;
    ServerConnection framesServerConnection;
    ServerConnection filesServerConnection;
    EditText hostEditText;
    EditText portEditText;
    TextView logger;
    Boolean running = false;
    Thread captureThread;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableStrictPolicy();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start);
        TextView directoryTextView = (TextView) findViewById(R.id.directory);
        logger = (TextView) findViewById(R.id.logger);
        hostEditText = (EditText) findViewById(R.id.host);
        portEditText = (EditText) findViewById(R.id.port);
        hostEditText.setText(DEFAULT_HOST, TextView.BufferType.EDITABLE);
        portEditText.setText(DEFAULT_PORT, TextView.BufferType.EDITABLE);
        directoryTextView.setText(FileHelper.findOldestDir(MAIN_DIR_PATH));
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startOrStopService();
            }
        });
    }

    private void startOrStopService() {
        try {
            if(!running) {
                running = true;
                if(createFrameService()) framesServerConnection = initServerConnection();
                startButton.setText("STOP");
            }
            else{
                captureThread.interrupt();
                running = false;
                startButton.setText("START");
                if (framesServerConnection != null) framesServerConnection.close();
                if (frameService != null) frameService.closeService();
                sendFiles();
            }
        }
        catch (Exception e){
            handleException(e);
        }
    }

    private void sendFiles() throws Exception {
        filesServerConnection = initServerConnection();
        List<File> files = getFiles();
        if(files.size() < 1) throw new Exception("no valid found in oldest folder in: "+ MAIN_DIR_PATH);
        sendFile(files.get(0));
        if(files.size() > 1) sendFile(files.get(1));
        if (filesServerConnection != null) filesServerConnection.close();
    }

    private void sendFile(File file) throws Exception{
        log("Starting sending file:" + file.getName());
        try {
            if (framesServerConnection != null) {
                filesServerConnection.writeFile(file);
                log("File was sent successfully");
            }
            else log("Server connection is closed");
        }
        catch (FileTooBigException e){
            handleException(e);
        }
    }

    private ServerConnection initServerConnection() {
        ServerConnection serverConnection = null;
        String host = hostEditText.getText().toString();
        int port = Integer.parseInt(portEditText.getText().toString());
        try {
            serverConnection = new ServerConnection(host, port, frameService.getFileName());
        }catch (Exception e){
            handleException(e);
        }
        return serverConnection;
    }

    private void disableStrictPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            if(framesServerConnection!=null) framesServerConnection.close();
            if(filesServerConnection!=null) filesServerConnection.close();
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private boolean createFrameService() {
        boolean result = false;
        try {
            List<File> files = getFiles();
            if(files.size() < 1) throw new Exception("no valid found in oldest folder in: "+ MAIN_DIR_PATH);
            if(files.size() == 1) frameService = dispatchService(files.get(0));
            if(files.size() > 1) createFrameServices(files.get(0), files.get(1));
            result = true;
            startRepeatingTask();
        }
        catch(Exception e){
            handleException(e);
        }
        return result;
    }

    private void createFrameServices(File file, File file2) throws Exception{
        if(FileHelper.getExtension(file).equals("MJPEG")) {
            frameService = dispatchService(file);
            log("Frame services created successfully");
        }
        else{
            frameService = dispatchService(file2);
            log("Frame services created successfully");
        }
    }

    private FrameService dispatchService(File file) throws Exception{
        if(FileHelper.getExtension(file).equals("MJPEG")) {
            log("Frame service created successfully");
            return new MJPEGFrameService(file);
        }
        else if(FileHelper.getExtension(file).equals("MP4")) {
            log("Frame service created successfully");
            return new MP4FrameService(file);
        }
        return null;
    }

    private void sendFrameFromService() throws Exception{
        if(framesServerConnection != null) {
            if (frameService != null) {
                framesServerConnection.writeFrame(frameService.getFrameBytes());
                log("frame was sent from frame service 1");
            }
        }
    }

    private List<File> getFiles() throws Exception {
        String dirPath = FileHelper.findOldestDir(MAIN_DIR_PATH);
        if(dirPath == null) throw new Exception("directory not found in: "+ MAIN_DIR_PATH);
        ArrayList<File> files = FileHelper.findFilesWithExtension(dirPath, "MJPEG");
        files.addAll(FileHelper.filterNotContainsSubstring("audio", FileHelper.findFilesWithExtension(dirPath, "MP4")));
        return FileHelper.filterNotEmpty(files);
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        log(e.getMessage());
    }

    private void log(String message) {
        String s = logger.getText().toString();
        logger.setText(message + "\r\n" + s.substring(0, Math.min(s.length(), 1024)));
    }

    private void toast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    class CaptureThread implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    sendFrameFromService();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    void startRepeatingTask() {
        captureThread = new Thread(new CaptureThread());
        captureThread.start();
        log("Starting new thread for frames");
    }
}