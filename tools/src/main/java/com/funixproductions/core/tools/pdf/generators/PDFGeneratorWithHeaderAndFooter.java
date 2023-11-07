package com.funixproductions.core.tools.pdf.generators;

import lombok.NonNull;

public abstract class PDFGeneratorWithHeaderAndFooter extends PDFGenerator {


    /**
     * Constructor.
     *
     * @param pdfName the name of the PDF file
     */
    protected PDFGeneratorWithHeaderAndFooter(@NonNull String pdfName) {
        super(pdfName);
    }
}
