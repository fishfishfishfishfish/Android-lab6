package com.chan.android_lab6;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.sql.Time;
import java.text.SimpleDateFormat;

public class HelloMusic extends AppCompatActivity {
    private MusicService.MyBinder mbinder;
    private MediaPlayer mp;
    Button PlayBtn;
    Button StopBtn;
    Button QuitBtn;
    SeekBar seekBar;
    TextView FullTime;
    TextView CurrTime;
    TextView Status;
    SimpleDateFormat TimeDF = new SimpleDateFormat("mm:ss");
    Handler mHandler;
    ImageView CoverImageView;
    ObjectAnimator CoverAnima;

    private ServiceConnection SC = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("service", "connected");
            mbinder = (MusicService.MyBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            SC = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_music);

        PlayBtn = (Button)findViewById(R.id.play_btn);
        StopBtn = (Button)findViewById(R.id.stop_btn);
        QuitBtn = (Button)findViewById(R.id.quit_btn);
        seekBar = (SeekBar)findViewById(R.id.music_seekbar);
        FullTime = (TextView)findViewById(R.id.full_time_textview);
        CurrTime = (TextView)findViewById(R.id.curr_time_textview);
        Status = (TextView)findViewById(R.id.status);
        mp = MusicService.mp;
        CoverImageView = findViewById(R.id.Album_cover_image);
        CoverAnima = ObjectAnimator.ofFloat(CoverImageView, "rotation", 0.0F, 359.0F);
        CoverAnima.setDuration(3000);
        CoverAnima.setInterpolator(new LinearInterpolator());
        CoverAnima.setRepeatCount(-1);


        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, SC, Context.BIND_AUTO_CREATE);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case 123:
                        try{
                            int code = 104;
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            mbinder.transact(code, data, reply,0);
                            int currtime = reply.readInt();
                            int fulltime = reply.readInt();
                            seekBar.setProgress(currtime);
                            seekBar.setMax(fulltime);
                            FullTime.setText(TimeDF.format(fulltime));
                            CurrTime.setText(TimeDF.format(currtime));
                        }catch(RemoteException e){
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };

        Thread SeekBarThread = new Thread(){
            @Override
            public void run(){
                while(true){
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    if(SC != null){
                        mHandler.obtainMessage(123).sendToTarget();
                    }
                }
            }
        };
        SeekBarThread.start();

        PlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int code = 101;
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mbinder.transact(code, data, reply,0);
                }catch(RemoteException e){
                    e.printStackTrace();
                }

                if(v.getTag().toString().equals("1")){
                    ((Button) v).setText("PAUSED");
                    v.setTag("0");
                }else{
                    ((Button) v).setText("PLAY");
                    v.setTag("1");
                }

                if(v.getTag().equals("1")){
                    Status.setText("paused");
                    CoverAnima.pause();
                }else{
                    Status.setText("playing");
                    CoverAnima.start();
                }
            }
        });

        StopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int code = 102;
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mbinder.transact(code, data, reply,0);
                }catch(RemoteException e){
                    e.printStackTrace();
                }
                PlayBtn.setText("PLAY");
                PlayBtn.setTag("1");
                Status.setText("stopped");
                CurrTime.setText("00:00");
                CoverAnima.end();
            }
        });

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int code = 103;
                    Parcel data = Parcel.obtain();
                    Parcel reply = Parcel.obtain();
                    mbinder.transact(code, data, reply,0);
                }catch(RemoteException e){
                    e.printStackTrace();
                }

                unbindService(SC);
                SC = null;
                try{
                    HelloMusic.this.finish();
                    System.exit(0);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    try{
                        int code = 105;
                        Parcel data = Parcel.obtain();
                        Parcel reply = Parcel.obtain();
                        data.writeInt(progress);
                        mbinder.transact(code, data, reply,0);
                    }catch(RemoteException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
