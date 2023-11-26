package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import lombok.NonNull;

import java.io.File;
import java.util.List;

public abstract class PDFGeneratorInvoice extends PDFGeneratorWithHeaderAndFooter {

    private static final float ROW_HEIGHT = 20;

    private static final float ROW_ITEM_ID_WIDTH = 40;
    private static final float ROW_ITEM_NAME_WIDTH = 100;
    private static final float ROW_ITEM_DESCRIPTION_WIDTH = 200;
    private static final float ROW_ITEM_AMOUNT_COUNT_WIDTH = 40;
    private static final float ROW_ITEM_UNIT_PRICE_WIDTH = 60;
    private static final float ROW_ITEM_TOTAL_PRICE_WIDTH = 60;
    private static final float TABLE_WIDTH = 500;

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

    public final void init() throws ApiException {
        newPage();
        drawTable();
    }

    private void drawTable() throws ApiException {
        final int rows = this.invoiceItems.size();

        try {
            contentStream.setLineWidth(0.5f);
            contentStream.setFont(DEFAULT_FONT, 8);

            drawTabHeader();

            InvoiceItem item;
            float rowHeight;
            for (int i = 0; i < rows; ++i) {
                item = this.invoiceItems.get(i);
                rowHeight = getRowHeightFromItem(item);

                drawRectLine(rowHeight);
                drawContentRaw(i, item, rowHeight);
                downCursor(rowHeight);
            }
        } catch (Exception e) {
            throw new ApiException("Impossible de créer le tableau de la facture.", e);
        }
    }

    private void drawTabHeader() throws ApiException {
        drawRectLine(ROW_HEIGHT);
        drawContentRaw(
                "N",
                "Produit",
                "Description",
                "Quantité",
                "Prix Unitaire (HT)",
                "Prix Total (HT)",
                ROW_HEIGHT
        );
        downCursor(ROW_HEIGHT);
    }

    private float getRowHeightFromItem(final InvoiceItem item) {
        return 10;
    }

    private void drawRectLine(final float rowHeight) {
        try {
            final float x = super.margin;
            final List<Float> margins = List.of(
                    ROW_ITEM_ID_WIDTH,
                    ROW_ITEM_NAME_WIDTH,
                    ROW_ITEM_DESCRIPTION_WIDTH,
                    ROW_ITEM_AMOUNT_COUNT_WIDTH,
                    ROW_ITEM_UNIT_PRICE_WIDTH,
                    ROW_ITEM_TOTAL_PRICE_WIDTH
            );
            float marginToAdd = 0;

            contentStream.moveTo(x, super.yPosition);
            contentStream.lineTo(x + TABLE_WIDTH, super.yPosition);
            contentStream.lineTo(x + TABLE_WIDTH, super.yPosition - rowHeight);
            contentStream.lineTo(x, super.yPosition - rowHeight);
            contentStream.lineTo(x, super.yPosition);

            contentStream.moveTo(x, super.yPosition - rowHeight);
            contentStream.lineTo(x + TABLE_WIDTH, super.yPosition - rowHeight);

            for (float margin : margins) {
                contentStream.moveTo(x + marginToAdd + margin, super.yPosition);
                contentStream.lineTo(x + marginToAdd + margin, super.yPosition - rowHeight);
                marginToAdd += margin;
            }

            contentStream.stroke();
        } catch (Exception e) {
            throw new ApiException("Impossible d'initialiser la facture, erreur lors de la mise en place du haut de tableau.", e);
        }
    }

    private void drawContentRaw(final int rowNumber, final InvoiceItem item, final float rowHeight) throws ApiException {
        try {
            drawContentRaw(
                    Integer.toString(rowNumber),
                    item.getInvoiceItemName(),
                    item.getInvoiceItemDescription(),
                    Integer.toString(item.getInvoiceItemQuantity()),
                    "€ " + String.format("%.4f", item.getInvoiceItemPrice()),
                    "€ " + String.format("%.4f", item.getInvoiceItemQuantity() * item.getInvoiceItemPrice()),
                    rowHeight
            );
        } catch (Exception e) {
            throw new ApiException("Une erreur est survenue lors de l'ajout de données à une ligne de la facture.", e);
        }
    }

    private void drawContentRaw(@NonNull final String rowNumber,
                                @NonNull final String name,
                                @NonNull final String description,
                                @NonNull final String quantity,
                                @NonNull final String price,
                                @NonNull final String totalPrice,
                                final float rowHeight) throws ApiException {
        final float x = super.margin + 2;
        final float y = super.yPosition - (rowHeight / 2 + rowHeight / 3);

        final List<Float> margins = List.of(
                ROW_ITEM_ID_WIDTH,
                ROW_ITEM_NAME_WIDTH,
                ROW_ITEM_DESCRIPTION_WIDTH,
                ROW_ITEM_AMOUNT_COUNT_WIDTH,
                ROW_ITEM_UNIT_PRICE_WIDTH,
                ROW_ITEM_TOTAL_PRICE_WIDTH
        );
        final List<String> datas = List.of(
                rowNumber,
                name,
                description,
                quantity,
                price,
                totalPrice
        );
        float marginToAdd = 0;
        int loopSize = margins.size();
        int i = 0;

        while (i < loopSize) {
            drawCell(x + marginToAdd, y, datas.get(i));
            marginToAdd += margins.get(i);
            ++i;
        }
    }

    private void downCursor(final float yToRemove) {
        yPosition -= yToRemove;
        if (yPosition <= margin) {
            newPage();
        }
    }

    private void drawCell(final float x, final float y, final String text) throws ApiException {
        try {
            contentStream.beginText();
            contentStream.newLineAtOffset(x, y);
            contentStream.showText(text);
            contentStream.endText();
        } catch (Exception e) {
            throw new ApiException("Erreur lors de l'écriture dans le tableau de la facture.", e);
        }
    }

}
