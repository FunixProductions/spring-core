package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public abstract class PDFGeneratorInvoice extends PDFGeneratorWithHeaderAndFooter {

    private final List<InvoiceItem> invoiceItems;

    @NonNull
    private final PDFCompanyData clientData;

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  @NonNull List<InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  @NonNull File headerLogoFile,
                                  @NonNull List<InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData, headerLogoFile);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  byte[] imageByteArray,
                                  @NonNull String imageName,
                                  @NonNull List<InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData, imageByteArray, imageName);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

}
