package edu.psu.solitaire;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.preference.PreferenceManager;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements DisplayNewGameDialog.onDialogListener {
    Bundle bundle;
    List<Card> cards;
    int stock;
    TextView movesView;
    TextView stockView;
    TextView timeText;
    DoublyLinkedList deck =new DoublyLinkedList(R.id.shuffle_button,"deck");
    DoublyLinkedList discard=new DoublyLinkedList(R.id.discard_slot,"discard");
    DoublyLinkedList spades_pile=new DoublyLinkedList(R.id.spade_slot,"spades");
    DoublyLinkedList hearts_pile=new DoublyLinkedList(R.id.heart_slot,"hearts");
    DoublyLinkedList clubs_pile=new DoublyLinkedList(R.id.club_slot,"clubs");
    DoublyLinkedList diamonds_pile=new DoublyLinkedList(R.id.diamond_slot,"diamonds");
    DoublyLinkedList col1 = new DoublyLinkedList(R.id.king_slot1,"col1");
    DoublyLinkedList col2 = new DoublyLinkedList(R.id.king_slot2,"col2");
    DoublyLinkedList col3 = new DoublyLinkedList(R.id.king_slot3,"col3");
    DoublyLinkedList col4 = new DoublyLinkedList(R.id.king_slot4,"col4");
    DoublyLinkedList col5 = new DoublyLinkedList(R.id.king_slot5,"col5");
    DoublyLinkedList col6 = new DoublyLinkedList(R.id.king_slot6,"col6");
    DoublyLinkedList col7 = new DoublyLinkedList(R.id.king_slot7,"col7");

    int moves=0;
    boolean won=false;
    int min=0;
    int sec=0;
    String time=min+":"+sec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle=savedInstanceState;
        setContentView(R.layout.activity_game);
        Toolbar myToolbar =findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final String DEFAULT_COLOR="Red";
        String color =sharedPreferences.getString("background",DEFAULT_COLOR);
        ConstraintLayout li=(ConstraintLayout) findViewById(R.id.game_screen);
        switch (color){
            case "Red"-> li.setBackgroundColor(Color.parseColor("#570314"));
            case "Orange"->li.setBackgroundColor(Color.parseColor("#B13D06"));
            case "Yellow"->li.setBackgroundColor(Color.parseColor("#A87F03"));
            case "Green"->li.setBackgroundColor(Color.parseColor("#195703"));
            case "Blue"->li.setBackgroundColor(Color.parseColor("#052688"));
            case "Purple"->li.setBackgroundColor(Color.parseColor("#4B0588"));
        }
        movesView = (TextView) findViewById(R.id.moves);
        stockView=(TextView) findViewById(R.id.stock);
        timeText =(TextView) findViewById(R.id.time);


        Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int temp=msg.arg1;
                Handler handler=new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if(msg.arg1==0){
                            deal();
                        }
                        if(bundle!=null){
                            moves=bundle.getInt("moves");
                            min=bundle.getInt("min");
                            sec=bundle.getInt("sec");
                        }else if(msg.obj!=null){
                            Card stats=(Card) msg.obj;
                            moves=stats.rank;
                            min=stats.imageId;
                            sec=stats.cardImage;

                        }
                        String moveText=""+moves;
                        movesView.setText(moveText);
                        stock=deck.getSize();
                        String stockText=""+stock;
                        stockView.setText(stockText);
                        final Timer t = new Timer();
                        t.scheduleAtFixedRate(new TimerTask() {
                            public void run() {
                                runOnUiThread(() -> {
                                    if (checkWin()) {
                                        t.cancel();
                                        //https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
                                        Date c = Calendar.getInstance().getTime();
                                        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                                        String formattedDate = df.format(c);
                                        (new Thread(()->{
                                            StatDatabase.insert(new Stat(0,moves,time,formattedDate));
                                        })).start();

                                        return;
                                    }
                                    sec++;
                                    if(sec==60){
                                        min++;
                                        sec=0;
                                    }
                                    time=min+":"+sec;
                                    timeText.setText(time);

                                });
                            }
                        }, 0, 1000);
                    }

                };
                Thread thread=new Thread(()->{

                    if(temp==1){
                        create();
                        Message msg2 = handler.obtainMessage();
                        msg2.arg1=0;
                        handler.sendMessage(msg2);
                    }else{
                        recreateAll();
                        Card stats=CardDatabase.getDatabase(getApplication()).cardsDAO().getBycol("stats").get(0);
                        Message msg2 = handler.obtainMessage();
                        msg2.arg1=1;
                        msg2.obj=stats;
                        handler.sendMessage(msg2);
                    }

                });
                thread.start();
            }

        };
        Thread thread=new Thread(()->{
            Message msg = handler.obtainMessage();

            if(CardDatabase.getDatabase(getApplication()).cardsDAO().getALL().size()==0) {

                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",1, R.id.ace_club, R.drawable.ace_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",2, R.id.two_club, R.drawable.two_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",3, R.id.three_club, R.drawable.three_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",4, R.id.four_club, R.drawable.four_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",5, R.id.five_club, R.drawable.five_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",6, R.id.six_club, R.drawable.six_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",7, R.id.seven_club, R.drawable.seven_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",8, R.id.eight_club, R.drawable.eight_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",9, R.id.nine_club, R.drawable.nine_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",10, R.id.ten_club, R.drawable.ten_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",11, R.id.jack_club, R.drawable.jack_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",12, R.id.queen_club, R.drawable.queen_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "club",13, R.id.king_club, R.drawable.king_clubs, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",1, R.id.ace_diamond, R.drawable.ace_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",2, R.id.two_diamond, R.drawable.two_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",3, R.id.three_diamond, R.drawable.three_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",4, R.id.four_diamond, R.drawable.four_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",5, R.id.five_diamond, R.drawable.five_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",6, R.id.six_diamond, R.drawable.six_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",7, R.id.seven_diamond, R.drawable.seven_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",8, R.id.eight_diamond, R.drawable.eight_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",9, R.id.nine_diamond, R.drawable.nine_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",10, R.id.ten_diamond, R.drawable.ten_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",11, R.id.jack_diamond, R.drawable.jack_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",12, R.id.queen_diamond, R.drawable.queen_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "diamond",13, R.id.king_diamond, R.drawable.king_diamonds, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",1, R.id.ace_spade, R.drawable.ace_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",2, R.id.two_spade, R.drawable.two_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",3, R.id.three_spade, R.drawable.three_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",4, R.id.four_spade, R.drawable.four_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",5, R.id.five_spade, R.drawable.five_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",6, R.id.six_spade, R.drawable.six_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",7, R.id.seven_spade, R.drawable.seven_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",8, R.id.eight_spade, R.drawable.eight_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",9, R.id.nine_spade, R.drawable.nine_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",10, R.id.ten_spade, R.drawable.ten_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",11, R.id.jack_spade, R.drawable.jack_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",12, R.id.queen_spade, R.drawable.queen_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "spade",13, R.id.king_spade, R.drawable.king_spades, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",1, R.id.ace_heart, R.drawable.ace_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",2, R.id.two_heart, R.drawable.two_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",3, R.id.three_heart, R.drawable.three_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",4, R.id.four_heart, R.drawable.four_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",5, R.id.five_heart, R.drawable.five_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",6, R.id.six_heart, R.drawable.six_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",7, R.id.seven_heart, R.drawable.seven_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",8, R.id.eight_heart, R.drawable.eight_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",9, R.id.nine_heart, R.drawable.nine_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",10, R.id.ten_heart, R.drawable.ten_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",11, R.id.jack_heart, R.drawable.jack_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",12, R.id.queen_heart, R.drawable.queen_hearts, false, "deck", -1));
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0, "heart",13, R.id.king_heart, R.drawable.king_hearts, false, "deck", -1));
            }
            List<Card> temp=CardDatabase.getDatabase(getApplication()).cardsDAO().getByOrder(-1);
            if(temp.size()==0){
                msg.arg1=0;
            }else{
                msg.arg1=1;
            }
            handler.sendMessage(msg);
        });
        thread.start();




        ImageButton shuffle = (ImageButton) findViewById(R.id.shuffle_button);
        shuffle.setOnClickListener(v -> {
            while(discard.getSize()!=0){
                ImageView topCard = (ImageView) findViewById(discard.getTail().getId());

                ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
                anim.setTarget(topCard);
                anim.setDuration(100);
                anim.start();
                //From https://stackoverflow.com/questions/42235534/how-to-move-imageview-to-another-imageview-android-animation
                topCard.animate()
                        .x(shuffle.getX())
                        .y(shuffle.getY())
                        .z(shuffle.getZ() + deck.getSize() + 1)
                        .setDuration(100)
                        .start();
                topCard.postDelayed(() -> topCard.setImageResource(R.drawable.card_back), 50);
                discard.getTail().setFaceUp(false);
                Node card = discard.remove(discard.getTail());
                deck.insert(card);
            }
            int newStock=deck.getSize();
            String newStockText=""+newStock;
            stockView.setText(newStockText);

        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        (new Thread(()->{
            List<Card> stats=CardDatabase.getDatabase(getApplication()).cardsDAO().getBycol("stats");
            if(stats.size()==0){
                CardDatabase.getDatabase(getApplication()).cardsDAO().insert(new Card(0,"stats", moves, min, sec, false, "stats", 0));
            }else{
                Card stat=stats.get(0);
                stat.rank=moves;
                stat.imageId=min;
                stat.cardImage=sec;
                CardDatabase.getDatabase(getApplication()).cardsDAO().update(stat);
            }
        })).start();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("moves",moves);
        outState.putInt("min",min);
        outState.putInt("sec",sec);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_toolbar, menu);
        return true;
    }
    @Override
    public GameActivity getActivity() {
        return this;
    }
    public void destroyAll(){

        destroy(spades_pile);
        destroy(clubs_pile);
        destroy(diamonds_pile);
        destroy(hearts_pile);
        destroy(discard);
        destroy(col1);
        destroy(col2);
        destroy(col3);
        destroy(col4);
        destroy(col5);
        destroy(col6);
        destroy(col7);
        Node node=deck.getHead();
    }

    public void destroy(DoublyLinkedList column){
        Node node=column.getHead();
        while(node!=null){
            Node temp=node;
            node=node.getNext();
            column.remove(temp);
            temp.setFaceUp(false);
            deck.insert(temp);
            move(temp,false);
        }
    }
    public void create(){
        deck.insert(new Node(R.id.ace_club, "club", 1,R.drawable.ace_clubs));
        deck.insert(new Node(R.id.two_club, "club", 2,R.drawable.two_clubs));
        deck.insert(new Node(R.id.three_club, "club", 3,R.drawable.three_clubs));
        deck.insert(new Node(R.id.four_club, "club", 4,R.drawable.four_clubs));
        deck.insert(new Node(R.id.five_club, "club", 5,R.drawable.five_clubs));
        deck.insert(new Node(R.id.six_club, "club", 6,R.drawable.six_clubs));
        deck.insert(new Node(R.id.seven_club, "club", 7,R.drawable.seven_clubs));
        deck.insert(new Node(R.id.eight_club, "club", 8,R.drawable.eight_clubs));
        deck.insert(new Node(R.id.nine_club, "club", 9,R.drawable.nine_clubs));
        deck.insert(new Node(R.id.ten_club, "club", 10,R.drawable.ten_clubs));
        deck.insert(new Node(R.id.jack_club, "club", 11,R.drawable.jack_clubs));
        deck.insert(new Node(R.id.queen_club, "club", 12,R.drawable.queen_clubs));
        deck.insert(new Node(R.id.king_club, "club", 13,R.drawable.king_clubs));
        deck.insert(new Node(R.id.ace_diamond, "diamond", 1,R.drawable.ace_diamonds));
        deck.insert(new Node(R.id.two_diamond, "diamond", 2,R.drawable.two_diamonds));
        deck.insert(new Node(R.id.three_diamond, "diamond", 3,R.drawable.three_diamonds));
        deck.insert(new Node(R.id.four_diamond, "diamond", 4,R.drawable.four_diamonds));
        deck.insert(new Node(R.id.five_diamond, "diamond", 5,R.drawable.five_diamonds));
        deck.insert(new Node(R.id.six_diamond, "diamond", 6,R.drawable.six_diamonds));
        deck.insert(new Node(R.id.seven_diamond, "diamond", 7,R.drawable.seven_diamonds));
        deck.insert(new Node(R.id.eight_diamond, "diamond", 8,R.drawable.eight_diamonds));
        deck.insert(new Node(R.id.nine_diamond, "diamond", 9,R.drawable.nine_diamonds));
        deck.insert(new Node(R.id.ten_diamond, "diamond", 10,R.drawable.ten_diamonds));
        deck.insert(new Node(R.id.jack_diamond, "diamond", 11,R.drawable.jack_diamonds));
        deck.insert(new Node(R.id.queen_diamond, "diamond", 12,R.drawable.queen_diamonds));
        deck.insert(new Node(R.id.king_diamond, "diamond", 13,R.drawable.king_diamonds));
        deck.insert(new Node(R.id.ace_spade, "spade", 1,R.drawable.ace_spades));
        deck.insert(new Node(R.id.two_spade, "spade", 2,R.drawable.two_spades));
        deck.insert(new Node(R.id.three_spade, "spade", 3,R.drawable.three_spades));
        deck.insert(new Node(R.id.four_spade, "spade", 4,R.drawable.four_spades));
        deck.insert(new Node(R.id.five_spade, "spade", 5,R.drawable.five_spades));
        deck.insert(new Node(R.id.six_spade, "spade", 6,R.drawable.six_spades));
        deck.insert(new Node(R.id.seven_spade, "spade", 7,R.drawable.seven_spades));
        deck.insert(new Node(R.id.eight_spade, "spade", 8,R.drawable.eight_spades));
        deck.insert(new Node(R.id.nine_spade, "spade", 9,R.drawable.nine_spades));
        deck.insert(new Node(R.id.ten_spade, "spade", 10,R.drawable.ten_spades));
        deck.insert(new Node(R.id.jack_spade, "spade", 11,R.drawable.jack_spades));
        deck.insert(new Node(R.id.queen_spade, "spade", 12,R.drawable.queen_spades));
        deck.insert(new Node(R.id.king_spade, "spade", 13,R.drawable.king_spades));
        deck.insert(new Node(R.id.ace_heart, "heart", 1,R.drawable.ace_hearts));
        deck.insert(new Node(R.id.two_heart, "heart", 2,R.drawable.two_hearts));
        deck.insert(new Node(R.id.three_heart, "heart", 3,R.drawable.three_hearts));
        deck.insert(new Node(R.id.four_heart, "heart", 4,R.drawable.four_hearts));
        deck.insert(new Node(R.id.five_heart, "heart", 5,R.drawable.five_hearts));
        deck.insert(new Node(R.id.six_heart, "heart", 6,R.drawable.six_hearts));
        deck.insert(new Node(R.id.seven_heart, "heart", 7,R.drawable.seven_hearts));
        deck.insert(new Node(R.id.eight_heart, "heart", 8,R.drawable.eight_hearts));
        deck.insert(new Node(R.id.nine_heart, "heart", 9,R.drawable.nine_hearts));
        deck.insert(new Node(R.id.ten_heart, "heart", 10,R.drawable.ten_hearts));
        deck.insert(new Node(R.id.jack_heart, "heart", 11,R.drawable.jack_hearts));
        deck.insert(new Node(R.id.queen_heart, "heart", 12,R.drawable.queen_hearts));
        deck.insert(new Node(R.id.king_heart, "heart", 13,R.drawable.king_hearts));
    }
    public void deal(){
        for(int j =0; j<7; j++){
            for(int k=0;k<=j;k++){
                Node temp = deck.remove(deck.getHead());
                DoublyLinkedList col;
                if(k==j){
                    temp.setFaceUp(true);

                }
                switch (j) {
                    case 0 -> {
                        col = col1;
                        col.insert(temp);
                    }
                    case 1 -> {
                        col = col2;
                        col.insert(temp);
                    }
                    case 2 -> {
                        col = col3;
                        col.insert(temp);
                    }
                    case 3 -> {
                        col = col4;
                        col.insert(temp);
                    }
                    case 4 -> {
                        col = col5;
                        col.insert(temp);
                    }
                    case 5 -> {
                        col = col6;
                        col.insert(temp);
                    }
                    case 6 -> {
                        col = col7;
                        col.insert(temp);
                    }
                }

                move(temp,true);
            }
        }
        Node card=deck.getHead();

        ImageButton shuffle=(ImageButton) findViewById(R.id.shuffle_button);
        for(int i=0;i<24;i++){
            ImageView cardImage=(ImageView) findViewById(card.getId());
            cardImage.setZ(shuffle.getZ()+i);
            Node finalCard = card;
            int finalI = i;
            CardDatabase.getCard(card.getSuit(), card.getRank(), newCard -> {
                newCard.col = finalCard.getCol().getName();
                newCard.order = finalI;
                newCard.faceUp= finalCard.isFaceUp();
                CardDatabase.update(newCard);
            });
            card=card.getNext();

        }
    }

    public void move(Node card, boolean inPlayArea){

        ImageView mover = (ImageView) findViewById(card.getId());
        ImageView dest = (ImageView) findViewById(card.getCol().getId());

        int y=(card.getCol().getSize()-1)*50;

        int z=card.getCol().getSize()+1;
        mover.post(() -> {
            if(inPlayArea){
                mover.animate()
                        .x(dest.getX())
                        .y(dest.getY()+y)
                        .z(dest.getZ()+z)
                        .setDuration(500)
                        .start();
            }else{
                mover.animate()
                        .x(dest.getX())
                        .y(dest.getY())
                        .z(dest.getZ()+z)
                        .setDuration(500)
                        .start();
            }

            if(card.isFaceUp()){

                mover.postDelayed(() -> mover.setImageResource(card.getImage()),250);
            }else{
                mover.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mover.setImageResource(R.drawable.card_back);

                    }
                },250);
            }
        });

    }

    public void flip(Node card){

        ImageView flipper = (ImageView) findViewById(card.getId());
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
        anim.setTarget(flipper);
        anim.setDuration(100);
        anim.start();
        flipper.postDelayed(() -> {
            flipper.setImageResource(card.getImage());

            card.setFaceUp(true);
            CardDatabase.getCard(card.getSuit(), card.getRank(), newCard -> {
                newCard.col = card.getCol().getName();
                newCard.order = card.getOrder();
                newCard.faceUp= card.isFaceUp();
                CardDatabase.update(newCard);
            });

        }, 50);
        card.setId(flipper.getId());
    }




    public void startHome(MenuItem item) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void startSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void startNewGame(MenuItem item) {
        if(won){
            reset();
        }else{
            DisplayNewGameDialog newGameDialog = new DisplayNewGameDialog();
            newGameDialog.show(getSupportFragmentManager(), "newGameDialog");
        }
    }
    public void recreateAll(){
        recreate("deck",deck);
        recreate("discard",discard);
        recreate("spades",spades_pile);
        recreate("clubs",clubs_pile);
        recreate("diamonds",diamonds_pile);
        recreate("hearts",hearts_pile);
        recreate("col1",col1);
        recreate("col2",col2);
        recreate("col3",col3);
        recreate("col4",col4);
        recreate("col5",col5);
        recreate("col6",col6);
        recreate("col7",col7);
    }
    public void recreate(String col, DoublyLinkedList DLcol){
        List<Card> cardList=CardDatabase.getDatabase(getApplication()).cardsDAO().getBycol(col);

        for(int i =0; i<cardList.size();i++){

            Node temp=new Node(cardList.get(i).imageId,cardList.get(i).suit,cardList.get(i).rank,cardList.get(i).cardImage,cardList.get(i).faceUp);
            DLcol.insert(temp);
            move(temp, col.equals("col1") || col.equals("col2") || col.equals("col3") || col.equals("col4") || col.equals("col5") || col.equals("col6") || col.equals("col7"));
        }

    }



    public void reset(){
        (new Thread(()->{
            List<Card> stats=CardDatabase.getDatabase(getApplication()).cardsDAO().getBycol("stats");
            if(stats.size()!=0){

                CardDatabase.getDatabase(getApplication()).cardsDAO().delete(stats.get(0));
            }
        })).start();
        destroyAll();
        deal();

        deck.setSize(24);
        String stockText=""+deck.getSize();
        stockView.setText(stockText);
        moves=0;
        String moveText=""+0;
        movesView.setText(moveText);
        min=0;
        sec=0;
        time=min+":"+sec;
        timeText.setText(time);

    }

    Node card;
    public boolean isRed(Node card){
        return(card.getSuit().equals("diamond") || card.getSuit().equals("heart"));
    }
    public Card getCard(String suit, int rank){
        Card temp=cards.get(0);
        for(int i =0; i<52;i++) {
            temp=cards.get(i);
            if (temp.suit.equals(suit) && temp.rank == rank) {
                break;
            }
        }
        return temp;
    }
    public boolean checkMove(Node card,Node tail){
        if((isRed(card)&&!isRed(tail))||(!isRed(card)&&isRed(tail))){
            return (card.getRank()== tail.getRank()-1);
        }
        return false;
    }
    public boolean checkWin(){
        return((spades_pile.getSize()==13)&&(clubs_pile.getSize()==13)&&(hearts_pile.getSize()==13)&&(diamonds_pile.getSize()==13));
    }

    public void select(View view) {

        if(!won) {
            int id = view.getId();
            float x = view.getX();
            float y = view.getY();
            ImageView imagevVew = (ImageView) view;
            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    card = (Node) msg.obj;

                    if (card.isFaceUp()) {
                        boolean moved = false;
                        DoublyLinkedList pile = null;
                        Node lastCard = null;
                        switch (card.getSuit()) {
                            case "spade" -> {
                                pile = spades_pile;
                            }
                            case "club" -> {
                                pile = clubs_pile;
                            }
                            case "heart" -> {
                                pile = hearts_pile;
                            }
                            case "diamond" -> {
                                pile = diamonds_pile;
                            }
                        }
                        assert pile != null;
                        if (pile.getSize() == card.getRank() - 1 && card.getNext() == null) {

                            if (card.getPrev() != null && !card.getPrev().isFaceUp()) {
                                lastCard = card.getPrev();
                            }
                            DoublyLinkedList prevLoc = card.getCol();
                            prevLoc.remove(card);
                            pile.insert(card);
                            move(card, false);
                            if (checkWin()) {
                                won = true;
                                Toast.makeText(GameActivity.this, R.string.win_statement, Toast.LENGTH_LONG).show();
                                (new Thread(() -> {
                                    List<Card> stats=CardDatabase.getDatabase(getApplication()).cardsDAO().getBycol("stats");
                                    if(stats.size()!=0){
                                        CardDatabase.getDatabase(getApplication()).cardsDAO().delete(stats.get(0));
                                    }
                                    List<Card> temp=CardDatabase.getDatabase(getApplication()).cardsDAO().getALL();
                                    for(int i =0;i<temp.size();i++){
                                        Card card=temp.get(i);
                                        card.col="deck";
                                        card.order=-1;
                                        card.faceUp=false;
                                        CardDatabase.getDatabase(getApplication()).cardsDAO().update(card);
                                    }

                                })).start();
                            }
                            moved = true;
                            moves++;

                            String moveText = "" + moves;
                            movesView.setText(moveText);
                            if (lastCard != null) {
                                flip(lastCard);
                            }
                        }
                        if (!moved) {

                            for (int i = 0; i < 7; i++) {
                                switch (i) {
                                    case 0 -> pile = col1;
                                    case 1 -> pile = col2;
                                    case 2 -> pile = col3;
                                    case 3 -> pile = col4;
                                    case 4 -> pile = col5;
                                    case 5 -> pile = col6;
                                    case 6 -> pile = col7;
                                }
                                if (pile.getSize() > 0) {
                                    Node tail = pile.getTail();
                                    if (checkMove(card, tail)) {

                                        if (card.getPrev() != null && !card.getPrev().isFaceUp()) {
                                            lastCard = card.getPrev();
                                        }
                                        moves++;
                                        String moveText = "" + moves;
                                        movesView.setText(moveText);
                                        Node temp = card;
                                        while (temp != null) {
                                            temp = temp.getNext();
                                            DoublyLinkedList prevLoc = card.getCol();
                                            prevLoc.remove(card);
                                            pile.insert(card);
                                            move(card, true);
                                            card = temp;
                                        }
                                        if (lastCard != null) {
                                            flip(lastCard);
                                        }
                                        break;
                                    }
                                } else if (card.getRank() == 13) {
                                    if (card.getPrev() != null) {
                                        lastCard = card.getPrev();
                                    }
                                    Node temp = card;
                                    moves++;
                                    String moveText = "" + moves;
                                    movesView.setText(moveText);
                                    while (temp != null) {
                                        temp = temp.getNext();
                                        DoublyLinkedList prevLoc = card.getCol();
                                        prevLoc.remove(card);
                                        pile.insert(card);
                                        move(card, true);
                                        card = temp;
                                    }
                                    if (lastCard != null) {
                                        flip(lastCard);
                                    }
                                    break;
                                }
                            }
                        }

                    }
                }
            };
            Thread thread = new Thread(() -> {


                Node found = find(id, x, y);
                Message msg = handler.obtainMessage();
                msg.obj = found;
                handler.sendMessage(msg);
            });
            thread.start();
            if (deck.getSize() != 0 && imagevVew.getX() == findViewById(R.id.shuffle_button).getX() && imagevVew.getY() == findViewById(R.id.shuffle_button).getY()) {

                int image = deck.getTail().getImage();
                ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping);
                anim.setTarget(imagevVew);
                anim.setDuration(500);
                anim.start();
                ImageView dest = findViewById(R.id.discard_slot);
                //From https://stackoverflow.com/questions/42235534/how-to-move-imageview-to-another-imageview-android-animation
                imagevVew.animate()
                        .x(dest.getX())
                        .y(dest.getY())
                        .z(dest.getZ() + discard.getSize() + 1)
                        .setDuration(500)
                        .start();
                imagevVew.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imagevVew.setImageResource(image);

                    }
                }, 250);
                Node card = deck.remove(deck.getTail());
                card.setFaceUp(true);
                discard.insert(card);

                card.setId(imagevVew.getId());
                moves++;
                String moveText = "" + moves;
                movesView.setText(moveText);
                int stock = deck.getSize();
                String stockText = "" + stock;
                stockView.setText(stockText);

            }

        }
    }

    public Node find(int id, float x, float y){
        if(x==findViewById(R.id.king_slot1).getX()&&y >= findViewById(R.id.king_slot1).getY()){
            Node temp=col1.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot2).getX()&&y >= findViewById(R.id.king_slot2).getY()){
            Node temp=col2.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot3).getX()&&y >= findViewById(R.id.king_slot3).getY()){
            Node temp=col3.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot4).getX()&&y >= findViewById(R.id.king_slot4).getY()){
            Node temp=col4.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot5).getX()&&y >= findViewById(R.id.king_slot5).getY()){
            Node temp=col5.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot6).getX()&&y >= findViewById(R.id.king_slot6).getY()){
            Node temp=col6.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.king_slot7).getX()&&y >= findViewById(R.id.king_slot7).getY()){
            Node temp=col7.getHead();
            while(temp!=null){
                if(temp.getId()==id){
                    return temp;
                }
                temp=temp.getNext();
            }
        }else if(x==findViewById(R.id.discard_slot).getX()&&y == findViewById(R.id.discard_slot).getY()){

            Node temp=discard.getHead();
            while(temp!=null) {
                if(temp.getId()==id){
                    return temp;
                }
                temp = temp.getNext();
            }

        }else if(x==findViewById(R.id.diamond_slot).getX()&&y == findViewById(R.id.diamond_slot).getY()){

            Node temp=diamonds_pile.getHead();
            while(temp!=null) {
                if(temp.getId()==id){
                    return temp;
                }
                temp = temp.getNext();
            }

        }else if(x==findViewById(R.id.club_slot).getX()&&y == findViewById(R.id.club_slot).getY()){

            Node temp=clubs_pile.getHead();
            while(temp!=null) {
                if(temp.getId()==id){
                    return temp;
                }
                temp = temp.getNext();
            }

        }else if(x==findViewById(R.id.heart_slot).getX()&&y == findViewById(R.id.heart_slot).getY()){

            Node temp=hearts_pile.getHead();
            while(temp!=null) {
                if(temp.getId()==id){
                    return temp;
                }
                temp = temp.getNext();
            }

        }else if(x==findViewById(R.id.spade_slot).getX()&&y == findViewById(R.id.spade_slot).getY()){

            Node temp=spades_pile.getHead();
            while(temp!=null) {
                if(temp.getId()==id){
                    return temp;
                }
                temp = temp.getNext();
            }
        }
        Node joker=new Node(0,"None", 0,0);
        return joker;
    }
}
