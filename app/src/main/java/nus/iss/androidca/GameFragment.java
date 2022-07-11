package nus.iss.androidca;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

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
    private List<EasyFlipView> matchedPairtodisable = new ArrayList<>();
    private List<EasyFlipView> cards = new ArrayList<>();

    private SoundPool sp;
    private int[] soundIds;

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

        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        sp = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(attrs)
                .build();

        soundIds = new int[10];
        soundIds[0] = sp.load(this.getContext(), R.raw.card_flip, 2);
        soundIds[1] = sp.load(this.getContext(), R.raw.correct, 1);
        soundIds[2] = sp.load(this.getContext(), R.raw.wrong, 1);
        soundIds[3] = sp.load(this.getContext(), R.raw.winner, 1);

        View view = getView();
        if (view != null) {
            initBoard();
            initBtn(view);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onClick(View view) {

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


    private void handleFlip(int cardIndex){
        EasyFlipView card = cards.get(cardIndex);
        sp.play(soundIds[0], 1, 1, 3, 0, 1.0F);


        if (firstClicked < 0){
            firstClicked = cardIndex;
            card.setFlipEnabled(false);
        }
        else {
            //Matched
            if(board.get(firstClicked).equals(board.get(cardIndex))){
                sp.play(soundIds[1], 1, 1, 2, 0, 1.0F);


                card.setFlipEnabled(false);
                matchedPairtodisable.add(cards.get(firstClicked));
                matchedPairtodisable.add(card);
                enableOrDisableCards("Disable");

                matchedPairs++;
                iGameFragment.itemClicked("match");
                if (isGameOver()) {
                    //send congrats Msg
                    sp.play(soundIds[3], 1, 1, 1, 0, 1.0F);
                    iGameFragment.itemClicked("over");
                }
                //Enable the button selectively.
                enableOrDisableCards("Enable");
                clearFirstClick();
            }
            else {
                //MisMatched
                enableOrDisableCards("Disable");
                sp.play(soundIds[2], 1, 1, 1, 0, 1.0F);

                new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        //Do nothing
                    }
                    @Override
                    public void onFinish() {
                        EasyFlipView otherCard = cards.get(firstClicked);

                        card.setFlipEnabled(true);
                        otherCard.setFlipEnabled(true);
                        flipCardsBack(card, otherCard);
                        clearFirstClick();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                card.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
                                otherCard.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
                            }
                        }, 500);
                        enableOrDisableCards("Enable");
                    }
                }.start();
            }
        }
    }

    private void flipCardsBack(EasyFlipView card, EasyFlipView otherCard){
        card.setOnFlipListener(null);
        otherCard.setOnFlipListener(null);
        otherCard.flipTheView();
        card.flipTheView();
    }

    private  void enableOrDisableCards(String flag){
        if(flag.equals("Disable")) {
            for (EasyFlipView card : cards) {
                card.setFlipEnabled(false);
            }
        }

        if(flag.equals("Enable")) {
            for (EasyFlipView card : cards) {
                if(!matchedPairtodisable.contains(card))
                    card.setFlipEnabled(true);
            }
        }
    }

    private void onFlip(EasyFlipView easyFlipView){
        for (int i = 0; i < cards.size(); i++){
            EasyFlipView currCard = cards.get(i);
            if(currCard.getId() == easyFlipView.getId()){
                handleFlip(i);
                break;
            }
        }
    }

    private void initBtn(View view) {
        EasyFlipView card1 = view.findViewById(R.id.flipCard0);
        EasyFlipView card2 = view.findViewById(R.id.flipCard1);
        EasyFlipView card3 = view.findViewById(R.id.flipCard2);
        EasyFlipView card4 = view.findViewById(R.id.flipCard3);
        EasyFlipView card5 = view.findViewById(R.id.flipCard4);
        EasyFlipView card6 = view.findViewById(R.id.flipCard5);
        EasyFlipView card7 = view.findViewById(R.id.flipCard6);
        EasyFlipView card8 = view.findViewById(R.id.flipCard7);
        EasyFlipView card9 = view.findViewById(R.id.flipCard8);
        EasyFlipView card10 = view.findViewById(R.id.flipCard9);
        EasyFlipView card11 = view.findViewById(R.id.flipCard10);
        EasyFlipView card12 = view.findViewById(R.id.flipCard11);

        card1.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card2.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card3.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card4.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card5.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card6.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card7.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card8.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card9.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card10.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card11.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));
        card12.setOnFlipListener((easyFlipView, newCurrentSide) -> onFlip(easyFlipView));

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);
        cards.add(card4);
        cards.add(card5);
        cards.add(card6);
        cards.add(card7);
        cards.add(card8);
        cards.add(card9);
        cards.add(card10);
        cards.add(card11);
        cards.add(card12);

        for (int i = 0; i < cards.size(); i++){
            ImageView front = cards.get(i).findViewById(R.id.front);
            front.setImageBitmap(Bitmaps[board.get(i)]);
        }

    }



    public void clearFirstClick() {
        this.firstClicked = -1;
    }

    public void reStartGame() {
        matchedPairs = 0;
        firstClicked = -1;
        Collections.shuffle(board);
        matchedPairtodisable.clear();
        enableOrDisableCards("Enable");
        flipAllCardsBack();
    }

    private void flipAllCardsBack(){
        for (EasyFlipView card: cards){
            card.setOnFlipListener(null);
        }

        for (EasyFlipView card: cards){
            card.flipTheView();
        }
        cards.clear();
        View view = getView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initBtn(view);
            }
        }, 500);
    }


}
