package com.reactiveandroid.internal.serializer;

import androidx.annotation.Nullable;

import java.util.Date;

class IncorrectTestSerializer extends TypeSerializer {

    @Nullable
    @Override
    public Date serialize(@Nullable Object data) {
        return null;
    }

    @Nullable
    @Override
    public Long deserialize(@Nullable Object data) {
        return null;
    }

}
