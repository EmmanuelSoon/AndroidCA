package nus.iss.androidca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import nus.iss.androidca.service.BgmService;

public class GameActivity extends AppCompatActivity implements GameFragment.IGameFragment{

    private TextView txtScore;
    private int matchCounter;
    private int startTime;
    private Handler handler = new Handler();
    private boolean onStop = false;
    private SharedPreferences userHighScoreDetails;
    private TinyDB tinydb;
    private Integer playerRankSize = 0;
    private MediaPlayer mp;
    private float volume = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        txtScore = findViewById(R.id.matchCount);
        txtScore.setText(String.valueOf(matchCounter));
        startTime = 0;
        matchCounter = 0;
        txtScore.setText(matchCounter + " of 6 matches");
        runTimer();
        tinydb = new TinyDB(this);
        Intent intent = getIntent();

        String[] cardFiles = intent.getStringArrayExtra("cardFiles");
        Bitmap[] bitmaps = processDownloadedImage(cardFiles);
        if (bitmaps == null) {
            // if there is a error when converting file to bitmap
            finish();
        }
        /*
        // send data to our fragment
        Bitmap[] bitmaps = new Bitmap[6];
        bitmaps[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card1);
        bitmaps[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card2);
        bitmaps[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card3);
        bitmaps[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card4);
        bitmaps[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card5);
        bitmaps[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card6);
        */
        Bitmap defaultBitmaps = BitmapFactory.decodeResource(this.getResources(), R.drawable.cardback1);
        FragmentManager fm = getSupportFragmentManager();
        GameFragment fragment = (GameFragment) fm.findFragmentById(R.id.fragment_game);
        fragment.setBitmaps(bitmaps, defaultBitmaps);

        restartMusic();
    }

    private void restartMusic(){
        if(mp == null){
            mp = MediaPlayer.create(this, R.raw.bgm);
        }
        if (!mp.isPlaying()) {
            mp.setLooping(true);
            mp.seekTo(0);
            mp.start();
            startFadeIn();
        }
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

    private void runTimer() {
        TextView txtTime = findViewById(R.id.timer);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!onStop) {
                    startTime = startTime + 1;
                    int seconds = startTime;
                    int minutes = seconds / 60;
                    int hours = minutes / 60;
                    seconds = seconds % 60;
                    minutes = minutes % 60;
                    txtTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    handler.postDelayed(this, 1000);
                }

            }
        });
    }
    @Override
    public void itemClicked(String content) {
        if (content.equals("match")) {
            matchCounter++;
            txtScore.setText(matchCounter + " of 6 matches");
            Toast.makeText(this, "Match!", Toast.LENGTH_SHORT).show();
        }
        else if(content.equals("over")) {
            mp.pause();
            onStop= true;
            handler.removeCallbacksAndMessages(null);
            ArrayList<Integer> rankingList = tinydb.getListInt("rankingList");
            int secsTaken = startTime;
            String msg = "";
            if (rankingList.isEmpty()) {
                msg = "Thanks for being our first player!";
            }
            else {
                int rank = findRanking(secsTaken, rankingList);
                double percentage = (1.0 - (rank*1.0)/rankingList.size())*100.0;
                msg = String.format("You are ranked in %d and beat %.1f%% players", rank+1, percentage);
            }
            rankingList.add(secsTaken);
            tinydb.putListInt("rankingList", rankingList);
            txtScore.setText("Game Over!");
            Toast.makeText(this, "You Win!", Toast.LENGTH_LONG).show();
            showPopuptoPlayer(startTime, msg);
        }
    }

    private void initGameAttribute() {
        matchCounter = 0;
        txtScore.setText(matchCounter + " of 6 matches");
    }

    private int findRanking(int timeTaken, ArrayList<Integer> list) {
        Collections.sort(list);
        int lo = 0, hi = list.size() - 1;
        while(lo <= hi) {
            int mid = (lo+hi)/2;
            Integer curr = list.get(mid);
            if (curr.compareTo(Integer. valueOf(timeTaken)) >=0) {
                hi = mid-1;
            }
            else
            {
                lo = mid + 1;
            }
        }
        return lo;
    }

    private void showPlayAgainAlert(String msg)
    {
        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("Congratulations! You Win!")
                .setMessage(msg + "\nDo you want to play again?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FragmentManager fm = getSupportFragmentManager();
                        GameFragment fragment = (GameFragment) fm.findFragmentById(R.id.fragment_game);
                        fragment.reStartGame();
                        initGameAttribute();
                        startTime = 0;
                        onStop= false;
                        runTimer();
                        restartMusic();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // response to main activity
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                })
                .setIcon(R.drawable.card1);
        dlg.show();
    }

    private void showPopuptoPlayer(Integer startTime, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GameActivity.this);
        final View customLayout = getLayoutInflater().inflate(R.layout.text_alert, null);
        alertDialog.setView(customLayout);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // send data from the AlertDialog to the Activity
                EditText editText = customLayout.findViewById(R.id.UserInput);
                String username ;

                if(editText.getText() != null && !editText.getText().toString().trim().equals(""))
                {
                    username = editText.getText().toString();
                }
                else
                {
                    username = "???";
                }
                userHighScoreDetails = getSharedPreferences("userHighScoreDetails",MODE_PRIVATE);
                SharedPreferences.Editor editor = userHighScoreDetails.edit();
                editor.putString(("userName" + getplayerRankSize()),username);
                editor.putInt(("userTime" + getplayerRankSize()),startTime);
                editor.commit();
                showPlayAgainAlert(msg);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private Integer getplayerRankSize()
    {
        while(userHighScoreDetails.contains("userName" + playerRankSize))
        {
            playerRankSize += 1;
        }

        return playerRankSize;
    }

    private Bitmap[] processDownloadedImage(String[] path) {
        if (path == null) {
            return null;
        }
        Bitmap[] bitmaps = new Bitmap[6];
        for (int i = 0; i < 6; i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(path[i]);
            if (bitmap == null) {
                return null;
            }
            bitmaps[i] = bitmap;
        }
        return bitmaps;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(mp.isPlaying()){
            mp.stop();
        }
        mp.release();

        Intent intent = new Intent(this, BgmService.class);
        intent.setAction("play");
        intent.putExtra("location", "home");
        startService(intent);
    }
}