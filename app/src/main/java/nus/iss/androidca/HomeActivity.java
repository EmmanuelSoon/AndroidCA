package nus.iss.androidca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import nus.iss.androidca.service.BgmService;

public class HomeActivity extends AppCompatActivity {

    private Button enter;
    private Button viewRankingBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        enter = findViewById(R.id.enterBtn);
        viewRankingBtn = findViewById(R.id.viewRankingBtn);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                Toast.makeText(HomeActivity.this, "Enjoy!",Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        viewRankingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ViewRankingActivity.class);
                Toast.makeText(HomeActivity.this, "Rankings!",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, BgmService.class);
        intent.setAction("play");
        intent.putExtra("location", "home");
        startService(intent);

    }
}