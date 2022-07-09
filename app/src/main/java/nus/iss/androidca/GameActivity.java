package nus.iss.androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    TextView txtScore;
    int matchCounter;
    int startTime;

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
    }


    private void runTimer() {
        TextView txtTime = findViewById(R.id.timer);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                startTime = startTime + 1;
                int seconds = startTime;
                int minutes = seconds / 60;
                int hours = minutes / 60;
                seconds = seconds % 60;
                minutes = minutes % 60;
                txtTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

                handler.postDelayed(this, 1000);
            }
        });
    }
}