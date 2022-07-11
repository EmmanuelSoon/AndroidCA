package nus.iss.androidca.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import nus.iss.androidca.R;


public class BgmService extends Service {

    private MediaPlayer mp = null;

    public BgmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate(){
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action.equalsIgnoreCase("play")){
            
            if (mp != null){
                stopMusic();
            }

            if(intent.getStringExtra("location").equalsIgnoreCase("home")){

                mp = MediaPlayer.create(this, R.raw.home_main);
            }
            else if(intent.getStringExtra("location").equalsIgnoreCase("game")){
                mp = MediaPlayer.create(this, R.raw.bgm);
            }
            
            //fade in here?

            mp.setLooping(true); // Set looping
            mp.setVolume(100, 100);
            mp.start();

        }


        return super.onStartCommand(intent, flags, startId);

    }



    public void stopMusic(){
        mp.stop();
        mp.release();;
        mp = null;
    }


}