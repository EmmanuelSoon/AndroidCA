package nus.iss.androidca;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nus.iss.androidca.wrapper.RankingList;

public class ViewRankingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<RankingList> rankinglist;
    ListView rankListView;
    SharedPreferences userHighScoreDetails;
    Integer playerRankSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ranking);

        rankListView = findViewById(R.id.rankListView);
        userHighScoreDetails = getSharedPreferences("userHighScoreDetails", MODE_PRIVATE);
        rankinglist = new ArrayList<>();
        Integer preferenceSize = getplayerRankSize();

        for(int i = 0; i < preferenceSize; i++)
        {
            String username = userHighScoreDetails.getString(("userName" + i),null);
            Integer time = userHighScoreDetails.getInt(("userTime" + i),0);
            RankingList curr = new RankingList(username,time);
            rankinglist.add(curr);
        }

        Collections.sort(rankinglist, (rank1, rank2) -> {
            if(rank1.getScore() < rank2.getScore())
                return -1;
            else if (rank1.getScore() > rank2.getScore())
                return 1;
            else
                return 0;
        });

        if(rankinglist.size() > 5){
            rankinglist = rankinglist.stream().limit(5).collect(Collectors.toList());
        }

        RankListViewAdapter adapter = new RankListViewAdapter(this, rankinglist);
        rankListView = findViewById(R.id.rankListView);

        if(rankListView != null)
        {
            rankListView.setAdapter(adapter);
            rankListView.setOnItemClickListener(this);
        }

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private Integer getplayerRankSize()
    {
        while(userHighScoreDetails.contains("userName" + playerRankSize))
        {
            playerRankSize += 1;
        }

        return playerRankSize;
    }
}