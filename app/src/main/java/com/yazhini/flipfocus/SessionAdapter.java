package com.yazhini.flipfocus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.yazhini.flipfocus.data.Session;
import com.yazhini.flipfocus.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionAdapter extends ListAdapter<Session, SessionAdapter.SessionViewHolder> {

    private static final DiffUtil.ItemCallback<Session> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Session>() {
                @Override
                public boolean areItemsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(@NonNull Session oldItem, @NonNull Session newItem) {
                    return oldItem.startTimeMs == newItem.startTimeMs
                            && oldItem.durationMs == newItem.durationMs;
                }
            };

    // created once, not inside onBindViewHolder (which runs for every row shown)
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

    public SessionAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = getItem(position);
        holder.tvDate.setText(dateFormat.format(new Date(session.startTimeMs)));
        holder.tvDuration.setText(TimeUtils.formatDuration(session.durationMs));
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate;
        final TextView tvDuration;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }
    }
}
