package pl.polsl.student.sebastianoprzedek.myapp;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sebas on 14.12.2017.
 */

public class FileHelper {
    public static String findOldestDir(String dir) { //compares using name in yyyymmdd... convention
        ArrayList<String> dirNames = new ArrayList<>();
        File mainDirectory = new File(dir);
        File[] files = mainDirectory.listFiles();
        for (File inFile : files)
            if (inFile.isDirectory())
                dirNames.add(inFile.getName());
        if(dirNames.size()==0) return null;
        String oldestDir = dirNames.get(0);
        for(int i=1; i< dirNames.size(); i++)
            if(oldestDir.compareTo(dirNames.get(i)) < 0)
                oldestDir = dirNames.get(i);
        return dir + "/" + oldestDir;
    }

    public static ArrayList<File> findFilesWithExtension(String dir, String extension) {
        ArrayList<File> properFiles = new ArrayList<>();
        File mainDirectory = new File(dir);
        File[] files = mainDirectory.listFiles();
        for (File inFile : files)
            if (inFile.getName().toUpperCase().endsWith("."+extension))
                properFiles.add(inFile);
        return properFiles;
    }

    public static void checkPath(String name) throws Exception{
        File file = new File(name);
        if(file.exists())
            if(!file.canRead())
                throw new Exception("cannot read file: " + name);
            else throw new Exception("file not exist: " + name);
    }
}
