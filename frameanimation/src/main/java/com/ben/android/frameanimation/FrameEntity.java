package com.ben.android.frameanimation;

import android.graphics.Bitmap;

public class FrameEntity {
    private Bitmap bitmap;
    private int width;
    private int height;
    //当前动画的播放帧
    private int frame;

    private FrameEntity( int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static FrameEntity obtain(Request request) {
        if (request == null) {
            throw new NullPointerException();
        }
        return new FrameEntity(request.getWidth(), request.getHeight());
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    public String toString() {
        return "FrameEntity{" +
                "bitmap=" + bitmap +
                ", width=" + width +
                ", height=" + height +
                ", frame=" + frame +
                '}';
    }
}
