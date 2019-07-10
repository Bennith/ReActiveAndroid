package com.reactiveandroid.internal.serializer;

import androidx.annotation.Nullable;

import java.util.Date;

class TestSerializer extends TypeSerializer<Date, Long> {

    @Nullable
    @Override
    public Long serialize(@Nullable Date data) {
        return null;
    }

    @Nullable
    @Override
    public Date deserialize(@Nullable Long data) {
        return null;
    }

}
