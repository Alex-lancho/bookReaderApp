package com.example.applectorlibros;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class PdfReaderActivity extends AppCompatActivity {

    private ImageView pdfImageView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;
    private ParcelFileDescriptor fileDescriptor;
    private Button nextPageButton, prevPageButton;
    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_reader);

        pdfImageView = findViewById(R.id.pdfImageView);
        nextPageButton = findViewById(R.id.nextPageButton);
        prevPageButton = findViewById(R.id.prevPageButton);
        Button backButton = findViewById(R.id.backButton);

        Intent intent = getIntent();
        String pdfPath = intent.getStringExtra("PDF_PATH");

        try {
            openPdfRenderer(pdfPath);
            showPage(currentPageIndex); // Muestra la primera página
        } catch (IOException e) {
            Toast.makeText(this, "Error al abrir el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        nextPageButton.setOnClickListener(v -> showPage(currentPageIndex + 1));
        prevPageButton.setOnClickListener(v -> showPage(currentPageIndex - 1));

        backButton.setOnClickListener(v -> finish());
    }

    private void openPdfRenderer(String pdfPath) throws IOException {
        File file = new File(pdfPath);
        if (!file.exists()) {
            Toast.makeText(this, "El archivo PDF no existe.", Toast.LENGTH_SHORT).show();
            return;
        }
        fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
    }

    private void showPage(int index) {
        if (pdfRenderer.getPageCount() <= index || index < 0) {
            Toast.makeText(this, "No hay más páginas.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cerrar la página actual si existe
        if (currentPage != null) {
            currentPage.close();
        }

        // Abrir la nueva página
        currentPageIndex = index;
        currentPage = pdfRenderer.openPage(index);

        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        pdfImageView.setImageBitmap(bitmap);

        // Habilitar o deshabilitar los botones dependiendo de la página actual
        prevPageButton.setEnabled(currentPageIndex > 0);
        nextPageButton.setEnabled(currentPageIndex < pdfRenderer.getPageCount() - 1);
    }

    @Override
    protected void onDestroy() {
        try {
            if (currentPage != null) {
                currentPage.close();
            }
            pdfRenderer.close();
            fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}