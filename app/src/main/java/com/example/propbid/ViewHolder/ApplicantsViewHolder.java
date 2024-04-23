package com.example.propbid.ViewHolder;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propbid.Interface.ItemCllickListener;
import com.example.propbid.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ApplicantsViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView applicantName,bidPrice,textOngoing,applicantDate,applicantNames,applicantBid;
    public CircleImageView applicantImage;
    public RatingBar applicantRating;
    public ItemCllickListener listener;



    public ApplicantsViewHolder(@NonNull View itemView) {
        super(itemView);
        applicantName = (TextView) itemView.findViewById(R.id.applicantName);
        applicantImage = (CircleImageView) itemView.findViewById(R.id.applicantImage);
        bidPrice = (TextView) itemView.findViewById(R.id.bidPrice);
        applicantNames = (TextView) itemView.findViewById(R.id.applicantName);
        applicantBid = (TextView) itemView.findViewById(R.id.applicantBid);
        applicantDate = (TextView) itemView.findViewById(R.id.applicantDate);
        //applicantRating = (RatingBar) itemView.findViewById(R.id.applicantRating);
    }

    @Override
    public void onClick(View view) {

        listener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemCllickListener listener) {
        this.listener = listener;
    }
}