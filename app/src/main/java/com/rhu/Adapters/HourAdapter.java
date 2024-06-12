package com.rhu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Objects.Hour;
import com.rhu.R;

import java.util.ArrayList;

public class HourAdapter extends RecyclerView.Adapter<HourAdapter.hourViewHolder> {

    private static final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<Hour> arrHours;
    private OnHourListener mOnHourListener;

    public HourAdapter(Context context, ArrayList<Hour> arrHours, OnHourListener onHourListener) {
        this.context = context;
        this.arrHours = arrHours;
        this.mOnHourListener = onHourListener;
    }

    @NonNull
    @Override
    public hourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_hour, parent, false);
        return new hourViewHolder(view, mOnHourListener);
    }

    @Override
    public void onBindViewHolder(@NonNull hourViewHolder holder, int position) {
        Hour hour = arrHours.get(position);

        String status = hour.getStatus();

        if (position <= 3) {
//            holder.tvHour.setText((position + 8) + ":00 - "+(position + 9)+":00 AM");
            holder.tvHour.setText((position + 8) + ":00 AM");
        }
//        else if (position == 3) {
////            holder.tvHour.setText((position + 8) + ":00 - "+(position + 9)+":00 PM");
//            holder.tvHour.setText((position + 8) + ":00 PM");
//        }
        else {
//            holder.tvHour.setText((position - 3) + ":00 - "+(position - 2)+":00 PM");
            holder.tvHour.setText((position - 3) + ":00 PM");
        }
    }

    @Override
    public int getItemCount() {
        return arrHours.size();
    }

    public class hourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnHourListener onHourListener;
        TextView tvHour;
        MaterialButton btnBook;

        public hourViewHolder(@NonNull View itemView, OnHourListener onHourListener) {
            super(itemView);

            tvHour = itemView.findViewById(R.id.tvHour);
            btnBook = itemView.findViewById(R.id.btnBook);

            this.onHourListener = onHourListener;
            btnBook.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onHourListener.onHourClick(getAdapterPosition());
        }
    }

    public interface OnHourListener{
        void onHourClick(int position);
    }
}
