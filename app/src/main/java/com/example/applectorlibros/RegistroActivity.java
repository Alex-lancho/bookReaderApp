package com.example.applectorlibros;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etDni, etCarrera, etCodigo, etUsuario;
    private Button btnRegistrar,btnAtras;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etDni = findViewById(R.id.etDni);
        etCarrera = findViewById(R.id.etCarrera);
        etCodigo = findViewById(R.id.etCodigo);
        etUsuario = findViewById(R.id.etUsuario);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnAtras = findViewById(R.id.btnAtras); // Inicializa el botón "Atrás"

        db = new DatabaseHelper(this);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String apellidos = etApellidos.getText().toString();
                String dni = etDni.getText().toString();
                String carrera = etCarrera.getText().toString();
                String codigo = etCodigo.getText().toString();
                String usuario = etUsuario.getText().toString();

                // Verificar que todos los campos estén llenos
                if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() ||
                        carrera.isEmpty() || codigo.isEmpty() || usuario.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                    return; // No continuar si hay algún campo vacío
                }

                Estudiante estudiante = new Estudiante(nombre, apellidos, dni, carrera, codigo, usuario);

                boolean insertado = db.agregarEstudiante(estudiante);

                if (insertado) {
                    Toast.makeText(RegistroActivity.this, "Estudiante registrado exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Volver al login
                } else {
                    Toast.makeText(RegistroActivity.this, "Error al registrar estudiante", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Configurar el botón "Atrás" para regresar a la actividad de login
        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });
    }
}
