package com.example.propbid.ViewHolder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propbid.Interface.ItemCllickListener;
import com.example.propbid.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView notificationMessage;
    public RelativeLayout boxColorChanger;
    public CircleImageView profileImage;
    public ItemCllickListener listener;


    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);
        notificationMessage= (TextView) itemView.findViewById(R.id.notificationMessage);
        boxColorChanger = (RelativeLayout) itemView.findViewById(R.id.boxColor);
        profileImage = (CircleImageView) itemView.findViewById(R.id.fromProfile);


    }

    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemCllickListener listener) {
        this.listener = listener;
    }
}