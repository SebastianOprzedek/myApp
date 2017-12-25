package pl.polsl.student.sebastianoprzedek.common.helper.exceptions;

/**
 * Created by Sebastian Oprzędek on 25.12.2017.
 */

public class FileTooBigException extends Exception {
    String filename;

    public FileTooBigException(String filename){
        this.filename = filename;
    }

    @Override
    public String getMessage() {
        return "File too big: " + filename;
    }
}
