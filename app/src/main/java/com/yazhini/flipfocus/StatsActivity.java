package com.yazhini.flipfocus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.appbar.MaterialToolbar;

import com.yazhini.flipfocus.util.StatsCalculator;
import com.yazhini.flipfocus.util.TimeUtils;
import com.yazhini.flipfocus.view.BarChartView;
import com.yazhini.flipfocus.viewmodel.StatsViewModel;

public class StatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        BarChartView chart = findViewById(R.id.barChart);
        TextView tvWeekTotal = findViewById(R.id.tvWeekTotal);

        StatsViewModel viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        viewModel.getLastWeekSessions().observe(this, sessions -> {
            long now = System.currentTimeMillis();
            long[] totals = StatsCalculator.dailyTotals(sessions, now);
            String[] labels = StatsCalculator.dayLabels(now);
            chart.setData(labels, totals);

            long sum = 0;
            for (long t : totals) {
                sum += t;
            }
            tvWeekTotal.setText("Total this week: " + TimeUtils.formatDuration(sum));
        });
    }
}