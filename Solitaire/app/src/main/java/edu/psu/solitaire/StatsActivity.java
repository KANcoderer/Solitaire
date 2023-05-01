package edu.psu.solitaire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StatsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        setSupportActionBar(findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.stats_list);
        StatListAdapter adapter = new StatListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StatViewModel statViewModel = new ViewModelProvider(this).get(StatViewModel.class);
        statViewModel.getAllStats().observe(this,adapter::setStats);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.stats_toolbar,menu);
        return true;
    }
    public void startHome(MenuItem item) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void startSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public  class StatListAdapter extends RecyclerView.Adapter<StatListAdapter.StatViewHolder>{
        class StatViewHolder extends RecyclerView.ViewHolder{
            private final TextView idView;
            private final TextView movesView;
            private final TextView timeView;
            private final TextView dateView;

            private Stat stat;

            private StatViewHolder(View itemView){
                super(itemView);
                idView=itemView.findViewById(R.id.gameId);
                movesView=itemView.findViewById(R.id.movesStat);
                timeView=itemView.findViewById(R.id.timeStat);
                dateView=itemView.findViewById(R.id.date);


            }
        }
        private List<Stat> stats;

        private final LayoutInflater layoutInflater;

        StatListAdapter(Context context){
            layoutInflater=LayoutInflater.from(context);
        }

        @Override
        public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
            return new StatViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull StatViewHolder holder, int position){
            if(stats != null){
                Stat current = stats.get(position);
                holder.stat=current;

                holder.idView.setText(String.valueOf(current.id));
                holder.movesView.setText(String.valueOf(current.moves));
                holder.timeView.setText(current.time);
                holder.dateView.setText(current.date);
            }else{
                holder.idView.setText(R.string.intializing);
                holder.movesView.setText(R.string.intializing);
                holder.timeView.setText(R.string.intializing);
                holder.dateView.setText(R.string.intializing);
            }
        }

        @Override
        public int getItemCount() {
            if(stats != null){
                return stats.size();
            }else{
                return 0;
            }
        }
        void setStats(List<Stat> stats){
            this.stats = stats;
            notifyDataSetChanged();
        }
    }
}