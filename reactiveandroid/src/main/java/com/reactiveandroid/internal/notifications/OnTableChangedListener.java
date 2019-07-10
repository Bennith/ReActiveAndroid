package com.reactiveandroid.internal.notifications;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface OnTableChangedListener {

    /**
     * Called when table changes.
     *
     * @param tableChanged The table that has changed.
     * @param action       The action that occurred.
     */
    void onTableChanged(@NonNull Class<?> tableChanged, @NonNull ChangeAction action);

}
