package com.ben.android.frameanimation.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ben.android.frameanimation.weight.FrameAnimationView;


public class MainActivity extends AppCompatActivity {
    private FrameAnimationView frameAnimationView;
    private FrameAnimationView frameAnimationView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frameAnimationView = findViewById(R.id.id_frameview);
        frameAnimationView2 = findViewById(R.id.id_frameview2);

    }

    public void startAnimationBySVGEvent(View view) {
        frameAnimationView.playFrameAnimationByUrl("https://github.com/yyued/SVGA-Samples/blob/master/posche.svga?raw=true", 30);
        //frameAnimationView2.playFrameAnimationByUrl("https://github.com/yyued/SVGA-Samples/blob/master/posche.svga?raw=true", 30);
    }


    public void startAnimationByPNGEvent(View view) {
        frameAnimationView.playFrameAnimationByUrl("http://169.1.1.23:8080/frames.zip", 30);
        frameAnimationView2.playFrameAnimationByUrl("http://169.1.1.23:8080/frames2.zip", 30);
    }

    public void stopAnimationEvent(View view) {
        frameAnimationView.stopAnimation();
        frameAnimationView2.stopAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        frameAnimationView.stopAnimation();
        frameAnimationView2.stopAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
