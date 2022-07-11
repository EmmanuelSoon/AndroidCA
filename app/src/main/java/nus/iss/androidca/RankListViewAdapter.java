package nus.iss.androidca;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import nus.iss.androidca.wrapper.RankingList;

public class RankListViewAdapter extends ArrayAdapter<Object> {

    private final Context context;
    protected List<RankingList> rankinglist;

    public RankListViewAdapter(@NonNull Context context, List<RankingList> rankinglist)
    {
        super(context, R.layout.ranklinglist_row);
        this.context = context;
        this.rankinglist = rankinglist;
        addAll(new Object[rankinglist.size()]);
    }

    @androidx.annotation.NonNull
    public View getView(int rowPos, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ranklinglist_row, parent, false);
        }

        TextView userName = convertView.findViewById(R.id.rankingListuserName);
        userName.setText(rankinglist.get(rowPos).getUserName());

        TextView score = convertView.findViewById(R.id.rankingListScore);
        score.setText(rankinglist.get(rowPos).getScore().toString());

        return convertView;
    }


}
