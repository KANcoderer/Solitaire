package edu.psu.solitaire;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class StatViewModel extends AndroidViewModel{
    private LiveData<List<Stat>> stats;
    int count;
    public StatViewModel(Application application){
        super(application);
        stats=StatDatabase.getDatabase(getApplication()).statsDAO().getALL();
    }

    public LiveData<List<Stat>> getAllStats(){return stats;}

}
