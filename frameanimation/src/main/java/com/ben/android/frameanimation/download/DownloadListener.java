package com.ben.android.frameanimation.download;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/20
 * 下载监听
 */
public interface DownloadListener {

    void onFinish();

    void onError(Exception e);

    void onProgress(int cout, int cur);
}
