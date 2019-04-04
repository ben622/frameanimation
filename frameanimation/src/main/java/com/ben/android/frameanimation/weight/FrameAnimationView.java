package com.ben.android.frameanimation.weight;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FrameAnimationView extends FrameLayout {
    private static final String[] CONCAT_PNG = {".zip", ".rar"};
    private static final String CONCAT_SVG = ".svg";


    private PNGFrameAnimationView pngFrameAnimationView;
    private SVGAImageView svgaImageView;

    private String remoteUrl;

    public FrameAnimationView(Context context) {
        this(context, null);
    }

    public FrameAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrameAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        try {
            //configuration cache
            File cacheDir = new File(getContext().getApplicationContext().getCacheDir(), "http");
            HttpResponseCache.install(cacheDir, 1024 * 1024 * 128);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pngFrameAnimationView = new PNGFrameAnimationView(getContext());
        svgaImageView = new SVGAImageView(getContext());

        addView(pngFrameAnimationView);
        addView(svgaImageView);
    }


    /**
     * 以URL形式播放服务端资源<br>
     *     根据URL自动判断播放类型
     * @param remoteUrl
     */
    public void playFrameAnimationByUrl(String remoteUrl,int duration) {
        if (remoteUrl.isEmpty()) {
            return;
        }
        //clear runing animation
        stopAnimation();
        this.remoteUrl = remoteUrl;
        if (isContains(CONCAT_PNG, remoteUrl)) {
            //load png
            loadByPNG(duration);
        } else if (remoteUrl.contains(CONCAT_SVG)) {
            //load svg
            loadBySVG();
        }
    }

    private boolean isContains(String[] strs,String match) {
        for (String str : strs) {
            if (match.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 基于文件形式播放，只能播放.png文件
     * @param files
     */
    public void playFrameAnimationByFiles(int duration,File... files) {
        stopAnimation();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        pngFrameAnimationView.setLayoutParams(layoutParams);
        pngFrameAnimationView.setDuration(duration);
        pngFrameAnimationView.setFrameAnimationSourceByLocalDiskFiles(files);
        pngFrameAnimationView.start();
    }

    /**
     * 播放包资源文件
     * @param ids
     */
    public void playFrameAnimationByPackage(int duration,Integer... ids) {
        stopAnimation();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        pngFrameAnimationView.setLayoutParams(layoutParams);
        pngFrameAnimationView.setDuration(duration);
        pngFrameAnimationView.setFrameAnimationSourceByLocalPackage(ids);
        pngFrameAnimationView.start();
    }




    private void loadByPNG(int duration) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = LayoutParams.MATCH_PARENT;
        layoutParams.height = LayoutParams.MATCH_PARENT;
        pngFrameAnimationView.setLayoutParams(layoutParams);
        pngFrameAnimationView.setDuration(duration);
        pngFrameAnimationView.setFrameAnimationSourceByRemote(remoteUrl);
        pngFrameAnimationView.start();

    }

    private void loadBySVG() {
        try {
            //load svgimageview
            FrameLayout.LayoutParams layoutParams = (LayoutParams) svgaImageView.getLayoutParams();
            layoutParams.width = LayoutParams.MATCH_PARENT;
            layoutParams.height = LayoutParams.MATCH_PARENT;
            svgaImageView.setLayoutParams(layoutParams);

            //decode svg
            SVGAParser parser = new SVGAParser(getContext());
            parser.decodeFromURL(new URL(remoteUrl), new SVGAParser.ParseCompletion() {

                @Override
                public void onError() {
                    Toast.makeText(getContext(), "动画资源加载失败，请重试！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete(SVGAVideoEntity videoItem) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    svgaImageView.setImageDrawable(drawable);
                    svgaImageView.startAnimation();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAnimation() {
        pngFrameAnimationView.stop();
        svgaImageView.stopAnimation();
    }
}
