package com.github.davidji80.videoplayer;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    private final String Tag = MainActivity.class.getSimpleName();

    //定义一个媒体播发对象
    private MediaPlayer mMediaPlayer;
    //定义一个缓冲区句柄（由屏幕合成程序管理）
    private Surface surface;

    //封面
    private ImageView videoImage;
    //进度条
    private SeekBar seekBar;

    //为多线程定义Handler
    private Handler handler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextureView textureView=findViewById(R.id.textureview);
        //为textureView设置监听
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        videoImage=findViewById(R.id.video_image);

        seekBar= findViewById(R.id.seekbar);
        //为seekbar设置监听
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    /**
     * 定义一个Runnable对象
     * 用于更新播发进度
     */
    private final Runnable mTicker = new Runnable(){
        @Override
        public void run() {
            //延迟200ms再次执行runnable,就跟计时器一样效果
            handler.postDelayed(mTicker,200);

            if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
                //更新播放进度
                seekBar.setProgress(mMediaPlayer.getCurrentPosition());
            }
        }
    };

    /**
     * 定义一个线程，用于播发视频
     */
    private class PlayerVideoThread extends Thread{
        @Override
        public void run(){
            try {
                mMediaPlayer= new MediaPlayer();
                //把res/raw的资源转化为Uri形式访问(android.resource://)
                Uri uri = Uri.parse("android.resource://com.github.davidji80.videoplayer/"+R.raw.ansen);
                //设置播放资源(可以是应用的资源文件／url／sdcard路径)
                mMediaPlayer.setDataSource(MainActivity.this,uri);
                //设置渲染画板
                mMediaPlayer.setSurface(surface);
                //设置播放类型
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //播放完成监听
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
                //预加载监听
                mMediaPlayer.setOnPreparedListener(onPreparedListener);
                //设置是否保持屏幕常亮
                mMediaPlayer.setScreenOnWhilePlaying(true);
                //同步的方式装载流媒体文件
                mMediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当装载流媒体完毕的时候回调
     */
    private MediaPlayer.OnPreparedListener onPreparedListener=new MediaPlayer.OnPreparedListener(){
        @Override
        public void onPrepared(MediaPlayer mp){
            //隐藏图片
            videoImage.setVisibility(View.GONE);
            //开始播放
            mMediaPlayer.start();
            //设置总进度
            seekBar.setMax(mMediaPlayer.getDuration());
            Log.e(Tag+"Duration",Integer.toString(mMediaPlayer.getDuration()));
            //用线程更新进度
            handler.post(mTicker);
        }
    };

    /**
     * 流媒体播放结束时回调类
     */
    private MediaPlayer.OnCompletionListener onCompletionListener=new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            videoImage.setVisibility(View.VISIBLE);
            seekBar.setProgress(0);
            //删除执行的Runnable 终止计时器
            handler.removeCallbacks(mTicker);
        }
    };

    /**
     * 定义TextureView监听类SurfaceTextureListener
     * 重写4个方法
     */
    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener(){

        /**
         * 初始化好SurfaceTexture后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            surface=new Surface(surfaceTexture);
            //开启一个线程去播放视频
            new PlayerVideoThread().start();
        }

        /**
         * 视频尺寸改变后调用
         * @param surfaceTexture
         * @param i
         * @param i1
         */
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        /**
         * SurfaceTexture即将被销毁时调用
         * @param surfaceTexture
         * @return
         */
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            surface=null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer=null;
            return true;
        }

        /**
         * 通过SurfaceTexture.updateteximage()更新指定的SurfaceTexture时调用
         * @param surfaceTexture
         */
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    /**
     * 定义SeekBar监听类OnSeekBarChangeListener
     * 重写3个方法
     */
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener=new SeekBar.OnSeekBarChangeListener() {

        /**
         * 进度级别已经更改的通知
         * @param seekBar
         * @param i
         * @param b
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        /**
         * 用户已经开始了一个触摸手势的通知
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //如果在播放中，指定视频播发位置
            if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
        }

        /**
         * 用户已经结束了一个触摸手势的通知
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Log.e(Tag+"Progress",Integer.toString(seekBar.getProgress()));
            //如果在播放中，指定视频播发位置
            if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
                mMediaPlayer.seekTo(seekBar.getProgress());
            }else{
                mMediaPlayer.seekTo(seekBar.getProgress());
                mMediaPlayer.start();
            }
        }
    };

}
