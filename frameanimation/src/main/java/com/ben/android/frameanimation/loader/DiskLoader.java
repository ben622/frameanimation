package com.ben.android.frameanimation.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.ben.android.frameanimation.Request;

import java.io.File;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/20
 * 从磁盘指定目录加载
 */
public class DiskLoader extends Loader{
    @Override
    protected Bitmap getBitmap(Request<String> request, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        if (outWidth > request.getWidth() || outHeight > request.getHeight()) {
            //optimization
            opts.inJustDecodeBounds = false;
            int widthSampleSize = Math.round((float) outWidth / (float) request.getWidth());
            int heightSampleSize = Math.round((float) outHeight / (float) request.getHeight());
            opts.inSampleSize = Math.max(widthSampleSize, heightSampleSize);
            BitmapFactory.decodeFile(path, opts);
        } else {
            bitmap = BitmapFactory.decodeFile(path);
        }
        return bitmap;
    }
}
