package com.example.applectorlibros;

public class Estudiante {
    private String nombre;
    private String apellidos;
    private String dni;
    private String carrera;
    private String codigo;
    private String usuario;

    public Estudiante(String nombre, String apellidos, String dni, String carrera, String codigo, String usuario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.carrera = carrera;
        this.codigo = codigo;
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDni() {
        return dni;
    }

    public String getCarrera() {
        return carrera;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getUsuario() {
        return usuario;
    }
}
