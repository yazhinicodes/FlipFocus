package com.yazhini.flipfocus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yazhini.flipfocus.data.Session;
import com.yazhini.flipfocus.data.SessionRepository;
import com.yazhini.flipfocus.util.StatsCalculator;

import java.util.List;

public class StatsViewModel extends AndroidViewModel {

    private final LiveData<List<Session>> lastWeekSessions;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        long windowStart = StatsCalculator.dayStarts(System.currentTimeMillis())[0];
        lastWeekSessions = new SessionRepository(application).getSessionsSince(windowStart);
    }

    public LiveData<List<Session>> getLastWeekSessions() {
        return lastWeekSessions;
    }
}
