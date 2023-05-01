package edu.psu.solitaire;

import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
@Entity(tableName="cards")
public class Card {
    public Card(int id, String suit, int rank, int imageId, int cardImage, boolean faceUp, String col, int order){
        this.id=id;
        this.suit=suit;
        this.rank=rank;
        this.imageId=imageId;
        this.cardImage=cardImage;
        this.faceUp=faceUp;
        this.col=col;
        this.order=order;
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="card_id")
    public int id;
    @ColumnInfo(name="suit")
    public String suit;
    @ColumnInfo(name="rank")
    public int rank;
    @ColumnInfo(name="face_up")
    public boolean faceUp;
    @ColumnInfo(name="card_column")
    public String col;
    @ColumnInfo(name="card_order")
    public int order;
    @ColumnInfo(name="imageId")
    public int imageId;
    @ColumnInfo(name="cardImage")
    public int cardImage;
}
