package edu.psu.solitaire;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final String DEFAULT_COLOR="Red";
        String color =sharedPreferences.getString("background",DEFAULT_COLOR);
        ConstraintLayout li=(ConstraintLayout) findViewById(R.id.home_screen);
        switch (color){
            case "Red"-> li.setBackgroundColor(Color.parseColor("#570314"));
            case "Orange"->li.setBackgroundColor(Color.parseColor("#B13D06"));
            case "Yellow"->li.setBackgroundColor(Color.parseColor("#A87F03"));
            case "Green"->li.setBackgroundColor(Color.parseColor("#195703"));
            case "Blue"->li.setBackgroundColor(Color.parseColor("#052688"));
            case "Purple"->li.setBackgroundColor(Color.parseColor("#4B0588"));
        }
        StatDatabase.getDatabase(getApplication()).statsDAO().getALL();

    }
    public void startContinueGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
    public void startNewGame(View view) {
        Handler handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                startActivity(intent);
            }

        };
        Thread thread=new Thread(()->{
            List<Card> temp=CardDatabase.getDatabase(getApplication()).cardsDAO().getALL();
            if(CardDatabase.getDatabase(getApplication()).cardsDAO().getALL().size()!=0) {
                for(int i =0;i<temp.size();i++){
                    Card card=temp.get(i);
                    if(card.col.equals("stats")){
                        continue;
                    }
                    card.col="deck";
                    card.order=-1;
                    card.faceUp=false;
                    CardDatabase.getDatabase(getApplication()).cardsDAO().update(card);
                }
            }
            Message msg = handler.obtainMessage();
            handler.sendMessage(msg);

        });
        thread.start();

    }

    public void startStats(View view) {
        Intent intent = new Intent(this, StatsActivity.class);
        startActivity(intent);
    }
    public void startSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}