package com.reactiveandroid.query.api;

//pull request - bendothall
//Pagination API

import android.util.Log;

import com.reactiveandroid.query.QueryBase;
import com.reactiveandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

public class Pagination<T> extends QueryBase<T> {

    private Pagination() {
        super(null, null);
    }

    public static int getTableRowCount(Class<?> table) {

        if (table == null) {
            throw new IllegalArgumentException("Database Table referenced not found.");
        }

        return Select.from(table).count();
    }


    public static List loadPaginationData(
            String tableColumn,
            int offset,
            int limit,
            String orderBy,
            Class<?> table
    ){

        if (tableColumn == null) {
            throw new IllegalArgumentException("Database Table must be set");
        }

        if (limit == 0) {
            throw new IllegalArgumentException("Limit cannot be set to 0");
        }

        if (orderBy == null || !(orderBy.contains("DESC") || orderBy.contains("ASC")) ) {
            throw new IllegalArgumentException("Order must be set to DESC or ASC " + orderBy);
        }

        if (table == null) {
            throw new IllegalArgumentException("Database Table Class referenced not found.");
        }

        List result = new ArrayList<>();
        result = Select.from(table)
                .orderBy(tableColumn + " " + orderBy)
                .limit(limit)
                .offset(offset)
                .fetch();

        return result;
    }


}
