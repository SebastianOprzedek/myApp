package pl.polsl.student.sebastianoprzedek.myapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_DIR_PATH = "/storage/emulated/0/eye/Pupil Mobile/local_recording";
    public static final String CAMERA_ID = "video_18eaccef";
    ImageView imageView;
    FrameService frameService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
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

    private void createFrameService() {
        try {
            frameService = new FrameService(getFile(CAMERA_ID));
            toast("Service created successfully");
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private void setFrameFromService() {
        try {
            Bitmap bmp = frameService.getFrame();
            imageView.setImageBitmap(bmp);
        }
        catch(Exception e){
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        toast(e.getMessage());
    }

    private File getFile(String cameraId) throws Exception {
        String dirPath = findOldestDir(MAIN_DIR_PATH);
        if(dirPath == null) throw new Exception("directory not found in: "+ MAIN_DIR_PATH);
        String path = dirPath + "/" + cameraId + ".mjpeg";
        checkPath(path);
        return new File(path);
    }

    private String findOldestDir(String dir) { //compares using name in yyyymmdd... convention
        ArrayList<String> dirNames = new ArrayList<>();
        File mainDirectory = new File(dir);
        File[] files = mainDirectory.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                dirNames.add(inFile.getName());
            }
        }
        if(dirNames.size()==0) return null;
        String oldestDir = dirNames.get(0);
        for(int i=1; i< dirNames.size(); i++){
            if(oldestDir.compareTo(dirNames.get(i)) < 0)
                oldestDir = dirNames.get(i);
        }
        return dir + "/" + oldestDir;
    }

    private void checkPath(String name) throws Exception{
        File file = new File(name);
        if(file.exists()) {
            if(!file.canRead()) {
                throw new Exception("cannot read file: " + name);
            }
        }
        else throw new Exception("file not exist: " + name);
    }


    private void toast(String s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
