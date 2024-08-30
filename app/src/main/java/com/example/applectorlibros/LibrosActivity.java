package com.example.applectorlibros;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LibrosActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private ListView listView;
    private List<PdfFile> pdfFiles;
    private static final String TAG = "LibrosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libros);

        listView = findViewById(R.id.listView);
        pdfFiles = new ArrayList<>();

        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showManageStoragePermissionDialog();
            } else {
                proceedWithAppFunctionality();
            }
        } else {
            // Para versiones anteriores a Android 11, pedimos los permisos tradicionales
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                // Muestra el modal para aceptar permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                // Si los permisos ya fueron concedidos, procedemos con la funcionalidad de la app
                proceedWithAppFunctionality();
            }
        }
    }

    private void showManageStoragePermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new AlertDialog.Builder(this)
                    .setTitle("Permiso necesario")
                    .setMessage("Esta aplicación necesita acceso a todos los archivos para funcionar correctamente. Por favor, concede el permiso en la configuración.")
                    .setPositiveButton("Conceder permiso", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            Uri uri = Uri.parse("package:" + getPackageName());
                            intent.setData(uri);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            // Si la intención no es manejada, dirigir a la configuración de la aplicación
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            Toast.makeText(this, "No se pudo abrir la configuración de permisos. Por favor, concede el permiso manualmente en la configuración de la aplicación.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        Toast.makeText(LibrosActivity.this, "Permiso necesario para acceder a los archivos.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .show();
        } else {
            Toast.makeText(this, "Este permiso solo es necesario en Android 11 o superior.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permiso concedido. Ahora puedes acceder a todos los archivos.", Toast.LENGTH_SHORT).show();
                    proceedWithAppFunctionality();
                } else {
                    Toast.makeText(this, "Permiso para gestionar todos los archivos fue denegado.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido. Ahora puedes acceder a los archivos.", Toast.LENGTH_SHORT).show();
                proceedWithAppFunctionality();
            } else {
                Toast.makeText(this, "Permiso denegado. La aplicación no puede funcionar sin este permiso.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void proceedWithAppFunctionality() {
        loadPdfFiles();  // Este método debe cargar la lista de PDFs
        displayPdfList();  // Este método debe mostrar la lista de PDFs en el ListView
    }

    private void loadPdfFiles() {
        pdfFiles.clear();
        // Buscar en la carpeta Downloads
        File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (documentsDirectory != null && documentsDirectory.exists()) {
            Log.i(TAG, "Buscando archivos en: " + documentsDirectory.getAbsolutePath());
            findPdfFiles(documentsDirectory);
        } else {
            Log.e(TAG, "Directorio de descargas no existe o no es accesible.");
        }

        if (pdfFiles.isEmpty()) {
            Log.e(TAG, "No se encontraron archivos PDF.");
        }
    }

    private void findPdfFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.isHidden()) {
                    findPdfFiles(file);  // Recursión para buscar en subdirectorios
                } else if (file.getName().toLowerCase().endsWith(".pdf")) {
                    pdfFiles.add(new PdfFile(file.getName(), file.getAbsolutePath()));
                    Log.i(TAG, "Archivo PDF encontrado: " + file.getAbsolutePath());
                }
            }
        } else {
            Log.e(TAG, "No se encontraron archivos en el directorio: " + directory.getAbsolutePath());
        }
    }

    private void displayPdfList() {
        if (pdfFiles.isEmpty()) {
            Toast.makeText(this, "No se encontraron archivos PDF.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                pdfFiles.stream().map(PdfFile::getName).toArray(String[]::new));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            PdfFile selectedFile = pdfFiles.get(position);
            Intent intent = new Intent(LibrosActivity.this, PdfReaderActivity.class);
            intent.putExtra("PDF_PATH", selectedFile.getPath());
            startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Verificar si ahora se ha concedido el permiso de gestión de archivos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // Permiso concedido, proceder a cargar la lista de PDFs
                proceedWithAppFunctionality();
            }
        } else {
            // Para versiones anteriores a Android 11, verificar permisos de almacenamiento
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                proceedWithAppFunctionality();
            }
        }
    }
}

