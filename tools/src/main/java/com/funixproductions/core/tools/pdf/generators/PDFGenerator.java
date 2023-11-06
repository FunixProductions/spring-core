package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.Closeable;
import java.io.IOException;

/**
 * Abstract class for PDF generators.
 */
@Getter(value = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class PDFGenerator implements Closeable {

    /**
     * The name of the PDF file.
     */
    private final String pdfName;

    /**
     * The PDF document.
     */
    private final PDDocument pdfDocument = new PDDocument();

    /**
     * Generates the PDF document.
     * @throws ApiException if an error occurs
     */
    abstract void generatePDF() throws ApiException;

    /**
     * Creates a new page in the PDF document.
     * @return the new page
     */
    final PDPage newPage() {
        final PDPage pdPage = new PDPage();

        pdfDocument.addPage(pdPage);
        return pdPage;
    }

    final

    /**
     * Closes the PDF document. And free memory.
     * @throws IOException if an error occurs
     */
    @Override
    public final void close() throws IOException {
        this.pdfDocument.close();
    }
}
