package com.example.contatos;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditarContato extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText mNameEditText, mNumberEditText;
    private Uri mCurrentContactUri;
    private String mType = Contract.ContactEntry.TIPO_CONTATO_CELULAR;
    private boolean mContactHasChanged = false;
    Spinner mSpinner;
    public static final int LOADER = 0;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mContactHasChanged = true;
            return false;
        }
    };

    boolean hasAllRequiredValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_contato);

        Intent intent = getIntent();
        mCurrentContactUri = intent.getData();

        mNameEditText = findViewById(R.id.nameEditText);
        mNumberEditText = findViewById(R.id.phoneEditText);
        mSpinner = findViewById(R.id.spinner);

        if (mCurrentContactUri == null) {
            setTitle("Add a Contact");
            invalidateOptionsMenu();

        } else {
            setTitle("Edit a Contact");
            getLoaderManager().initLoader(LOADER, null, this);

        }

        mNameEditText.setOnTouchListener(mOnTouchListener);
        mNumberEditText.setOnTouchListener(mOnTouchListener);
        mSpinner.setOnTouchListener(mOnTouchListener);

        setUpSpinner();


    }

    private void setUpSpinner() {

        ArrayAdapter spinner = ArrayAdapter.createFromResource(this, R.array.arrayspinner, android.R.layout.simple_spinner_item);
        spinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(spinner);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.casa))) {
                        mType = Contract.ContactEntry.TIPO_CONTATO_CASA;
                    } else if (selection.equals(getString(R.string.trabalho))) {
                        mType = Contract.ContactEntry.TIPO_CONTATO_TRABALHO;

                    } else {
                        mType = Contract.ContactEntry.TIPO_CONTATO_CELULAR;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = Contract.ContactEntry.TIPO_CONTATO_CELULAR;

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menueditor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentContactUri == null) {
            MenuItem item = (MenuItem) menu.findItem(R.id.delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveContact();
                if (hasAllRequiredValues == true) {
                    finish();
                }
                return true;
            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mContactHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditarContato.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditarContato.this);
                    }
                };
                showUnsavedChangesDialog(discardButton);
                return true;
        }
            return super.onOptionsItemSelected(item);
    }

    private boolean saveContact() {
        String name = mNameEditText.getText().toString().trim();
        String phone = mNumberEditText.getText().toString().trim();

        if (mCurrentContactUri == null && TextUtils.isEmpty(name)
        && mType == Contract.ContactEntry.TIPO_CONTATO_CELULAR) {
            hasAllRequiredValues = true;
            return hasAllRequiredValues;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;


        } else {
            values.put(Contract.ContactEntry.COLUMN_NOME, name);
        }

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Preencha o numero", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;


        } else {
            values.put(Contract.ContactEntry.COLUMN_NUMERO, phone);
        }

        values.put(Contract.ContactEntry.COLUMN_TIPO_CONTATO, mType);

        hasAllRequiredValues = true;
        return hasAllRequiredValues;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       String[] projection = {Contract.ContactEntry._ID,
               Contract.ContactEntry.COLUMN_NOME,
               Contract.ContactEntry.COLUMN_NUMERO,
               Contract.ContactEntry.COLUMN_TIPO_CONTATO
       };

       return new CursorLoader(this, mCurrentContactUri,
               projection, null,
               null,
               null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int name = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NOME);
            int type = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_TIPO_CONTATO);
            int number = cursor.getColumnIndex(Contract.ContactEntry.COLUMN_NUMERO);

            String contactname = cursor.getString(name);
            String contactnumber = cursor.getString(number);
            String typeof = cursor.getString(type);

            mNumberEditText.setText(contactnumber);
            mNameEditText.setText(contactname);

            switch (typeof) {

                case Contract.ContactEntry.TIPO_CONTATO_CASA:
                    mSpinner.setSelection(1);
                    break;

                case Contract.ContactEntry.TIPO_CONTATO_TRABALHO:
                        mSpinner.setSelection(2);
                    break;

                default:
                    mSpinner.setSelection(0);

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNumberEditText.setText("");
        mNameEditText.setText("");
    }

    private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Descartar alterações?");
        builder.setPositiveButton("Descartar", discardButtonClickListener);
        builder.setNegativeButton("Continuar Editando", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Deletar este item");
        builder.setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentContactUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentContactUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Erro",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deletado",
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        if (!mContactHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
}

