package com.example.applectorlibros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etCodigo;
    private Button btnIngresar, btnCrearCuenta;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = findViewById(R.id.etUsuario);
        etCodigo = findViewById(R.id.etCodigo);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        db = new DatabaseHelper(this);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString().trim();
                String codigo = etCodigo.getText().toString().trim();

                // Verificar que los campos no estén vacíos
                if (!usuario.isEmpty() && !codigo.isEmpty()) {
                    Estudiante estudiante = db.obtenerEstudiante(usuario, codigo);

                    if (estudiante != null) {
                        // Si el estudiante existe, mostrar mensaje y navegar a la siguiente actividad
                        Toast.makeText(MainActivity.this, "Bienvenido, " + estudiante.getNombre(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, LibrosActivity.class);
                        startActivity(intent);
                    } else {
                        // Si no existe el estudiante, mostrar mensaje de error
                        Toast.makeText(MainActivity.this, "Usuario o código incorrecto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si los campos están vacíos, mostrar mensaje de error
                    Toast.makeText(MainActivity.this, "Por favor, ingresa tu usuario y código", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }
}

