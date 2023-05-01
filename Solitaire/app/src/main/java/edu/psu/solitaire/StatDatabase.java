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


@Database(entities = {Stat.class},version = 1,exportSchema = false)
public abstract class StatDatabase extends RoomDatabase {
    public interface StatListener {
        void onStatReturn(Stat stat);
    }

    public abstract StatsDAO statsDAO();

    private static StatDatabase INSTANCE;

    public static StatDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StatDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    StatDatabase.class, "StatDatabase").addCallback(createStatDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback createStatDatabaseCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db){
            super.onCreate(db);

        }
    };
    public static void insert(Stat stat){
        (new Thread(()->INSTANCE.statsDAO().insert(stat))).start();
    }

    public static void getStat(int id, StatListener listener) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                listener.onStatReturn((Stat) msg.obj);
            }
        };
        (new Thread(() -> {
            Message msg = handler.obtainMessage();
            msg.obj = INSTANCE.statsDAO().getById(id);
            handler.sendMessage(msg);
        })).start();
    }
    public static void delete(int statId){
        (new Thread(()-> INSTANCE.statsDAO().delete(statId))).start();
    }
    public static void update(Stat stat){
        (new Thread(()-> INSTANCE.statsDAO().update(stat))).start();
    }
}
