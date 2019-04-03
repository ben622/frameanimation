package com.ben.android.frameanimation.loader;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import com.ben.android.frameanimation.FrameEntity;
import com.ben.android.frameanimation.Request;


/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/20
 */
public abstract class Loader implements ILoader<FrameEntity, Request<String>> {
    private static final int MAX_QUEUE_SIZE = 3;
    protected int frame = -1;
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    public FrameEntity loader(Request<String> request) {
        if (request == null && request.getContext() == null) {
            throw new NullPointerException();
        }
        if (request.getQueue().size() >= MAX_QUEUE_SIZE) {
            return null;
        }
        if (request.getOld().size() <= 0) {
            return null;
        }

        FrameEntity frameEntity = FrameEntity.obtain(request);
        //set next rendering bitmap,not recreate frameeneity.
        String url;
        if (frame >= request.getOld().size() - 1) {
            //lastbitmap -> firstbitmap
            frame = 0;
            url = String.valueOf(request.getOld().get(frame));
            frameEntity.setFrame(frame);
        } else {
            frame++;
            url = String.valueOf(request.getOld().get(frame));
            frameEntity.setFrame(frame);
        }

        Bitmap bitmap = getBitmap(request, url);
        if (bitmap == null) {
            return null;
        }
        //bitmap = resizeBitmap(bitmap, request.getWidth() / 2, request.getHeight() / 2);
        frameEntity.setBitmap(bitmap);
        return frameEntity;
    }

    @Override
    public void free() {
        Log.e(TAG, "-->stop animation,free memory");
        frame = -1;
        System.gc();
    }

    protected abstract Bitmap getBitmap(Request<String> request, String url);


    protected Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w;
            int newHeight = h;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            return resizedBitmap;
        } else {
            return null;
        }
    }
}
