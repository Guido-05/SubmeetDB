package com.submeet.scheduler.avvisi.boundary;

import com.submeet.scheduler.avvisi.controller.NotifyControl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class NotifyBoundary {

    private final NotifyControl control = new NotifyControl();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public NotifyBoundary() {
        System.out.println("[Scheduler] Starting scheduler...");
        scheduleDailyNotification();
    }

    private void scheduleDailyNotification() {
        // Calcola il tempo di attesa fino alle 09:00
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.with(LocalTime.of(9, 0));
        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1);
        }

        long initialDelay = java.time.Duration.between(now, nextRun).toMillis();
        long period = TimeUnit.DAYS.toMillis(1);

        scheduler.scheduleAtFixedRate(this::checkNotify, initialDelay, period, TimeUnit.MILLISECONDS);
        //scheduler.scheduleAtFixedRate(this::checkNotify, 0, 30, TimeUnit.SECONDS);  // DEBUG

    }

    public void checkNotify() {
        System.out.println("[Scheduler] Expiration deadlines check started at " + LocalTime.now());

        control.checkRevisionDeadline();
        control.checkReviewerInviteDeadline();
        control.checkPaperAssignmentDeadline();
        control.checkPaperSubmissionDeadline();
        control.checkFinalVersionDeadline();

        System.out.println("[Scheduler] Check completed at " + LocalTime.now());
    }
}
