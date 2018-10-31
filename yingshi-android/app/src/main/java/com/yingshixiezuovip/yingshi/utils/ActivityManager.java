package com.yingshixiezuovip.yingshi.utils;

import android.app.Activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于处理退出程序时可以退出所有的activity，而编写的通用类
 *
 * @author duanyanrui
 * @date 2012-9-9
 */
@SuppressWarnings("JavaDoc")
public class ActivityManager {

    private List<Activity> activityList = new LinkedList<Activity>();
    private HashMap<String, Activity> activityHashMap = new HashMap<>();
    private static ActivityManager instance;
    private static long mlastTime = 0;

    private ActivityManager() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public static ActivityManager getInstance() {
        if (null == instance) {
            synchronized (ActivityManager.class) {
                instance = new ActivityManager();
            }
        }
        return instance;
    }


    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
        activityHashMap.put(activity.getClass().getName(), activity);
    }

    public Activity getActivity(String classname) {
        return activityHashMap.get(classname);
    }

    public void removeActivity(Activity activity) {
        activityList.remove(activity);
        activityHashMap.remove(activity.getClass().getName());
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    public int size() {
        return activityList.size();
    }

    public boolean isBackground() {
        return activityList.size() == 0;
    }
}