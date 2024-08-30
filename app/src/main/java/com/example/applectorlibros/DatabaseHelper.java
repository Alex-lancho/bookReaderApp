package com.example.applectorlibros;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "estudiantes.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "estudiante";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_APELLIDOS = "apellidos";
    private static final String COLUMN_DNI = "dni";
    private static final String COLUMN_CARRERA = "carrera";
    private static final String COLUMN_CODIGO = "codigo";
    private static final String COLUMN_USUARIO = "usuario";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOMBRE + " TEXT,"
                + COLUMN_APELLIDOS + " TEXT,"
                + COLUMN_DNI + " TEXT,"
                + COLUMN_CARRERA + " TEXT,"
                + COLUMN_CODIGO + " TEXT,"
                + COLUMN_USUARIO + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean agregarEstudiante(Estudiante estudiante) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOMBRE, estudiante.getNombre());
        values.put(COLUMN_APELLIDOS, estudiante.getApellidos());
        values.put(COLUMN_DNI, estudiante.getDni());
        values.put(COLUMN_CARRERA, estudiante.getCarrera());
        values.put(COLUMN_CODIGO, estudiante.getCodigo());
        values.put(COLUMN_USUARIO, estudiante.getUsuario());

        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    public Estudiante obtenerEstudiante(String usuario, String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USUARIO + "=? AND " + COLUMN_CODIGO + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{usuario, codigo});

        if (cursor != null && cursor.moveToFirst()) {
            Estudiante estudiante = new Estudiante(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_APELLIDOS)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DNI)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CARRERA)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CODIGO)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USUARIO))
            );
            cursor.close();
            return estudiante;
        } else {
            return null;
        }
    }
}

