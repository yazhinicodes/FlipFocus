package com.yazhini.flipfocus.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SessionRepository {

    private final SessionDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SessionRepository(Application application) {
        dao = AppDatabase.getInstance(application).sessionDao();
    }

    public void insert(Session session) {
        executor.execute(() -> dao.insert(session));
    }

    public LiveData<List<Session>> getAllSessions() {
        return dao.getAllSessions();
    }

    public LiveData<Long> getTotalSince(long startOfDayMs) {
        return dao.getTotalSince(startOfDayMs);
    }

    public LiveData<List<Session>> getSessionsSince(long sinceMs) {
        return dao.getSessionsSince(sinceMs);
    }
}
