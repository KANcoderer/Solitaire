package edu.psu.solitaire;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CardsDAO {
    @Query("SELECT * FROM cards ORDER BY card_id DESC")
    List<Card> getALL();
    @Query("SELECT * FROM cards WHERE card_order=:order ORDER BY card_id DESC")
    List<Card> getByOrder(int order);
    @Query("SELECT * FROM cards WHERE card_id=:cardId ")
    Card getById(int cardId);
    @Query("SELECT * FROM cards WHERE card_column LIKE :col Order by card_order ASC")
    List<Card> getBycol(String col);

    @Query("SELECT * FROM cards WHERE rank=:rank and suit LIKE :suit")
    Card getBycard(String suit, int rank);
    @Insert
    void insert(Card card);
    @Update
    void update(Card card);
    @Delete
    void delete(Card card);

    @Query("DELETE FROM cards WHERE card_id =:cardId")
    void delete(int cardId);
}
