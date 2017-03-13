package com.vendomatica.vendroid.connectivity.helpers;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileHelper {
    private final static String EXT_DIR =  Environment.getExternalStorageDirectory().getAbsolutePath();
    public  final static String MAIN_DIRECTORY = EXT_DIR + "/Vending";

    public static String saveFileWithDate(String filename, List<String> data){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = sdf.format(new Date());

        int idx = filename.indexOf('.');
        if (idx!= -1){
            final String s = filename.substring(0, idx) + '_' + date + filename.substring(idx, filename.length());
            return saveFile(s, data);
        }else
            return saveFile(filename, data);
    }

    public static String saveFile(String filename, List<String> data){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File folder = new File(MAIN_DIRECTORY);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                final String full_name = MAIN_DIRECTORY + File.separator + filename;
                return save(full_name, data);
            }
        }
        return null;
    }


    private static String save(String fileName, List<String> data){
        try {
            File f = new File(fileName);
            f.createNewFile();

            FileWriter writer = new FileWriter(f);

            for (String str : data)
                writer.write(str);

            writer.flush();

            writer.close();
            return fileName;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
