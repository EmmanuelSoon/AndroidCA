package nus.iss.androidca;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameFragment extends Fragment implements View.OnClickListener {

    private Bitmap[] Bitmaps = null;
    private Bitmap defaultBitmap = null;
    private int size = 0;
    private int matchedPairs = 0;
    private int firstClicked = -1;
    private List<Integer> board = new ArrayList<>();
    private List<ImageButton> btns = new ArrayList<>();
    private List<ImageButton> matchedPairtodisable = new ArrayList<>();

    private boolean isGameOver() {
        return size == matchedPairs;
    }
    private void initBoard() {
        for (int i = 0; i < size; i++) {
            board.add(i);
            board.add(i);
        }
        Collections.shuffle(board);
    }


    public GameFragment() { }
    public void setBitmaps(Bitmap[] Bitmaps, Bitmap defaultBitmap) {
        this.size = Bitmaps.length;
        this.Bitmaps = Bitmaps;
        this.defaultBitmap = defaultBitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            initBtn(view);
            initBoard();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        for (int i = 0; i < btns.size(); i++) {
            ImageButton btn = btns.get(i);
            if (btn.getId() == id) {
                // i is the index of the button that we set start from 0
                handleClick(i);
                break;
            }
        }
    }
    private void handleClick(int btnIndex){
        if (firstClicked < 0) {
            // if it is first click
            firstClicked = btnIndex;
            ImageButton btn = btns.get(btnIndex);
            btn.setImageBitmap(Bitmaps[board.get(btnIndex)]);
            //Temporarily disable the button that we just clicked
            btn.setEnabled(false);
        }
        else {
            //check whether they are matching
            if (board.get(firstClicked).equals(board.get(btnIndex))) {
                ImageButton btn = btns.get(btnIndex);

                //Add the matched pairs to the list to keep it disabled.
                matchedPairtodisable.add(btns.get(firstClicked));
                matchedPairtodisable.add(btn);
                btn.setImageBitmap(Bitmaps[board.get(btnIndex)]);
                enableorDisableBtns("Disable");
                btn.setEnabled(false);

                matchedPairs++;
                iGameFragment.itemClicked("match");
                if (isGameOver()) {
                    //send congrats Msg
                    iGameFragment.itemClicked("over");
                }
                //Enable the button selectively.
                enableorDisableBtns("Enable");
                clearFirstClick();

            }
            else {
                //if 2 images are not matching
                ImageButton btn = btns.get(btnIndex);
                btn.setImageBitmap(Bitmaps[board.get(btnIndex)]);
                enableorDisableBtns("Disable");
                btn.setEnabled(false);


                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        //Do nothing
                    }
                    @Override
                    public void onFinish() {
                        reverseBackImage(firstClicked);
                        reverseBackImage(btnIndex);
                        enableorDisableBtns("Enable");
                        clearFirstClick();
                    }
                }.start();
            }
        }
    }

    private void reverseBackImage(int index) {
        ImageButton btn = btns.get(index);
        btn.setImageBitmap(defaultBitmap);
        btn.setEnabled(true);
    }

    public interface IGameFragment{
        void itemClicked(String content);
    }
    private IGameFragment iGameFragment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iGameFragment = (IGameFragment) context;
    }

    private void initBtn(View v) {
        ImageButton imageButton0 = v.findViewById(R.id.card0);
        if (imageButton0 != null) {
            imageButton0.setOnClickListener(this);
            btns.add(imageButton0);
        }
        ImageButton imageButton1 = v.findViewById(R.id.card1);
        if (imageButton1 != null) {
            imageButton1.setOnClickListener(this);
            btns.add(imageButton1);
        }
        ImageButton imageButton2 = v.findViewById(R.id.card2);
        if (imageButton2 != null) {
            imageButton2.setOnClickListener(this);
            btns.add(imageButton2);
        }
        ImageButton imageButton3 = v.findViewById(R.id.card3);
        if (imageButton3 != null) {
            imageButton3.setOnClickListener(this);
            btns.add(imageButton3);
        }
        ImageButton imageButton4 = v.findViewById(R.id.card4);
        if (imageButton4 != null) {
            imageButton4.setOnClickListener(this);
            btns.add(imageButton4);
        }
        ImageButton imageButton5 = v.findViewById(R.id.card5);
        if (imageButton5 != null) {
            imageButton5.setOnClickListener(this);
            btns.add(imageButton5);
        }
        ImageButton imageButton6 = v.findViewById(R.id.card6);
        if (imageButton6 != null) {
            imageButton6.setOnClickListener(this);
            btns.add(imageButton6);
        }
        ImageButton imageButton7 = v.findViewById(R.id.card7);
        if (imageButton7 != null) {
            imageButton7.setOnClickListener(this);
            btns.add(imageButton7);
        }
        ImageButton imageButton8 = v.findViewById(R.id.card8);
        if (imageButton8 != null) {
            imageButton8.setOnClickListener(this);
            btns.add(imageButton8);
        }
        ImageButton imageButton9 = v.findViewById(R.id.card9);
        if (imageButton9 != null) {
            imageButton9.setOnClickListener(this);
            btns.add(imageButton9);
        }
        ImageButton imageButton10 = v.findViewById(R.id.card10);
        if (imageButton10 != null) {
            imageButton10.setOnClickListener(this);
            btns.add(imageButton10);
        }
        ImageButton imageButton11 = v.findViewById(R.id.card11);
        if (imageButton11 != null) {
            imageButton11.setOnClickListener(this);
            btns.add(imageButton11);
        }
    }

    private void enableorDisableBtns(String flag)
    {
        if(flag.equals("Disable")) {
            for (ImageButton button : btns) {
                button.setEnabled(false);
            }
        }

        if(flag.equals("Enable")) {
            for (ImageButton button : btns) {
                if(!matchedPairtodisable.contains(button))
                button.setEnabled(true);
            }
        }
    }

    public void clearFirstClick() {
        this.firstClicked = -1;
    }
}
