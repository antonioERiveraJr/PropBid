package com.example.propbid.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propbid.Interface.ItemCllickListener;
import com.example.propbid.R;

public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView ClientName;
    public ImageView ClientPicture;
    public ItemCllickListener listener;


    public ClientViewHolder(@NonNull View itemView) {
        super(itemView);
        ClientName = (TextView) itemView.findViewById(R.id.client_name);
        ClientPicture = (ImageView) itemView.findViewById(R.id.client_picture);

    }

    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemCllickListener listener){
        this.listener = listener;
    }
}

