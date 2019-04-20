package com.reactiveandroid.query.api;

import com.reactiveandroid.ReActiveAndroid;

public final class Transactions {

    public static void BeginTransactions(Class<?> databaseClass) {

        if (databaseClass == null) {
            throw new IllegalArgumentException("Database Class referenced not found.");
        }

        ReActiveAndroid.getDatabase(databaseClass).beginTransaction();

    }

    public static void EndTransactions(Class<?> databaseClass) {

        if (databaseClass == null) {
            throw new IllegalArgumentException("Database Class referenced not found.");
        }

        ReActiveAndroid.getDatabase(databaseClass).getWritableDatabase().setTransactionSuccessful();
        ReActiveAndroid.getDatabase(databaseClass).endTransaction();
    }

}
