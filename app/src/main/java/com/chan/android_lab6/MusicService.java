package com.chan.android_lab6;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.RemoteException;

public class MusicService extends Service {
    public static MediaPlayer mp = new MediaPlayer();
    private MyBinder mBinder = new MyBinder();
    public MusicService() {
        try {
            mp.setDataSource("/data/melt.mp3");
            mp.prepare();
            mp.setLooping(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    public class MyBinder extends Binder{
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException{
            switch (code){
                case 101:
                    //play
                    if(mp.isPlaying()){
                        mp.pause();
                    }else{
                        mp.start();
                    }
                    break;
                case 102:
                    //stop
                    if(mp != null){
                        mp.seekTo(0);
                        mp.stop();  // 调用后MusicPlayer不能再播放音频
                        try{
                            mp.prepare();
                            mp.seekTo(0);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 103:
                    //exit
                    if(mp != null){
                        mp.stop();  // 调用后MusicPlayer不能再播放音频
                        mp.release();  //释放和MusicPlayer相关的资源
                    }
                    break;
                case 104:
                    //refresh UI, return time
                    reply.writeInt(mp.getCurrentPosition());
                    reply.writeInt(mp.getDuration());
                    break;
                case 105:
                    //drag the process bar
                    int destination = data.readInt();
                    mp.seekTo(destination);
                    break;
            }
            return super.onTransact(code, data, reply, flags);
        }
    }
}
