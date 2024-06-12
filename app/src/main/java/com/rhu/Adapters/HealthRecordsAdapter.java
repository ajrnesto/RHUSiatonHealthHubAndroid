package com.rhu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Fragments.ChatFragment;
import com.rhu.Objects.HealthRecord;
import com.rhu.Objects.HealthRecord;
import com.rhu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class HealthRecordsAdapter extends RecyclerView.Adapter<HealthRecordsAdapter.healthRecordsViewHolder> {

    private static final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<HealthRecord> arrHealthRecords;
    private OnHealthRecordsListener mOnHealthRecordsListener;

    public HealthRecordsAdapter(Context context, ArrayList<HealthRecord> arrHealthRecords, OnHealthRecordsListener onHealthRecordsListener) {
        this.context = context;
        this.arrHealthRecords = arrHealthRecords;
        this.mOnHealthRecordsListener = onHealthRecordsListener;
    }

    @NonNull
    @Override
    public healthRecordsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_health_record, parent, false);
        return new healthRecordsViewHolder(view, mOnHealthRecordsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull healthRecordsViewHolder holder, int position) {
        HealthRecord healthRecords = arrHealthRecords.get(position);
        String id = healthRecords.getId();
        String userUid = healthRecords.getUserUid();
        String typeOfRecord = healthRecords.getTypeOfRecord();
        String description = healthRecords.getDescription();
        long timestamp = healthRecords.getTimestamp();

        holder.tvHealthRecordType.setText(typeOfRecord);
        holder.tvDescription.setText(description);
        loadTimestamp(holder, timestamp);
    }

    private void loadTimestamp(healthRecordsViewHolder holder, long schedule) {
        SimpleDateFormat sdfTimestamp = new SimpleDateFormat("MMM d yyyy, hh:mm aa");
        holder.tvTimestamp.setText(sdfTimestamp.format(schedule));
    }

    @Override
    public int getItemCount() {
        return arrHealthRecords.size();
    }

    public class healthRecordsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnHealthRecordsListener onHealthRecordsListener;
        TextView tvTimestamp, tvHealthRecordType, tvDescription;

        public healthRecordsViewHolder(@NonNull View itemView, OnHealthRecordsListener onHealthRecordsListener) {
            super(itemView);

            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvHealthRecordType = itemView.findViewById(R.id.tvHealthRecordType);
            tvDescription = itemView.findViewById(R.id.tvDescription);

            this.onHealthRecordsListener = onHealthRecordsListener;

        }

        @Override
        public void onClick(View view) {
            onHealthRecordsListener.onHealthRecordsClick(getAdapterPosition());
        }
    }

    public interface OnHealthRecordsListener{
        void onHealthRecordsClick(int position);
    }
}
