package com.example.contatos;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class Adapter extends CursorAdapter {


    public Adapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.contato, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView, numberView, typeofContactView;

        nameView = view.findViewById(R.id.textName);
        numberView = view.findViewById(R.id.textNumber);
        typeofContactView = view.findViewById(R.id.textTypeofContact);
        int name = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NOME);
        int type = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_TIPO_CONTATO);
        int number = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NUMERO);

        String contactname = cursor.getString(name);
        String contactnumber = cursor.getString(number);
        String typeof = cursor.getString(type);

        nameView.setText(contactname);
        numberView.setText(contactnumber);
        typeofContactView.setText(typeof);
    }
}
