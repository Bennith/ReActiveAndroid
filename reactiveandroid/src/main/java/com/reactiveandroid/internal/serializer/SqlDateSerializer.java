package com.reactiveandroid.internal.serializer;

import androidx.annotation.Nullable;

import java.sql.Date;

public final class SqlDateSerializer extends TypeSerializer<Date, Long> {

    @Nullable
    public Long serialize(@Nullable Date data) {
        if (data == null) return null;
        return data.getTime();
    }

    @Nullable
    public Date deserialize(@Nullable Long data) {
        if (data == null) return null;
        return new Date(data);
    }

}