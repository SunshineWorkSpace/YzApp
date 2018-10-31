package com.yingshixiezuovip.yingshi.datautils;

import java.util.HashMap;

/**
 * Created by Resmic on 2016/8/10.
 */

public class TaskInfo {
    private HashMap<String, Object> taskParams;
    private TaskListener taskListener;
    private TaskType taskType;

    public TaskInfo(TaskType taskType, HashMap<String, Object> taskParams, TaskListener taskListener) {
        this.taskType = taskType;
        this.taskParams = taskParams;
        this.taskListener = taskListener;
    }

    public TaskInfo() {
    }

    ;

    public HashMap<String, Object> getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(HashMap<String, Object> taskParams) {
        this.taskParams = taskParams;
    }

    public TaskListener getTaskListener() {
        return taskListener;
    }

    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

}
