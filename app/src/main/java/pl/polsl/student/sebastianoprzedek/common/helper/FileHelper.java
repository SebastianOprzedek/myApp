package pl.polsl.student.sebastianoprzedek.common.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static List<File> filterNotContainsSubstring(String substring, ArrayList<File> files) {
        List<File> result = new ArrayList<>();
        for(int i=0; i<files.size(); i++)
            if(!files.get(i).getName().contains(substring))
                result.add(files.get(i));
        return result;
    }

    public static List<File> filterNotEmpty(ArrayList<File> files) {
        List<File> result = new ArrayList<>();
        for(int i=0; i<files.size(); i++)
            if(files.get(i).length() != 0)
                result.add(files.get(i));
        return result;
    }

    public static String getExtension(File file) {
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i+1).toUpperCase();
        }
        return extension;
    }
}
