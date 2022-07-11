package nus.iss.androidca.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import nus.iss.androidca.R;


public class BgmService extends Service {

    private MediaPlayer mp = null;
    private float volume;

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
            volume = 0;
            if (mp != null){
                stopMusic();
            }

            if(intent.getStringExtra("location").equalsIgnoreCase("home")){

                mp = MediaPlayer.create(this, R.raw.home_main);
            }
            else if(intent.getStringExtra("location").equalsIgnoreCase("game")){
                mp = MediaPlayer.create(this, R.raw.bgm);
            }
            

            mp.setLooping(true); // Set looping
//            mp.setVolume(100, 100);
            mp.start();
            startFadeIn();

        }

        else if(action.equalsIgnoreCase("pause")){
            mp.pause();
        }


        return super.onStartCommand(intent, flags, startId);

    }



    public void stopMusic(){
        mp.stop();
        mp.release();;
        mp = null;
    }

    private void startFadeIn(){
        final int FADE_DURATION = 2000;
        final int FADE_INTERVAL = 200;
        final int MAX_VOLUME = 1;
        int numberOfSteps = FADE_DURATION/FADE_INTERVAL;
        final float deltaVolume = MAX_VOLUME / (float)numberOfSteps;

        final Timer timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume);
                if(volume>=1f){
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask,FADE_INTERVAL,FADE_INTERVAL);
    }

    private void fadeInStep(float deltaVolume){
        mp.setVolume(volume, volume);
        volume += deltaVolume;
    }






}