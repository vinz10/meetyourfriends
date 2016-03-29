package com.example.vincent.meetyourfriends;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.UsersContract;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

        // Lecture du fichier cache
        String fileName = "cache.txt";
        String [] temp;
        int ch;

        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;

        try {
            fis = openFileInput(fileName);
            try {
                while( (ch = fis.read()) != -1)
                    fileContent.append((char)ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        temp = fileContent.toString().split(";");

        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eCurrentPassword = ((EditText)findViewById(R.id.maCurrentPassword));

        eCurrentMail.setText(temp[0].toString());
        eCurrentPassword.setText(temp[1].toString());
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //

                //add the infos about the apartment to the intent -> so we can show the last intruduced apartment

                Intent intent = new Intent(this, Events.class);

                ModifyAccount.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void modifyEmail(View view) {

        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eCurrentPassword = ((EditText)findViewById(R.id.maCurrentPassword));
        EditText eNewMail = ((EditText)findViewById(R.id.maNewMail));
        EditText eConfirmMail = ((EditText)findViewById(R.id.maConfirmMail));

        TextView errorMail = ((TextView) findViewById(R.id.maErrorEmail));

        if (eNewMail.getText().toString().isEmpty()) {
            errorMail.setHint(R.string.newMailnull);
            errorMail.setVisibility(View.VISIBLE);
        } else if (!isEmailValid(eNewMail.getText().toString())) {
            errorMail.setHint(R.string.MailInvalid);
            errorMail.setVisibility(View.VISIBLE);
        } else if (eConfirmMail.getText().toString().isEmpty()) {
            errorMail.setHint(R.string.confirmMailnull);
            errorMail.setVisibility(View.VISIBLE);
        } else if (eNewMail.getText().toString().equals(eConfirmMail.getText().toString())) {
            errorMail.setVisibility(View.INVISIBLE);

            // Récupération de l'id
            int id = 0;
            Cursor cursor = null;
            DbHelper mDbHelper = new DbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            id = cursor.getInt(0);

            // Modification dans la base de données
            ContentValues values = new ContentValues();
            values.put(UsersContract.UserEntry.KEY_EMAIL, eNewMail.getText().toString());
            db.update(UsersContract.UserEntry.TABLE_NAME, values, "id="+id, null);

            // Modification du fichier cache
            this.deleteFile("cache.txt");
            String file = "cache.txt";
            String output = eNewMail.getText().toString() + ";" + eCurrentPassword.getText().toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(file, Context.MODE_PRIVATE);
                outputStream.write(output.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, Events.class);
            startActivity(intent);
        } else {
            errorMail.setHint(R.string.mailNotSame);
            errorMail.setVisibility(View.VISIBLE);
        }
    }

    public void modifyPassword(View view) {

        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eNewPassword = ((EditText)findViewById(R.id.maNewPassword));
        EditText eConfirmPassword = ((EditText)findViewById(R.id.maConfirmPassword));

        TextView errorPassword = ((TextView) findViewById(R.id.maErrorPassword));

        if (eNewPassword.getText().toString().isEmpty()) {
            errorPassword.setHint(R.string.newPasswordnull);
            errorPassword.setVisibility(View.VISIBLE);
        } else if (eConfirmPassword.getText().toString().isEmpty()) {
            errorPassword.setHint(R.string.confirmPasswordlnull);
            errorPassword.setVisibility(View.VISIBLE);
        } else if (eNewPassword.getText().toString().equals(eConfirmPassword.getText().toString())) {
            errorPassword.setVisibility(View.INVISIBLE);

            // Récupération de l'id
            int id = 0;
            Cursor cursor = null;
            DbHelper mDbHelper = new DbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            id = cursor.getInt(0);

            // Modification dans la base de données
            ContentValues values = new ContentValues();
            values.put(UsersContract.UserEntry.KEY_PASSWORD, eNewPassword.getText().toString());
            db.update(UsersContract.UserEntry.TABLE_NAME, values, "id="+id, null);

            // Modification du fichier cache
            this.deleteFile("cache.txt");
            String file = "cache.txt";
            String output = eCurrentMail.getText().toString() + ";" + eNewPassword.getText().toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(file, Context.MODE_PRIVATE);
                outputStream.write(output.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, Events.class);
            startActivity(intent);
        } else {
            errorPassword.setHint(R.string.passwordNotSame);
            errorPassword.setVisibility(View.VISIBLE);
        }
    }

    public void deleteAccount(View view) {

        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));

        // Récupération de l'id
        int id = 0;
        Cursor cursor = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        id = cursor.getInt(0);

        // Suppression dans la base de données
        db.delete(UsersContract.UserEntry.TABLE_NAME, UsersContract.UserEntry.KEY_ID + "=" + id, null);

        // Suppression du fichier cache
        this.deleteFile("cache.txt");

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
