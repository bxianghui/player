package com.example.drawer.entity;

import java.util.ArrayList;
import java.util.List;

public class PlayMode {
    private static int mode=-1;

    public static void setMode(int mode) {
        PlayMode.mode = mode;
    }

    public static int getMode() {
        return mode;
    }

    private  static int position=-1;

    public static int getPosition() {
        return position;
    }

    public static void setPosition(int position) {
        PlayMode.position = position;
    }
    private static List<Music> list=new ArrayList<>();

    public static List<Music> getList() {
        return list;
    }

    public static void setList(List<Music> list) {
        PlayMode.list = list;
    }

    public static boolean isPlaying=true;

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        PlayMode.isPlaying = isPlaying;
    }
}
