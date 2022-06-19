package com.example.drawer.application;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {
    private List<AppCompatActivity> activityList = new LinkedList<AppCompatActivity>();
    private static MyApplication instance;
    private MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    //单例模式中获取唯一的MyApplication实例
    public static MyApplication getInstance() {
        if(null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(AppCompatActivity activity)  {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void exit() {
        for(Activity activity:activityList) {
            activity.finish();
        }
        activityList.clear();
    }
}
