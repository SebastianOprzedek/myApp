package pl.polsl.student.sebastianoprzedek.myapp.service;

import android.graphics.Bitmap;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_videoio;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import java.io.File;
import java.io.IOException;
import pl.polsl.student.sebastianoprzedek.common.helper.ByteHelper;

/**
 * Created by Sebastian Oprzędek on 18.12.2017.
 */

public class VideoCaptureService implements FrameService {

    opencv_videoio.VideoCapture videoCapture;
    String filename;
    public VideoCaptureService(File file) throws Exception{
        videoCapture = new opencv_videoio.VideoCapture(file.getAbsolutePath());
        filename = file.getName();
    }

    public void closeService() throws IOException{
        videoCapture.close();
    }

    public Bitmap getFrame() throws Exception{
        opencv_core.Mat image = new opencv_core.Mat();
        videoCapture.read(image);
        Frame frame = new OpenCVFrameConverter.ToMat().convert(image);
        return new AndroidFrameConverter().convert(frame);
    }

    public byte[] getFrameBytes() throws Exception{
        return ByteHelper.bitmapToByteArray(getFrame());
    }

    public String getFileName(){
        return filename;
    }

}
