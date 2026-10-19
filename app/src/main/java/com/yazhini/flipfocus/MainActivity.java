package com.yazhini.flipfocus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.yazhini.flipfocus.util.TimeUtils;
import com.yazhini.flipfocus.viewmodel.MainViewModel;
import com.yazhini.flipfocus.work.ReminderScheduler;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final float FACE_DOWN_START = -8f;
    private static final float FACE_DOWN_STOP = -6f;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private MainViewModel viewModel;

    private TextView tvStatus, tvTimer, tvLast, tvToday;

    private boolean isFocusing = false;
    private long startTimeMs = 0;
    private long sessionStartEpochMs = 0;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                // Nothing to do. If the user said no, the reminder is simply not shown.
            });

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            long elapsed = SystemClock.elapsedRealtime() - startTimeMs;
            tvTimer.setText(TimeUtils.formatDuration(elapsed));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvTimer = findViewById(R.id.tvTimer);
        tvLast = findViewById(R.id.tvLast);
        tvToday = findViewById(R.id.tvToday);

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
        findViewById(R.id.btnStats).setOnClickListener(v ->
                startActivity(new Intent(this, StatsActivity.class)));

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getTodayTotalMs().observe(this, totalMs -> {
            tvToday.setText(TimeUtils.formatDuration(totalMs));
        });

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (accelerometer == null) {
            tvStatus.setText("This phone has no accelerometer");
        }

        ReminderScheduler.scheduleDaily(this);

        // TEMPORARY: show the notification right now. Run ONCE, then DELETE this line!
        androidx.work.WorkManager.getInstance(this).enqueue(
                new androidx.work.OneTimeWorkRequest.Builder(
                        com.yazhini.flipfocus.work.ReminderWorker.class).build());

        requestNotificationPermissionIfNeeded();
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (isFocusing) {
            stopFocus();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float z = event.values[2];

        if (!isFocusing && z < FACE_DOWN_START) {
            startFocus();
        } else if (isFocusing && z > FACE_DOWN_STOP) {
            stopFocus();
        }
    }

    private void startFocus() {
        isFocusing = true;
        startTimeMs = SystemClock.elapsedRealtime();
        sessionStartEpochMs = System.currentTimeMillis();
        tvStatus.setText("Focusing... keep it face-down");
        handler.post(ticker);
    }

    private void stopFocus() {
        isFocusing = false;
        handler.removeCallbacks(ticker);
        long duration = SystemClock.elapsedRealtime() - startTimeMs;
        viewModel.saveSession(sessionStartEpochMs, duration);
        tvLast.setText(TimeUtils.formatDuration(duration));
        tvTimer.setText("00:00");
        tvStatus.setText("Place your phone face-down to start");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}