package com.moutamid.uchannelbooster.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moutamid.uchannelbooster.R;
import com.moutamid.uchannelbooster.models.CoinPack;
import com.moutamid.uchannelbooster.ui.subscribe.utilis.Stash;

import java.util.List;

public class CoinPackAdapter extends RecyclerView.Adapter<CoinPackAdapter.ViewHolder> {
    private List<CoinPack> coinPackList;
    Context context;

    public CoinPackAdapter(List<CoinPack> coinPackList, Context context) {
        this.coinPackList = coinPackList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_pack, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CoinPack coinPack = coinPackList.get(position);
        holder.coinsTextView.setText("+" + coinPack.getCoins());
        holder.priceTextView.setText("$" + coinPack.getPrice());
        holder.priceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stash.put("coins",coinPack.getCoins()+"");
                Stash.put("amount",coinPack.getPrice()+"");
                context.startActivity(new Intent(context, SubscriptionActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return coinPackList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView coinsTextView;
        Button priceTextView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coinsTextView = itemView.findViewById(R.id.textviewStyle);
            priceTextView = itemView.findViewById(R.id.buyPoints1);
        }
    }
}
