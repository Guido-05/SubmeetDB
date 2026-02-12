package com.submeet.scheduler.avvisi;
import com.submeet.dbmsboundary.DBMSBoundary;
import com.submeet.scheduler.avvisi.boundary.NotifyBoundary;

public class MainScheduler {
    public static void main(String[] args) {
        DBMSBoundary.startConnection();

        new NotifyBoundary();
    }
}
