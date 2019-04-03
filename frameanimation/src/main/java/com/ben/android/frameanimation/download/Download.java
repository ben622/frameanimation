package com.ben.android.frameanimation.download;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/20
 * 帧动画资源下载
 */
public class Download {
    public  static void downloadRemoteSource(final String request, final File dir, final DownloadListener listener) {
        new Thread(new Runnable() {

            private File file;

            @Override
            public void run() {
                try {
                    if (dir == null) {
                        return;
                    }
                    //create cache directory
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    URL url = new URL(request.trim());
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    if (200 == urlConnection.getResponseCode()) {
                        InputStream is = urlConnection.getInputStream();
                        long countLength = urlConnection.getContentLength();
                        long readLength = 0;

                        file = new File(dir, MD5Utils.MD5Encode(request, "utf8"));
                        OutputStream os = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int len = 0;

                        while (-1 != (len = is.read(buffer))) {
                            os.write(buffer, 0, len);
                            readLength += len;
                            float progress = (float) readLength / (float) countLength * 100;
                            if (listener != null) {
                                listener.onProgress(100, (int) progress);
                            }
                            os.flush();
                        }
                        os.close();
                        // TODO: 2019/3/21 Unzip source file
                        ZipUtils.UnZipFolder(file.getAbsolutePath(), dir.getAbsolutePath());
                        //delete zip file
                        file.delete();
                        if (listener != null) {
                            listener.onFinish();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //clear cache
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                    if (listener != null) {
                        listener.onError(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
