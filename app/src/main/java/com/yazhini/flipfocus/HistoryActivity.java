package com.yazhini.flipfocus;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import com.yazhini.flipfocus.viewmodel.HistoryViewModel;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        TextView tvEmpty = findViewById(R.id.tvEmpty);

        SessionAdapter adapter = new SessionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(
                new androidx.recyclerview.widget.DividerItemDecoration(
                        this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        HistoryViewModel viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        viewModel.getSessions().observe(this, sessions -> {
            adapter.submitList(sessions);
            tvEmpty.setVisibility(sessions.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}