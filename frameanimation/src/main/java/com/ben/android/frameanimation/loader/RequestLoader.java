package com.ben.android.frameanimation.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.ben.android.frameanimation.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 * @desc 网络请求
 */
public class RequestLoader extends Loader {
    @Override
    protected Bitmap getBitmap(Request<String> request, String url) {
        byte[] bytes = getBitmapByteByRequestUrl(url);
        if (bytes == null) {
            return null;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        int outWidth = opts.outWidth;
        int outHeight = opts.outHeight;
        if (outWidth > request.getWidth() || outHeight > request.getHeight()) {
            //optimization
            opts.inJustDecodeBounds = false;
            int widthSampleSize = Math.round((float) outWidth / (float) request.getWidth());
            int heightSampleSize = Math.round((float) outHeight / (float) request.getHeight());
            opts.inSampleSize = Math.max(widthSampleSize, heightSampleSize);
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
        } else {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

        return bitmap;
    }


    public byte[] getBitmapByteByRequestUrl(String path) {
        try {
            URL url = new URL(path.trim());
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (200 == urlConnection.getResponseCode()) {
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;

                while (-1 != (len = is.read(buffer))) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                }
                return baos.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
