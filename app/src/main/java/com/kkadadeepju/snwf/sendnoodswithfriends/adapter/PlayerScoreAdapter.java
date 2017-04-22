package com.kkadadeepju.snwf.sendnoodswithfriends.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kkadadeepju.snwf.sendnoodswithfriends.R;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.PlayerInfo;
import com.kkadadeepju.snwf.sendnoodswithfriends.model.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.id.list;

/**
 * Created by Junyu on 2017-04-22.
 */

public class PlayerScoreAdapter extends RecyclerView.Adapter<PlayerScoreAdapter.ViewHolder> {

    private ArrayList<UserInfo> modelList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_player_score, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo playerInfo = modelList.get(position);

        holder.name.setText(playerInfo.getName());
        holder.score.setText(String.valueOf(playerInfo.getScore()));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void setData(ArrayList<UserInfo> userInfos) {
        this.modelList = userInfos;
    }

    public void addData(UserInfo userInfos) {
        modelList.add(userInfos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView score;

        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.playerName);
            score = (TextView) itemView.findViewById(R.id.playerScore);
        }
    }
}
