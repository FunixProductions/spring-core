package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.InvoiceBillingTotal;
import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import com.funixproductions.core.tools.pdf.tools.VATInformation;
import com.funixproductions.core.tools.time.TimeUtils;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PDFGeneratorInvoice extends PDFGeneratorWithHeaderAndFooter {

    private static final float ROW_HEIGHT_TAB_HEADER = 20;
    private static final float ROW_HEIGHT = 10;
    private static final float FONT_SIZE = 8;

    private static final float ROW_ITEM_ID_WIDTH = 20;
    private static final float ROW_ITEM_NAME_WIDTH = 100;
    private static final float ROW_ITEM_DESCRIPTION_WIDTH = 190;
    private static final float ROW_ITEM_AMOUNT_COUNT_WIDTH = 40;
    private static final float ROW_ITEM_UNIT_PRICE_WIDTH = 75;
    private static final float ROW_ITEM_TOTAL_PRICE_WIDTH = 75;
    private static final float TABLE_WIDTH = 500;

    private static final String DEVISE_LOGO = "€";
    private static final String DEVISE_NAME = "Euros";

    private final List<? extends InvoiceItem> invoiceItems;

    /**
     * Informations sur le client.
     */
    @NonNull
    private final PDFCompanyData clientData;

    /**
     * Description de la facture.
     */
    @Setter
    private String invoiceDescription;

    /**
     * Numéro de la facture.
     */
    @Setter
    private String invoiceNumber;

    /**
     * Méthode de paiement.
     */
    @Setter
    private String paymentMethod;

    /**
     * Lien vers les conditions de vente
     */
    @Setter
    private String cgvUrl;

    /**
     * % de réduction sur la somme finale
     */
    @Setter
    private Double percentageDiscount;

    /**
     * Informations sur la TVA payée par le client.
     */
    @Setter
    private VATInformation vatInformation;

    @Getter
    private InvoiceBillingTotal billingTotalInformation;

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  @NonNull List<? extends InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  @NonNull File headerLogoFile,
                                  @NonNull List<? extends InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData, headerLogoFile);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

    protected PDFGeneratorInvoice(@NonNull String pdfName,
                                  @NonNull PDFCompanyData companyData,
                                  byte[] imageByteArray,
                                  @NonNull String imageName,
                                  @NonNull List<? extends InvoiceItem> invoiceItems,
                                  @NonNull PDFCompanyData clientData) {
        super(pdfName, companyData, imageByteArray, imageName);
        this.invoiceItems = invoiceItems;
        this.clientData = clientData;
    }

    @Override
    protected void setupHeader() throws ApiException {
        super.setupHeader();
        super.yPosition -= super.lineSpacing;

        if (this.invoiceNumber != null) {
            super.writePlainText(Collections.singleton(new PDFLine(
                    "Facture n° " + this.invoiceNumber
            )));
        }
        super.yPosition -= super.lineSpacing * 2;
    }

    /**
     * Initialisation de la facture. Vérifier que tous les setters sont bien remplis pour ne pas avoir de soucis légaux !
     * @throws ApiException en cas d'erreur d'init
     */
    public final void init() throws ApiException {
        newPage();

        try {
            contentStream.moveTo(margin, super.yPosition + super.lineSpacing * 2);

            super.setCompanyInfosHeader(this.clientData);
            writeInvoiceInfos();

            contentStream.moveTo(margin, super.yPosition + super.lineSpacing * 2);

            super.yPosition -= super.lineSpacing * 2;

            if (!Strings.isNullOrEmpty(this.invoiceDescription)) {
                this.writePlainText(Collections.singleton(new PDFLine(this.invoiceDescription)));
            }

            newPage();
            drawTable();
            super.yPosition -= super.lineSpacing * 2;

            checkBeforeWriteHeightAvailable(calculateTotalDataHeight());
            drawTotalData();
        } catch (IOException e) {
            throw new ApiException("Erreur lors de la création de la ligne séparatrice sur la facture.", e);
        }
    }

    private void writeInvoiceInfos() throws ApiException {
        final Instant now = Instant.now();
        final List<PDFLine> lines = new ArrayList<>();

        if (this.invoiceNumber != null) {
            lines.add(createInvoiceInfoLine("Facture n° " + this.invoiceNumber));
        }

        final String dateBilling = TimeUtils.getFrenchDateFromInstant(now);
        lines.addAll(List.of(
                createInvoiceInfoLine("Date de la facture : " + dateBilling),
                createInvoiceInfoLine("Date de payment : " + dateBilling)
        ));

        if (this.paymentMethod != null) {
            lines.add(createInvoiceInfoLine("Méthode de paiement : " + this.paymentMethod));
        }

        if (this.cgvUrl != null) {
            lines.add(createInvoiceInfoLine("Conditions générales de vente : " + this.cgvUrl));
        }

        super.writePlainText(lines);
    }

    private PDFLine createInvoiceInfoLine(final String text) {
        return new PDFLine(text, DEFAULT_FONT_SIZE - 3, DEFAULT_FONT, Color.GRAY);
    }

    private float calculateTotalDataHeight() throws ApiException {
        float heightNeeded = DEFAULT_FONT_SIZE + 1 + super.lineSpacing;

        if (this.percentageDiscount != null) {
            heightNeeded += DEFAULT_FONT_SIZE * 2 + super.lineSpacing * 2;
        }

        return heightNeeded + DEFAULT_FONT_SIZE * 3 + super.lineSpacing * 3;
    }

    private void drawTotalData() throws ApiException {
        this.billingTotalInformation = new InvoiceBillingTotal(
                this.invoiceItems,
                this.percentageDiscount,
                this.vatInformation
        );

        final List<PDFLine> toWrite = new ArrayList<>(List.of(new PDFLine(
                "Total de la facture :",
                DEFAULT_FONT_SIZE + 1,
                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                DEFAULT_FONT_COLOR
        )));

        if (this.billingTotalInformation.getDiscountAmount() > 0 && this.percentageDiscount != null) {
            toWrite.addAll(List.of(
                            new PDFLine("Une réduction de " + formatPrice(this.percentageDiscount) + " % est appliquée à cette facture.", DEFAULT_FONT_SIZE, DEFAULT_FONT, Color.GRAY),
                            new PDFLine(String.format("Remise : %s %s", DEVISE_LOGO, formatPrice(this.billingTotalInformation.getDiscountAmount())))
                    )
            );
        }

        final String vatAmount = this.vatInformation == null ? "0" : Double.toString(this.vatInformation.getVatRate());
        toWrite.addAll(List.of(
                new PDFLine(String.format("Total HT : %s %s", DEVISE_LOGO, formatPrice(this.billingTotalInformation.getTotalHtPrice()))),
                new PDFLine("TVA " + vatAmount + " % : " + DEVISE_LOGO + ' ' + formatPrice(this.billingTotalInformation.getVatAmount())),
                new PDFLine("Total TTC en " + DEVISE_NAME + " : " + DEVISE_LOGO + ' ' + formatPrice(this.billingTotalInformation.getTotalTtcPrice()))
        ));

        super.writePlainText(toWrite);
    }

    private void drawTable() throws ApiException {
        final int rows = this.invoiceItems.size();

        try {
            contentStream.setLineWidth(0.5f);
            contentStream.setNonStrokingColor(DEFAULT_FONT_COLOR);
            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), FONT_SIZE);

            drawTabHeader();

            contentStream.setFont(DEFAULT_FONT, FONT_SIZE);

            InvoiceItem item;
            float rowHeight;
            for (int i = 0; i < rows; ++i) {
                item = this.invoiceItems.get(i);
                rowHeight = getRowHeightFromItem(item);

                checkBeforeWriteHeightAvailable(rowHeight);
                drawRectLine(rowHeight);
                drawContentRaw(i + 1, item, rowHeight);
                downCursor(rowHeight);
            }
        } catch (Exception e) {
            throw new ApiException("Impossible de créer le tableau de la facture.", e);
        }
    }

    private void drawTabHeader() throws ApiException {
        drawRectLine(ROW_HEIGHT_TAB_HEADER);
        drawContentRaw(
                "N°",
                "Produit",
                "Description",
                "Quantité",
                "Prix Unitaire (HT)",
                "Prix Total (HT)",
                ROW_HEIGHT_TAB_HEADER
        );
        downCursor(ROW_HEIGHT_TAB_HEADER);
    }

    private float getRowHeightFromItem(final InvoiceItem item) {
        float finalHeight = ROW_HEIGHT;
        float lastHeight = countHeightForString(item.getInvoiceItemName(), ROW_ITEM_NAME_WIDTH);

        if (lastHeight > finalHeight) {
            finalHeight = lastHeight;
        }
        lastHeight = countHeightForString(item.getInvoiceItemDescription(), ROW_ITEM_DESCRIPTION_WIDTH);
        if (lastHeight > finalHeight) {
            finalHeight = lastHeight;
        }

        return finalHeight;
    }

    private float countHeightForString(final String line, final float maxLineWidth) throws ApiException {
        float height = ROW_HEIGHT;
        final String[] words = line.split(" ");
        StringBuilder lineBuilder = new StringBuilder();
        float wordWidth;

        try {
            for (final String word : words) {
                wordWidth = DEFAULT_FONT.getStringWidth(lineBuilder + " " + word) / 1000 * FONT_SIZE;

                if (wordWidth < (maxLineWidth - 2)) {
                    if (!lineBuilder.isEmpty()) {
                        lineBuilder.append(" ");
                    }
                    lineBuilder.append(word);
                } else {
                    height += ROW_HEIGHT + super.lineSpacing / 2;
                    lineBuilder = new StringBuilder(word);
                }
            }

            return height;
        } catch (Exception e) {
            throw new ApiException("Une erreur est survenue lors de la génération de la facture. Erreur de calcul de la taille d'une ligne dans la facture.", e);
        }
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
                    DEVISE_LOGO + ' ' + formatPrice(item.getInvoiceItemPrice()),
                    DEVISE_LOGO + ' ' + formatPrice(item.getInvoiceItemQuantity() * item.getInvoiceItemPrice()),
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
        float actualMargin;

        while (i < loopSize) {
            actualMargin = margins.get(i);

            drawCell(x + marginToAdd, y, datas.get(i), actualMargin);
            marginToAdd += actualMargin;
            ++i;
        }
    }

    private void downCursor(final float yToRemove) throws ApiException {
        try {
            yPosition -= yToRemove;
            if (yPosition <= margin) {
                newPage();
                contentStream.setFont(DEFAULT_FONT, FONT_SIZE);
                contentStream.setNonStrokingColor(DEFAULT_FONT_COLOR);
                contentStream.setLineWidth(0.5f);
            }
        } catch (IOException e) {
            throw new ApiException("Une erreur est survenue lors de la génération de la facture. Ajout de page erreur.", e);
        }
    }

    private void checkBeforeWriteHeightAvailable(final float yToDown) throws ApiException {
        try {
            if (yPosition - yToDown <= margin) {
                newPage();
                contentStream.setFont(DEFAULT_FONT, FONT_SIZE);
                contentStream.setNonStrokingColor(DEFAULT_FONT_COLOR);
                contentStream.setLineWidth(0.5f);
            }
        } catch (IOException e) {
            throw new ApiException("Une erreur est survenue lors de la génération de la facture. Ajout de page erreur.", e);
        }
    }

    private void drawCell(final float x, final float y, final String text, final float maxWidth) throws ApiException {
        try {
            final List<String> toWrite = new ArrayList<>();
            final String[] words = text.split(" ");
            StringBuilder lineBuilder = new StringBuilder();
            float wordWidth;
            float actualY = y;

            for (final String word : words) {
                wordWidth = DEFAULT_FONT.getStringWidth(lineBuilder + " " + word) / 1000 * FONT_SIZE;

                if (wordWidth < maxWidth - 2) {
                    if (!lineBuilder.isEmpty()) {
                        lineBuilder.append(" ");
                    }
                    lineBuilder.append(word);
                } else {
                    toWrite.add(lineBuilder.toString());
                    lineBuilder = new StringBuilder(word);
                    actualY += ROW_HEIGHT;
                }
            }

            toWrite.add(lineBuilder.toString());

            for (final String line : toWrite) {
                this.writeLine(line, x, actualY);
                actualY -= ROW_HEIGHT;
            }

        } catch (Exception e) {
            throw new ApiException("Erreur lors de l'écriture dans le tableau de la facture.", e);
        }
    }

    private void writeLine(final String line, final float x, final float y) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(line);
        contentStream.endText();
    }

    private static String formatPrice(final double price) {
        return String.format("%.4f", price);
    }

}
