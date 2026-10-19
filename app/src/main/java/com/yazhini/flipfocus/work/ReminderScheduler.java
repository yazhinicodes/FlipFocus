package com.yazhini.flipfocus.work;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public final class ReminderScheduler {

    private static final String WORK_NAME = "daily_focus_reminder";
    private static final int REMINDER_HOUR = 18;   // 6 PM

    private ReminderScheduler() {
    }

    public static void scheduleDaily(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(ReminderWorker.class, 24, TimeUnit.HOURS)
                        .setInitialDelay(delayUntilNext(REMINDER_HOUR), TimeUnit.MILLISECONDS)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request);
    }

    private static long delayUntilNext(int hour) {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, hour);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        if (!next.after(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1);   // 6 PM already passed -> tomorrow
        }
        return next.getTimeInMillis() - now.getTimeInMillis();
    }
}