package com.ben.android.frameanimation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 * @desc Bitmap请求模型
 */
public class Request<T> {
    private Context context;
    //帧动画间隔时间，在生产者线程中体现
    private int duration;
    private int repeatMode;
    private int repeatCount;
    private int width;
    private int height;
    //是否对图片做缓存处理
    private boolean isCache = true;
    //是否根据Request中的部分参数自动优化图片加载大小
    private int isAutoOptimizaiton;
    //request请求元数据
    private List<T> old;
    private LinkedBlockingDeque<FrameEntity> queue = new LinkedBlockingDeque<>();


    private Request(Context context, int duration, int width, int height, List<T> old) {
        this.context = context;
        this.duration = duration;
        this.width = width;
        this.height = height;
        this.old = old == null ? new ArrayList<T>() : old;
    }

    public static <T> Request<T> obtain(Context context, int duration, int width, int height, List<T> old) {
        return new Request<T>(context, duration, width, height, old);
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public int getIsAutoOptimizaiton() {
        return isAutoOptimizaiton;
    }

    public void setIsAutoOptimizaiton(int isAutoOptimizaiton) {
        this.isAutoOptimizaiton = isAutoOptimizaiton;
    }

    public List<T> getOld() {
        return old;
    }

    public void setOld(List<T> old) {
        this.old = old;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public LinkedBlockingDeque<FrameEntity> getQueue() {
        return queue;
    }

    public void setQueue(LinkedBlockingDeque<FrameEntity> queue) {
        this.queue = queue;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    @Override
    public String toString() {
        return "Request{" +
                "context=" + context +
                ", duration=" + duration +
                ", repeatMode=" + repeatMode +
                ", repeatCount=" + repeatCount +
                ", width=" + width +
                ", height=" + height +
                ", isCache=" + isCache +
                ", isAutoOptimizaiton=" + isAutoOptimizaiton +
                ", old=" + old +
                ", queue=" + queue +
                '}';
    }
}
