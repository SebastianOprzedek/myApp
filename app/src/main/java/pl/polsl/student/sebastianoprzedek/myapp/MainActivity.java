package pl.polsl.student.sebastianoprzedek.myapp;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    public static final int PERIOD = 2000;
    ImageView imageView;
    ImageView imageView2;
    FrameService frameService;
    FrameService frameService2;
    private int mInterval = PERIOD;
    private Handler mHandler;
    ServerConnection serverConnection;
    ServerConnection serverConnection2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        disableStrictPolicy();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Button openButton = (Button) findViewById(R.id.open);
        Button connectButton = (Button) findViewById(R.id.connect);
        Button getFrameButton = (Button) findViewById(R.id.getFrame);
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createFrameService();
            }
        });
        getFrameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setFrameFromService();
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                initServerConnections();
            }
        });
    }

    private void initServerConnections() {
        try {
            if (frameService != null)
                serverConnection = new ServerConnection(frameService.getFileName());
            if (frameService2 != null)
                serverConnection2 = new ServerConnection(frameService2.getFileName());
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

    private void createFrameService() {
        try {
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

    private void setFrameFromService() {
        try {
            Bitmap bmp = frameService.getFrame(); //TODO: Optimization. Remove unnecessary conversion
            imageView.setImageBitmap(bmp);
            if(serverConnection != null) serverConnection.writeFrame(bmp);
            if(frameService2 != null) {
                bmp = frameService2.getFrame();
                imageView2.setImageBitmap(bmp);
                if(serverConnection2 != null) serverConnection2.writeFrame(bmp);
            }
        }
        catch(Exception e){
            handleException(e);
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
            } finally {
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