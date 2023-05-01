package edu.psu.solitaire;


import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {Card.class},version = 1,exportSchema = false)
public abstract class CardDatabase extends RoomDatabase {
    public interface CardListener {
        void onCardReturn(Card card);
    }

    public abstract CardsDAO cardsDAO();

    private static CardDatabase INSTANCE;

    public static CardDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CardDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CardDatabase.class, "CardDatabase").addCallback(createCardDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback createCardDatabaseCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);
        }
    };

    public static void insert(Card card){
        (new Thread(()->{
            INSTANCE.cardsDAO().insert(card);
            System.out.println("card "+card.suit+ " "+ card.rank);
        })).start();
    }

    public static void getCard(String suit, int rank, CardListener listener) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.obj==null){
                    System.out.println("here");
                }
                Card card=(Card) msg.obj;
                listener.onCardReturn(card);
            }
        };
        (new Thread(() -> {
            Message msg = handler.obtainMessage();
            msg.obj = INSTANCE.cardsDAO().getBycard(suit, rank);
            handler.sendMessage(msg);
        })).start();
    }
    public static void delete(int cardId){
        (new Thread(()-> INSTANCE.cardsDAO().delete(cardId))).start();
    }
    public static void update(Card card){
        (new Thread(()-> INSTANCE.cardsDAO().update(card))).start();
    }
}

