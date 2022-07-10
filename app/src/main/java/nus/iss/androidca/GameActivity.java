package nus.iss.androidca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity implements GameFragment.IGameFragment{

    TextView txtScore;
    int matchCounter;
    int startTime;
    Handler handler = new Handler();
    private boolean onStop = false;
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
        // send data to our fragment
        Bitmap[] bitmaps = new Bitmap[6];
        bitmaps[0] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card1);
        bitmaps[1] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card2);
        bitmaps[2] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card3);
        bitmaps[3] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card4);
        bitmaps[4] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card5);
        bitmaps[5] = BitmapFactory.decodeResource(this.getResources(), R.drawable.card6);
        Bitmap defaultBitmaps = BitmapFactory.decodeResource(this.getResources(), R.drawable.cardback);
        FragmentManager fm = getSupportFragmentManager();
        GameFragment fragment = (GameFragment) fm.findFragmentById(R.id.fragment_game);
        fragment.setBitmaps(bitmaps, defaultBitmaps);
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
            onStop= true;
            handler.removeCallbacksAndMessages(null);
            txtScore.setText("Game Over!");
            Toast.makeText(this, "You Win!", Toast.LENGTH_LONG).show();

            AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                    .setTitle("Congratulations! You Win!")
                    .setMessage("Do you want to play again?")
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
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // response to main activity
                            finish();
                        }
                    })
                    .setIcon(R.drawable.card1);
            dlg.show();
        }
    }

    private void initGameAttribute() {
        matchCounter = 0;
        txtScore.setText(matchCounter + " of 6 matches");
    }
}