package com.reactiveandroid.query;

import androidx.annotation.NonNull;

import com.reactiveandroid.internal.notifications.ChangeAction;
import com.reactiveandroid.internal.utils.QueryUtils;

abstract class InsertQueryBase<T> extends ExecutableQueryBase<T> {

    InsertQueryBase(Query parent, Class<T> table) {
        super(parent, table);
    }

    @NonNull
    @Override
    ChangeAction getChangeAction() {
        return ChangeAction.INSERT;
    }


}
