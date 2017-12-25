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
    FrameService frameService2;
    ServerConnection serverConnection;
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
                createFrameService();
                initServerConnections(hostEditText.getText().toString(), Integer.parseInt(portEditText.getText().toString()));
                startButton.setText("STOP");
            }
            else{
                running = false;
                startButton.setText("START");
                if (serverConnection != null) serverConnection.close();
                if (frameService != null) frameService.closeService();
                if (frameService2 != null) frameService2.closeService();
            }
        }
        catch (Exception e){
            handleException(e);
        }
    }

    private void initServerConnections(String host, int port) {
        try {
            if (frameService != null)
                serverConnection = new ServerConnection(host, port, frameService.getFileName());
        }catch (Exception e){
            handleException(e);
        }
    }

    private void disableStrictPolicy() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            serverConnection.close();
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private void createFrameService() {
        try {
            List<File> files = getFiles();
            if(files.size() < 1) throw new Exception("no valid found in oldest folder in: "+ MAIN_DIR_PATH);
            frameService = dispatchService(files.get(0));
            if(files.size() > 1) frameService2 = dispatchService(files.get(1));
        }
        catch(Exception e){
            handleException(e);
        }
        startRepeatingTask();
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
        if(serverConnection != null) {
            if (frameService != null) {
                serverConnection.writeFrame(frameService.getFrameBytes());
                log("frame was sent from frame service 1");
            }
            if (frameService2 != null){
                serverConnection.writeFrame(frameService2.getFrameBytes());
                log("frame was sent from frame service 2");
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