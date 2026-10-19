package com.yazhini.flipfocus.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sessions")
public class Session {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long startTimeMs;   // when the session started
    public long durationMs;    // how long it lasted

    public Session(long startTimeMs, long durationMs) {
        this.startTimeMs = startTimeMs;
        this.durationMs = durationMs;
    }
}
