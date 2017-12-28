package pl.polsl.student.sebastianoprzedek.myapp.service;

import android.graphics.Bitmap;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import java.io.File;
import java.io.IOException;
import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;

/**
 * Created by sebas on 14.12.2017.
 */

public class MP4FrameService implements FrameService {

    File file;
    FFmpegFrameGrabber g;

    public MP4FrameService(File file) throws Exception{
        this.file = file;
        g = new FFmpegFrameGrabber(file.getAbsoluteFile());
        g.start();
    }

    public void closeService() throws IOException{
        g.stop();
    }

    public byte[] getNextFrameBytes() throws Exception{
        return ByteHelper.bitmapToByteArray(getNextFrame());
    }

    public Bitmap getNextFrame() throws Exception{
        return new AndroidFrameConverter().convert(g.grabImage());
    }

    public byte[] getLastFrameBytes() throws Exception{
        return null;
    }

    public Bitmap getLastFrame() throws Exception{
        return null;
    }

    public String getFileName(){
        return file.getName();
    }
}
