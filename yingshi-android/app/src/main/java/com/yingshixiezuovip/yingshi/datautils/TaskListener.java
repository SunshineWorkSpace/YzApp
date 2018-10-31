package com.yingshixiezuovip.yingshi.datautils;

public interface TaskListener {
    void taskStarted(TaskType type);

    void taskFinished(TaskType type, Object result, boolean isHistory);

    void taskIsCanceled(TaskType type);
}