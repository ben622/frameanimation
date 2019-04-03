package com.ben.android.frameanimation;


import com.ben.android.frameanimation.loader.ILoader;

public class FrameAnimationManager implements IAnimation {
    private IResponse response;
    private ILoader loader;
    private Request request;
    private Thread dealThread;
    private Thread resThread;
    private boolean isCancel = true;
    private boolean isRuning = false;
    private AnimationListener listener;
    private Condition condition;

    public FrameAnimationManager(IResponse response, ILoader loader) {
        this.response = response;
        this.loader = loader;
        condition = new Condition();
    }

    public void setListener(AnimationListener listener) {
        this.listener = listener;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }

    public ILoader getLoader() {
        return loader;
    }

    public void setLoader(ILoader loader) {
        this.loader = loader;
    }

    private void createThread() {
        //处理帧
        dealThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isCancel) {
                    if (loader != null) {
                        try {
                            Object obj = FrameAnimationManager.this.loader.loader(request);
                            if (obj == null) {
                                synchronized (condition) {
                                    //超过最大约束，等到渲染后继续load
                                    condition.wait();
                                }
                                continue;
                            }
                            request.getQueue().put(obj);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //响应渲染帧
        resThread = new Thread(new Runnable() {
            int count = 0;
            @Override
            public void run() {
                while (!isCancel) {
                    try {
                        //设置了重复播放次数
                        if (request.getRepeatCount() > 0) {
                            if (count >= request.getRepeatCount()) {
                                stop();
                                break;
                            }
                        }
                        //从队列中获取一帧
                        FrameEntity frameEntity = (FrameEntity) request.getQueue().take();
                        synchronized (condition) {
                            //notify dealthread
                            condition.notify();
                        }
                        if (response != null) {
                            //分发repeat事件
                            if (frameEntity.getFrame() == request.getOld().size() - 1) {
                                count++;
                                if (listener != null) {
                                    listener.onAnimationRepeat();
                                }
                            }
                            //将当前帧渲染至view上
                            response.response(frameEntity);
                        }
                        //在指定的时间内休眠
                        Thread.sleep(request.getDuration());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public synchronized void start() {
        if (isRuning()) {
            return;
        }
        if (listener != null) {
            listener.onAnimationStart();
        }
        createThread();
        isCancel = false;
        isRuning = true;
        dealThread.start();
        resThread.start();
    }

    @Override
    public synchronized void stop() {
        isCancel = true;
        isRuning = false;

        if (request != null) {
            request.getQueue().clear();
            request.getOld().clear();
        }
        if (listener != null) {
            listener.onAnimationEnd();
        }
        response = null;

        //release memeory
        if (loader != null) {
            loader.free();
        }
    }

    public boolean isRuning() {
        return isRuning;
    }


    class Condition {}
}
