package com.ben.android.frameanimation.weight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.ben.android.frameanimation.AnimationListener;
import com.ben.android.frameanimation.FrameAnimationManager;
import com.ben.android.frameanimation.FrameEntity;
import com.ben.android.frameanimation.IAnimation;
import com.ben.android.frameanimation.IResponse;
import com.ben.android.frameanimation.Request;
import com.ben.android.frameanimation.download.Download;
import com.ben.android.frameanimation.download.DownloadListener;
import com.ben.android.frameanimation.download.MD5Utils;
import com.ben.android.frameanimation.loader.DiskLoader;
import com.ben.android.frameanimation.loader.ILoader;
import com.ben.android.frameanimation.loader.LocalLoader;
import com.ben.android.frameanimation.loader.RequestLoader;

import java.io.File;

/**
 * @author @zhangchuan622@gmail.com
 * @version 1.0
 * @create 2019/3/19
 */
@SuppressLint("AppCompatCustomView")
public class PNGFrameAnimationView extends ImageView implements IAnimation, IResponse<FrameEntity> {
    private static final int MESSAGE = 0x001;
    private static final int CHANGE_SCALE_TYPE = 0x002;
    private static final int RELEASE = 0x003;
    //缓存目录
    public static final String CACHE_DIR = "frames";
    //默认资源
    private static final Integer[] DEFAULT_DOWNLOAD = null;
    //默认帧动画间隔时间
    private static final int DEFAULT_DURATION = 200;
    //根据设置来确定加载器
    private ILoader loader;
    private FrameAnimationManager animationManager;
    private FrameEntity preFrameEntity;
    private Request request;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE) {
                FrameEntity frameEntity = (FrameEntity) msg.obj;
                setImageBitmap(frameEntity.getBitmap());
                //recycle bitmap
                releaseFrameEntity();
                preFrameEntity = frameEntity;
            }
            if (msg.what == CHANGE_SCALE_TYPE) {
                setScaleType((ScaleType) msg.obj);
            }
            if (msg.what == RELEASE) {
                //releaes canvas
                setImageBitmap(null);
            }
        }
    };

    private void releaseFrameEntity() {
      /*  if (preFrameEntity != null && preFrameEntity.getBitmap() != null && !preFrameEntity.getBitmap().isRecycled()) {
            preFrameEntity.getBitmap().recycle();
            preFrameEntity = null;
        }*/
    }

    public PNGFrameAnimationView(Context context) {
        this(context, null);
    }

    public PNGFrameAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PNGFrameAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //initialize
        this.initialize();
    }

    private void initialize() {
        request = Request.<String>obtain(getContext(), DEFAULT_DURATION, getMeasuredWidth(), getMeasuredHeight(), null);
    }

    public void setOnAnimationListener(AnimationListener listener) {
        animationManager.setListener(listener);
    }

    public void setDuration(int duration) {
        request.setDuration(duration);
    }

    public void setRepeatCount(int repeatCount) {
        request.setRepeatCount(repeatCount);
    }

    /**
     * 从网络中加载图片
     *
     * @param urls
     */
    public void setFrameAnimationSourceByNet(String... urls) {
        loader = new RequestLoader();
        for (String url : urls) {
            request.getOld().add(url);
        }
    }

    /**
     * 从本地资源目录中加载
     *
     * @param ids
     */
    public void setFrameAnimationSourceByLocalPackage(Integer... ids) {
        loader = new LocalLoader();
        for (Integer id : ids) {
            request.getOld().add(id);

        }
    }

    /**
     * 从磁盘目录中加载
     *
     * @param directory
     */
    public void setFrameAnimationSourceByLocalDiskDirectory(File directory) {
        if (directory == null) {
            return;
        }
        setFrameAnimationSourceByLocalDiskFiles(directory.listFiles());

    }

    /**
     * 加载指定的文件
     *
     * @param files
     */
    public void setFrameAnimationSourceByLocalDiskFiles(File... files) {
        loader = new DiskLoader();
        for (File file : files) {
            if (file.isDirectory()) {
                setFrameAnimationSourceByLocalDiskDirectory(file);
            } else {
                setFilePathToReqeust(file);
            }
        }
    }

    private void setFilePathToReqeust(File file) {
        //allow .png suffix
        if (!file.getAbsolutePath().endsWith(".png")) {
            return;
        }
        request.getOld().add(file.getAbsolutePath());
    }

    /**
     * 加载服务器中的资源<br/>
     * .如果在指定缓存目录中存在资源文件，则使用本地资源进行播放<br/>
     * .否则先播放@defaultAnim 资源，待下载完成后再播放资源文件
     *
     * @param url
     */
    public synchronized void setFrameAnimationSourceByRemote(String url, Integer... defaultAnim) {
        final int repeatCount = request.getRepeatCount();
        final int duration = request.getDuration();
        final File cacheDir = new File(getContext().getCacheDir() + File.separator + CACHE_DIR + File.separator + MD5Utils.MD5Encode(url, "utf8"));
        if (cacheDir.exists() && cacheDir.listFiles().length > 0) {
            setFrameAnimationSourceByLocalDiskDirectory(cacheDir);
        } else {
            if (defaultAnim != null) {
                request.setRepeatCount(-1);
                request.setDuration(200);
                setFrameAnimationSourceByLocalPackage(defaultAnim == null ? DEFAULT_DOWNLOAD : defaultAnim);
            }
            Download.downloadRemoteSource(url, cacheDir, new DownloadListener() {
                @Override
                public void onFinish() {
                    stop();

                    Message message = Message.obtain();
                    message.what = CHANGE_SCALE_TYPE;
                    message.obj = ScaleType.FIT_CENTER;
                    handler.sendMessage(message);

                    request.setRepeatCount(repeatCount);
                    request.setDuration(duration);
                    setFrameAnimationSourceByLocalDiskDirectory(cacheDir);
                    start();
                }

                @Override
                public void onError(Exception e) {
                    Log.e("download", "download remote source error,please retry");
                }

                @Override
                public void onProgress(int cout, int cur) {
                }
            });
        }
    }

    /**
     * 当开始播放动画时
     */
    @Override
    public void start() {
        if (request == null || loader == null) {
            return;
        }
        request.setWidth(getMeasuredWidth());
        request.setHeight(getMeasuredHeight());
        //check animation status
        if (animationManager != null && animationManager.isRuning()) {
            stop();
        }
        animationManager = new FrameAnimationManager(this, loader);
        animationManager.setRequest(request);
        animationManager.start();
    }

    /**
     * 停止动画
     */
    @Override
    public void stop() {
        if (request == null || loader == null) {
            return;
        }
        animationManager.stop();
        //stop. release bitmap
        releaseFrameEntity();
        handler.sendEmptyMessage(RELEASE);

    }

    /**
     * 如果处于播放状态，该方法将会不停的被回调，时间间隔为request中指定的duration
     *
     * @param frameEntity
     */
    @Override
    public void response(FrameEntity frameEntity) {
        if (getContext() instanceof Activity && ((Activity) getContext()).isFinishing()) {
            // TODO: 2019/3/19 Activity is finishing
            return;
        }
        if (frameEntity == null && !isRuning()) {
            return;
        }
        Message message = Message.obtain();
        message.obj = frameEntity;
        message.what = MESSAGE;
        handler.sendMessage(message);
    }

    public boolean isRuning() {
        return animationManager.isRuning();
    }
}
