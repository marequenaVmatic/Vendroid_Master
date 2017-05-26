package com.vendomatica.vendroid.Model;

public class LogFile {

    public final static String TABLENAME = "log_file";
    public final static String TASKID = "taskid";
    public final static String CAPTURE_FILE = "capture_file";
    public final static String FILE_TYPE = "file_type";
    public final static String FILE_PATH = "file_path";

    private int taskID;
    private String captureFile;
    private String filePath;
    private String fileType;

    //public LogFile(int taskID, String filePath, String captureFile, String fileType) {
    public LogFile(String filePath, String captureFile, String fileType) {
        this.taskID = taskID;
        this.captureFile = captureFile;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    public int getTaskID() {
        return taskID;
    }

    public String getCaptureFile() {
        return captureFile;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() {
        return fileType;
    }
}
