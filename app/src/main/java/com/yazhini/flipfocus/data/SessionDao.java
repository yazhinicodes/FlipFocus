package com.yazhini.flipfocus.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    void insert(Session session);

    @Query("SELECT * FROM sessions ORDER BY startTimeMs DESC")
    LiveData<List<Session>> getAllSessions();   // used in Part 5

    @Query("SELECT COALESCE(SUM(durationMs), 0) FROM sessions WHERE startTimeMs >= :startOfDayMs")
    LiveData<Long> getTotalSince(long startOfDayMs);

    @Query("SELECT * FROM sessions WHERE startTimeMs >= :sinceMs ORDER BY startTimeMs DESC")
    LiveData<List<Session>> getSessionsSince(long sinceMs);
}
