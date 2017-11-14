package com.chan.android_lab6;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.media.MediaPlayer;

public class MusicService extends Service {
    public static MediaPlayer mp = new MediaPlayer();
    public MusicService() {
        try {

        }catch (Exception e){
            e.
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
