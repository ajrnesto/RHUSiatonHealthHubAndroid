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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Fragments.ChatFragment;
import com.rhu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class DoctorLocationAdapter extends RecyclerView.Adapter<DoctorLocationAdapter.doctorLocationViewHolder> {

    private static final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<String> arrDoctorLocation;
    ArrayList<Long> arrTimestamp;
    private OnDoctorLocationListener mOnDoctorLocationListener;

    public DoctorLocationAdapter(Context context, ArrayList<String> arrDoctorLocation, ArrayList<Long> arrTimestamp, OnDoctorLocationListener onDoctorLocationListener) {
        this.context = context;
        this.arrDoctorLocation = arrDoctorLocation;
        this.arrTimestamp = arrTimestamp;
        this.mOnDoctorLocationListener = onDoctorLocationListener;
    }

    @NonNull
    @Override
    public doctorLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_doctor_schedule, parent, false);
        return new doctorLocationViewHolder(view, mOnDoctorLocationListener);
    }

    @Override
    public void onBindViewHolder(@NonNull doctorLocationViewHolder holder, int position) {
        String doctorLocation = arrDoctorLocation.get(position);
        long timestamp = arrTimestamp.get(position);

        if (!Objects.equals(doctorLocation, "Not Set")) {
            holder.tvLocation.setText(doctorLocation);

            SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
            holder.tvDate.setText(sdfMonth.format(timestamp) + "\n" + sdfDay.format(timestamp));

            holder.tvAvailableMessage.setText("These services are only available in " + doctorLocation + ":");
        }
        else {
            holder.tvLocation.setText("No Doctor Schedule for Today");

            SimpleDateFormat sdfMonth = new SimpleDateFormat("MMM");
            SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
            holder.tvDate.setText(sdfMonth.format(timestamp) + "\n" + sdfDay.format(timestamp));

            holder.tvAvailableMessage.setText("These services are not available for the day:");
        }

        DB.collection("doctorName").document("doctorName")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            holder.tvDoctorName.setText(task.getResult().getString("doctorName"));
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return arrDoctorLocation.size();
    }

    public class doctorLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnDoctorLocationListener onDoctorLocationListener;
        TextView tvDate, tvLocation, tvAvailableMessage, tvDoctorName;

        public doctorLocationViewHolder(@NonNull View itemView, OnDoctorLocationListener onDoctorLocationListener) {
            super(itemView);

            this.onDoctorLocationListener = onDoctorLocationListener;

            tvDate = itemView.findViewById(R.id.tvDate);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAvailableMessage = itemView.findViewById(R.id.tvAvailableMessage);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
        }

        @Override
        public void onClick(View view) {
            onDoctorLocationListener.onDoctorLocationClick(getAdapterPosition());

        }
    }

    public interface OnDoctorLocationListener{
        void onDoctorLocationClick(int position);
    }
}
