package com.yazhini.flipfocus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.yazhini.flipfocus.data.Session;
import com.yazhini.flipfocus.data.SessionRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final LiveData<List<Session>> sessions;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        sessions = new SessionRepository(application).getAllSessions();
    }

    public LiveData<List<Session>> getSessions() {
        return sessions;
    }
}
