package com.example.contatos;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class Provider extends ContentProvider {

    public static final int CONTACTS = 100;
    public static final int CONTACTS_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_CONTACTS + "/#", CONTACTS_ID);
    }

    public DB mDbhelper;

    @Override
    public boolean onCreate() {
        mDbhelper = new DB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,  String[] projection, String selection,   String[] selectionArgs,   String sortOrder) {
        SQLiteDatabase database  = mDbhelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = database.query(Contract.ContactEntry.TABLE_NOME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CONTACTS_ID:

                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.ContactEntry.TABLE_NOME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cant Query" + uri);


        }


       cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Override
    public String getType( Uri uri) {
        return null;
    }

    @Override
    public Uri insert(  Uri uri,  ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, values);
            default:
                throw new IllegalArgumentException("Erro" + uri);
        }

    }

    private Uri insertContact(Uri uri, ContentValues values) {

        String name = values.getAsString(Contract.ContactEntry.COLUMN_NOME);
        if (name == null) {
            throw new IllegalArgumentException("Preencha o nome");
        }


        String number = values.getAsString(Contract.ContactEntry.COLUMN_NUMERO);
        if (number == null) {
            throw new IllegalArgumentException("Preencha o numero");
        }


        String type = values.getAsString(Contract.ContactEntry.COLUMN_TIPO_CONTATO);
        if (type == null || !Contract.ContactEntry.isValidType(type)) {
            throw new IllegalArgumentException("Preencha o tipo do contato");

        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        long id = database.insert(Contract.ContactEntry.TABLE_NOME, null, values);

        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete ( Uri uri,   String selection,  String[] selectionArgs) {

        int rowsDeleted;
        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                rowsDeleted = database.delete(Contract.ContactEntry.TABLE_NOME, selection, selectionArgs);
                break;

            case CONTACTS_ID:
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.ContactEntry.TABLE_NOME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Erro" + uri);
        }

        if (rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(  Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return updateContact(uri, values, selection, selectionArgs);

            case CONTACTS_ID:

                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateContact(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Erro");


        }
    }

    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Contract.ContactEntry.COLUMN_NOME)) {
            String name = values.getAsString(Contract.ContactEntry.COLUMN_NOME);
           if (name == null) {
            throw new IllegalArgumentException("Preencha o nome");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_NUMERO)) {

              String number = values.getAsString(Contract.ContactEntry.COLUMN_NUMERO);
              if (number == null) {
                  throw new IllegalArgumentException("Preencha o numero");
              }
        }


        if (values.containsKey(Contract.ContactEntry.COLUMN_TIPO_CONTATO)) {
            String type = values.getAsString(Contract.ContactEntry.COLUMN_TIPO_CONTATO);
            if (type == null || !Contract.ContactEntry.isValidType(type)) {
                throw new IllegalArgumentException("Preencha o tipo de contato");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        int rowsUpdated = database.update(Contract.ContactEntry.TABLE_NOME, values, selection, selectionArgs);
        if (rowsUpdated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
