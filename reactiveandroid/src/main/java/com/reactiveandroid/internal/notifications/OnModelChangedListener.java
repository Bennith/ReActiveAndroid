package com.reactiveandroid.internal.notifications;


import androidx.annotation.NonNull;

public interface OnModelChangedListener<ModelClass> {

    void onModelChanged(@NonNull ModelClass model, @NonNull ChangeAction action);

}
