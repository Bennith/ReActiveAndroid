package com.reactiveandroid.query.api;

//pull request - bendothall
//Transactions API

import androidx.annotation.NonNull;
import android.util.Log;

import com.reactiveandroid.Model;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.annotation.Table;
import com.reactiveandroid.internal.ModelAdapter;
import com.reactiveandroid.internal.database.DatabaseInfo;
import com.reactiveandroid.internal.database.table.TableInfo;
import com.reactiveandroid.query.Insert;
import com.reactiveandroid.query.Select;
import com.reactiveandroid.query.Update;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Boolean BulkCRUDUpdates(Class<?> databaseClass, Class<?> table, List<?> modelData) {

        boolean result = true;

        if (databaseClass == null) {
            throw new IllegalArgumentException("Database Class referenced not found.");
        }

        if (table == null) {
            throw new IllegalArgumentException("Table Class referenced not found.");
        }

        if (modelData == null) {
            throw new IllegalArgumentException("Model Data is Empty");
        }

        BeginTransactions(databaseClass);

        for (Object modelClassDataHolder : modelData) {

            for(Field modelClassDataHolderField : modelClassDataHolder.getClass().getDeclaredFields()){
                try {

                    modelClassDataHolderField.setAccessible(true);
                    Object value = modelClassDataHolderField.get(modelClassDataHolder);

                    if(Select.from(table).where(modelClassDataHolderField.getName() + " = ?", value.toString()).count() == 1){
                        //found
                        //Update.table(table).set(modelClassDataHolderField.getName() + " = ?", value.toString() + " Update via Bulk").where(modelClassDataHolderField.getName() + " = ?", value.toString()).execute();

                    }else{
                        //create new
                        //Insert.into(table).columns(modelClassDataHolderField.getName()).values(value.toString()+ " Insert via Bulk").execute();
                    }

                }catch (Exception error) {

                }

            }

        }


        EndTransactions(databaseClass);

        return result;
    }

}
