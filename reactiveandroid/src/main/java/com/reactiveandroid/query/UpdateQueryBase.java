package com.reactiveandroid.query;

import androidx.annotation.NonNull;

import com.reactiveandroid.internal.notifications.ChangeAction;

abstract class UpdateQueryBase<T> extends ExecutableQueryBase<T> {

    UpdateQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    @NonNull
    @Override
    ChangeAction getChangeAction() {
        return ChangeAction.UPDATE;
    }


}
