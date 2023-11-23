package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import lombok.NonNull;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.File;
import java.io.IOException;
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

    public final void init() throws IOException {
        newPage();
        drawTable();
    }

    private void drawTable() throws IOException {
        float x = super.margin;
        float tableWidth = 500;
        int rows = this.invoiceItems.size();
        float tableHeight = 20f * rows;
        float rowHeight = 20f;
        float tableYLength = rowHeight * (float) (rows + 1);
        float tableXLength = 495f;

        contentStream.setLineWidth(1f);
        contentStream.moveTo(x, super.yPosition);
        contentStream.lineTo(x + tableWidth, super.yPosition);
        contentStream.lineTo(x + tableWidth, super.yPosition - tableHeight);
        contentStream.lineTo(x, super.yPosition - tableHeight);
        contentStream.lineTo(x, super.yPosition);

        contentStream.moveTo(x, super.yPosition - rowHeight);
        contentStream.lineTo(x + tableWidth, super.yPosition - rowHeight);

        for (int i = 0; i <= rows; ++i) {
            contentStream.moveTo(x, yPosition - (i * rowHeight));
            contentStream.lineTo(x + tableWidth, yPosition - (i * rowHeight));
        }

        contentStream.stroke();

        float xPosition = x + margin;
        for (InvoiceItem item : this.invoiceItems) {
            drawCell(contentStream, xPosition, yPosition, String.valueOf(item.getInvoiceItemQuantity()));
            xPosition += 70f;
            drawCell(contentStream, xPosition, yPosition, item.getInvoiceItemName());
            xPosition += 200f;
            drawCell(contentStream, xPosition, yPosition, item.getInvoiceItemPrice() + "€");
            xPosition += 90f;
            drawCell(contentStream, xPosition, yPosition, (item.getInvoiceItemQuantity() * item.getInvoiceItemPrice()) + "€");

            yPosition -= rowHeight;
            if (yPosition <= margin) {
                newPage();
            }

            xPosition = x + margin;
        }
    }

    private void drawCell(PDPageContentStream contentStream, float x, float y, String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(text);
        contentStream.endText();
    }

}
