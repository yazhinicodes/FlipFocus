package com.yazhini.flipfocus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yazhini.flipfocus.data.Session;
import com.yazhini.flipfocus.data.SessionRepository;

import java.util.Calendar;

public class MainViewModel extends AndroidViewModel {

    private static final long MIN_SESSION_MS = 10_000;  // ignore shorter sessions

    private final SessionRepository repository;
    private final LiveData<Long> todayTotalMs;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new SessionRepository(application);
        todayTotalMs = repository.getTotalSince(startOfTodayMs());
    }

    public LiveData<Long> getTodayTotalMs() {
        return todayTotalMs;
    }

    public void saveSession(long startTimeMs, long durationMs) {
        if (durationMs >= MIN_SESSION_MS) {
            repository.insert(new Session(startTimeMs, durationMs));
        }
    }

    private long startOfTodayMs() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}