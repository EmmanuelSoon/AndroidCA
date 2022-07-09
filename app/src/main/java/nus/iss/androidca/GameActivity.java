package nus.iss.androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    TextView txtScore;
    int matchCounter;
    int mSeconds;
    int mMinutes;
    int mHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        txtScore = findViewById(R.id.matchCount);
        txtScore.setText(String.valueOf(matchCounter));

        matchCounter = 0;

        txtScore.setText(matchCounter + " of 6 matches");

        runTimer();
    }

    private void runTimer(){

    }
}