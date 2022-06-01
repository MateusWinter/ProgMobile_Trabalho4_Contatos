package com.example.contatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.contatos.Contract.ContactEntry;

public class DB extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todaycontact.db";
    public static final int DATABASE_VERSION = 1;



    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NOME + " ("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ContactEntry.COLUMN_NOME + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_NUMERO + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_TIPO_CONTATO + " TEXT NOT NULL);";

                db.execSQL(SQL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
