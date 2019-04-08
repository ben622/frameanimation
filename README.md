#### PNG格式支持多帧多实例动画播放，由于采用生产者消费者模型处理帧资源，对内存的开销较少。以帧资源500+为例加载PNG动画，内存消耗均为最大队列个数*处理后Bitmap内存开销。

## 在XML布局文件中
```
 <com.ben.android.frameanimation.weight.FrameAnimationView
    android:id="@+id/id_frameview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
## 在Activity中播放PNG动画
```
/基于文件形式的PNG资源
frameAnimationView.playFrameAnimationByFiles(duration,files);
//基于本地资源形式的PNG
frameAnimationView.playFrameAnimationByPackage(duration,ids);
//基于服务端资源
frameAnimationView2.playFrameAnimationByUrl(url, 30);
```
## 在Activity中播放SVG资源
```
frameAnimationView.playFrameAnimationByUrl("https://github.com/yyued/SVGA-Samples/blob/master/posche.svga?raw=true", 30);
```

<img src="./preview.gif" width="400px" height="800px" />
