package com.example.footballgames;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<PlayerPost> PlayerList;
    private Context context;

    public PlayerAdapter(List<PlayerPost> PlayerList, Context context) {
        this.PlayerList = PlayerList;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        PlayerPost playerPost = PlayerList.get(position);
        holder.isim.setText(playerPost.isim);
        holder.takim.setText(playerPost.takim);
        holder.reyting.setText(playerPost.reyting);


        Picasso.get()
                .load(playerPost.playerPhoto)
                .into(holder.playerPhoto);


        // Picasso kullanarak urunfoto'yu ImageView'a yükleyin


//



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*/
                if (playerPost.tur.equals("urun")) {
                    Intent intent = new Intent(context, UrunDetayActivity.class);
                    intent.putExtra("isim", urun.isim);
                    intent.putExtra("marka", urun.marka);
                    intent.putExtra("urunfoto", urun.urunfoto);
                    intent.putExtra("flagfoto", urun.flagfoto);
                    intent.putExtra("tur", urun.tur);
                    context.startActivity(intent);


                }*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return PlayerList.size();
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        public TextView isim, takim,reyting;
        public ImageView playerPhoto;

        public PlayerViewHolder(View view) {
            super(view);
            isim = view.findViewById(R.id.isim);
            takim = view.findViewById(R.id.takim);
            reyting = view.findViewById(R.id.reyting);
            playerPhoto= view.findViewById(R.id.playerPhoto);
        }
    }
}
