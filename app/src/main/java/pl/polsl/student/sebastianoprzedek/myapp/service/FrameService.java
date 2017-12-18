package pl.polsl.student.sebastianoprzedek.myapp.service;

import android.graphics.Bitmap;
import java.io.IOException;

/**
 * Created by sebas on 14.12.2017.
 */

public interface FrameService {

    public Bitmap getFrame() throws Exception;
    public void closeService() throws IOException;
    public String getFileName();
}
