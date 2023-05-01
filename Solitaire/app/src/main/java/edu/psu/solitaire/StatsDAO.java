package edu.psu.solitaire;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StatsDAO {
    @Query("SELECT * FROM stats ORDER BY game_number DESC")
    LiveData<List<Stat>> getALL();
    @Query("SELECT * FROM stats WHERE game_number=:gameId")
    Stat getById(int gameId);
    @Query("SELECT count(*) from stats")
    int getStatsCount();
    @Insert
    void insert(Stat statistic);
    @Update
    void update(Stat statistic);
    @Delete
    void delete(Stat statistic);

    @Query("DELETE FROM stats WHERE game_number =:gameId")
    void delete(int gameId);
}
