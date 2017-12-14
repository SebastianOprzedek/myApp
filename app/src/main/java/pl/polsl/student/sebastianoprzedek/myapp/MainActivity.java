package pl.polsl.student.sebastianoprzedek.myapp;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_DIR_PATH = "/storage/emulated/0/eye/Pupil Mobile/local_recording";
    public static final int PERIOD = 1000;
    ImageView imageView;
    ImageView imageView2;
    FrameService frameService;
    FrameService frameService2;
    private int mInterval = PERIOD;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        Button connectButton = (Button) findViewById(R.id.connect);
        Button getFrameButton = (Button) findViewById(R.id.getFrame);
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createFrameService();
            }
        });
        getFrameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setFrameFromService();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void createFrameService() {
        try {
            List<File> files = getFiles();
            if(files.size() < 1) throw new Exception("no mjpeg files found in oldest folder in: "+ MAIN_DIR_PATH);
            frameService = new FrameService(files.get(0));
            if(files.size() > 1) frameService2 = new FrameService(files.get(1));
            toast("Service created successfully");
        }
        catch(Exception e){
            handleException(e);
        }
        mHandler = new Handler();
        startRepeatingTask();
    }

    private void setFrameFromService() {
        try {
            Bitmap bmp = frameService.getFrame();
            imageView.setImageBitmap(bmp);
            if(frameService2 != null) {
                bmp = frameService2.getFrame();
                imageView2.setImageBitmap(bmp);
            }
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private ArrayList<File> getFiles() throws Exception {
        String dirPath = FileHelper.findOldestDir(MAIN_DIR_PATH);
        if(dirPath == null) throw new Exception("directory not found in: "+ MAIN_DIR_PATH);
        ArrayList<File> files = FileHelper.findFilesWithExtension(dirPath, "MJPEG");
        return files;
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