package edu.psu.solitaire;
import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
@Entity(tableName="stats")
public class Stat {
    public Stat(int id, int moves, String time, String date){
        this.id=id;
        this.moves=moves;
        this.time=time;
        this.date=date;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="game_number")
    public int id;

    @ColumnInfo(name="moves")
    public int moves;

    @ColumnInfo(name="time")
    public String time;


    @ColumnInfo(name="date")
    public String date;
}
