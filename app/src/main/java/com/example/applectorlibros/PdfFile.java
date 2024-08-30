package com.example.applectorlibros;

public class PdfFile {
    private String name;
    private String path;

    public PdfFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}