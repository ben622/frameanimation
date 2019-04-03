package com.ben.android.frameanimation.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ben.android.frameanimation.Request;


/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 * @desc 加载本地资源
 */
public class LocalLoader extends Loader {

    @Override
    protected Bitmap getBitmap(Request<String> request, String url) {
        int id = Integer.parseInt(url);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap;
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        if (outWidth > request.getWidth()  || outHeight > request.getHeight() ) {
            //optimization
            opts.inJustDecodeBounds = false;
            int widthSampleSize = Math.round((float) outWidth / (float) request.getWidth());
            int heightSampleSize = Math.round((float) outHeight / (float) request.getHeight());
            opts.inSampleSize = Math.max(widthSampleSize, heightSampleSize);
            bitmap = BitmapFactory.decodeResource(request.getContext().getResources(), id, opts);
        } else {
            bitmap = BitmapFactory.decodeResource(request.getContext().getResources(), id);
        }
        return bitmap;
    }
}
