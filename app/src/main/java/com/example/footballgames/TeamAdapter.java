package com.example.footballgames;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private List<TeamPost> TeamList;
    private Context context;

    public TeamAdapter(List<TeamPost> TeamList, Context context) {
        this.TeamList = TeamList;
        this.context = context;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.team_item, parent, false);
        return new TeamViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        TeamPost teamPost = TeamList.get(position);

        holder.takim_isim.setText(teamPost.takim_isim);
        Picasso.get()
                .load(teamPost.teamPhoto)
                .into(holder.teamPhoto);


        // Picasso kullanarak urunfoto'yu ImageView'a yükleyin


//



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*/



                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return TeamList.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        public TextView takim_isim;
        public ImageView teamPhoto;

        public TeamViewHolder(View view) {
            super(view);

            takim_isim = view.findViewById(R.id.takim_isim);

            teamPhoto= view.findViewById(R.id.teamPhoto);
        }
    }
}
