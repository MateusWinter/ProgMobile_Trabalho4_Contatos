package com.example.contatos;

import android.provider.BaseColumns;

public final class Contract {

    public Contract() {

    }

    public static final String CONTENT_AUTHORITY = "com.example.contatos";
    public static final String PATH_CONTACTS = "contatos";

    public static abstract class ContactEntry implements BaseColumns{

        public static final String TABLE_NOME = "contatos";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOME = "nome";
        public static final String COLUMN_NUMERO = "numero";
        public static final String COLUMN_TIPO_CONTATO = "tipo";


        public static final String TIPO_CONTATO_TRABALHO = "Trabalho";
        public static final String TIPO_CONTATO_CASA = "Casa";
        public static final String TIPO_CONTATO_CELULAR = "Celular";

        public static boolean isValidType (String type) {
            if (type == TIPO_CONTATO_CASA || type == TIPO_CONTATO_CELULAR || type == TIPO_CONTATO_TRABALHO) {
                return true;
            }
            return false;
        }


    }
}
