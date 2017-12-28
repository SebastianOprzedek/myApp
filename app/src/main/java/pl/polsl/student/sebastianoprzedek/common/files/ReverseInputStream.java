package pl.polsl.student.sebastianoprzedek.common.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class ReverseInputStream extends InputStream {
    RandomAccessFile in;
    long currentPos = -1;
    File file;

    public ReverseInputStream(File file) throws FileNotFoundException {
        in = new RandomAccessFile(file, "r");
        currentPos = file.length() -1;
        this.file = file;
    }

    public int read() throws IOException {
        currentPos--;
        if(currentPos < 0) {
            return -1;
        }
        in.seek(currentPos);
        return in.read();
    }

    public void moveToEnd(){
        currentPos = file.length()-1;
    }

    @Override
    public void close() throws IOException {
        if (in != null) {
            in.close();
            in = null;
        }
    }
}