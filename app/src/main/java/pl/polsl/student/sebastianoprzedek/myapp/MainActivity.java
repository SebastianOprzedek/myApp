package pl.polsl.student.sebastianoprzedek.myapp;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import pl.polsl.student.sebastianoprzedek.myapp.service.VideoCaptureService;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_DIR_PATH = "/storage/emulated/0/eye/Pupil Mobile/local_recording";
    public static final String DEFAULT_INTERVAL = "2000";
    public static final String DEFAULT_HOST = "192.168.1.68";
    public static final String DEFAULT_PORT = "4444";
    ImageView imageView;
    ImageView imageView2;
    FrameService frameService;
    FrameService frameService2;
    private int mInterval = Integer.parseInt(DEFAULT_INTERVAL);
    private Handler mHandler;
    ServerConnection serverConnection;
    ServerConnection serverConnection2;
    EditText hostEditText;
    EditText portEditText;
    EditText intervalEditText;
    Boolean running = false;
    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableStrictPolicy();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        startButton = (Button) findViewById(R.id.start);
        Button changeDirButton = (Button) findViewById(R.id.change);
        TextView diretoryTextView = (TextView) findViewById(R.id.directory);
        hostEditText = (EditText) findViewById(R.id.host);
        portEditText = (EditText) findViewById(R.id.port);
        intervalEditText = (EditText) findViewById(R.id.interval);
        hostEditText.setText(DEFAULT_HOST, TextView.BufferType.EDITABLE);
        portEditText.setText(DEFAULT_PORT, TextView.BufferType.EDITABLE);
        intervalEditText.setText(DEFAULT_INTERVAL, TextView.BufferType.EDITABLE);
        diretoryTextView.setText(FileHelper.findOldestDir(MAIN_DIR_PATH));
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startOrStopService();
            }
        });
    }

    private void startOrStopService() {
        try {
            if(!running) {
                createFrameService(Integer.parseInt(portEditText.getText().toString()));
                initServerConnections(hostEditText.getText().toString(), Integer.parseInt(portEditText.getText().toString()));
                startButton.setText("STOP");
            }
            else{
                startButton.setText("START");
                stopRepeatingTask();
                if (serverConnection != null) serverConnection.close();
                if (serverConnection2 != null) serverConnection2.close();
                if (frameService != null) frameService.closeService();
                if (frameService2 != null) frameService2.closeService();
            }
            running = !running;
        }
        catch (Exception e){
            handleException(e);
        }
    }

    private void initServerConnections(String host, int port) {
        try {
            if (frameService != null)
                serverConnection = new ServerConnection(host, port, frameService.getFileName());
            if (frameService2 != null)
                serverConnection2 = new ServerConnection(host, port, frameService2.getFileName());
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
        stopRepeatingTask();
        try{
            serverConnection.close();
            serverConnection2.close();
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private void createFrameService(int interval) {
        try {
            mInterval = interval;
            List<File> files = getFiles();
            if(files.size() < 1) throw new Exception("no valid found in oldest folder in: "+ MAIN_DIR_PATH);
            frameService = dispatchService(files.get(0));
            if(files.size() > 1) frameService2 = new MJPEGFrameService(files.get(1));
            toast("Service created successfully");
        }
        catch(Exception e){
            handleException(e);
        }
        mHandler = new Handler();
        startRepeatingTask();
    }

    private FrameService dispatchService(File file) throws Exception{
        if(FileHelper.getExtension(file).equals("MJPEG"))
            return new MJPEGFrameService(file);
        else if(FileHelper.getExtension(file).equals("MP4"))
            return new MP4FrameService(file);
        return null;
    }

    private void setFrameFromService() throws Exception{
        Bitmap bmp = frameService.getFrame(); //TODO: Optimization. Remove unnecessary conversion
            imageView.setImageBitmap(bmp);
        if(serverConnection != null) serverConnection.writeFrame(bmp);
        if(frameService2 != null) {
            bmp = frameService2.getFrame();
                imageView2.setImageBitmap(bmp);
            if(serverConnection2 != null) serverConnection2.writeFrame(bmp);
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
        toast(e.getMessage());
    }

    private void toast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                setFrameFromService();
            }
            catch (Exception e){
                handleException(e);
                stopRepeatingTask();
            }
            finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

}