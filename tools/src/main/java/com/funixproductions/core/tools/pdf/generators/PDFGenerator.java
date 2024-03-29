package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

/**
 * Abstract class for PDF generators.
 */
@Slf4j(topic = "PDFGenerator")
public abstract class PDFGenerator implements Closeable {

    public static final PDFont DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    public static final float DEFAULT_FONT_SIZE = 12;
    public static final Color DEFAULT_FONT_COLOR = Color.BLACK;

    /**
     * The name of the PDF file.
     */
    protected final String pdfName;

    /**
     * The PDF document.
     */
    protected final PDDocument pdfDocument = new PDDocument();

    /**
     * The current page of the PDF document editing.
     */
    protected PDPage currentPage;

    /**
     * The current page content stream.
     */
    protected PDPageContentStream contentStream;

    /**
     * The font size of the PDF document.
     */
    protected float topMargin = 50;

    /**
     * The spaces between break lines.
     */
    protected float lineSpacing = 20;

    /**
     * The margin of the PDF document.
     */
    protected float margin = 50;

    /**
     * The write cursor location, reset at new page.
     */
    protected float yPosition = topMargin;

    /**
     * Constructor.
     * @param pdfName the name of the PDF file
     */
    protected PDFGenerator(@NonNull final String pdfName) {
        final String finalName;

        if (pdfName.endsWith(".pdf")) {
            finalName = pdfName.substring(0, pdfName.length() - 4);
        } else {
            finalName = pdfName;
        }

        this.pdfName = finalName;
    }

    /**
     * Writes a text in the PDF document. Used to write paragraphs.
     * @param lines the lines to write
     * @throws ApiException if an error occurs
     */
    public final void writePlainText(@NonNull final Collection<PDFLine> lines) throws ApiException {
        if (contentStream == null || currentPage == null) {
            log.error("Le PDF {} n'a pas été initialisé. Vous devez créer la première page.", this.pdfName);
            throw new ApiException("Le PDF " + this.pdfName + " n'a pas été initialisé.");
        }

        try {
            float fontSize;
            PDFont font;
            String[] words;
            StringBuilder lineBuilder;
            float pageWidth;
            float wordWidth;
            Color fontColor;

            for (final PDFLine line : lines) {
                if (line == null) continue;
                fontSize = line.getFontSize();
                font = line.getFont();
                fontColor = line.getFontColor();

                contentStream.setFont(font, fontSize);
                contentStream.setNonStrokingColor(fontColor);

                if (yPosition <= margin) {
                    newPage();
                    contentStream.setFont(font, fontSize);
                    contentStream.setNonStrokingColor(fontColor);
                }

                words = line.getText().split(" ");
                lineBuilder = new StringBuilder();
                pageWidth = currentPage.getMediaBox().getWidth();

                for (final String word : words) {
                    wordWidth = font.getStringWidth(lineBuilder + " " + word) / 1000 * fontSize;

                    if (wordWidth < (pageWidth - (margin + 20))) {
                        if (!lineBuilder.isEmpty()) {
                            lineBuilder.append(" ");
                        }
                        lineBuilder.append(word);
                    } else {
                        this.writeLine(lineBuilder.toString());
                        lineBuilder = new StringBuilder(word);

                        if (yPosition <= margin) {
                            newPage();
                            contentStream.setFont(font, fontSize);
                            contentStream.setNonStrokingColor(fontColor);
                        }
                    }
                }

                this.writeLine(lineBuilder.toString());
            }
        } catch (final Exception e) {
            log.error("Une erreur est survenue lors de l'écriture dans le PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de l'écriture dans le PDF " + this.pdfName + ".", e);
        }
    }

    private void writeLine(final String line) throws ApiException {
        try {
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText(line);
            contentStream.endText();
            yPosition -= lineSpacing;
        } catch (Exception e) {
            log.error("Une erreur est survenue lors de l'écriture dans le PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de l'écriture dans le PDF " + this.pdfName + ".", e);
        }
    }

    /**
     * Creates a new page in the PDF document.
     */
    public void newPage() throws ApiException {
        try {
            if (contentStream != null) {
                contentStream.close();
            }

            this.currentPage = new PDPage();
            contentStream = new PDPageContentStream(pdfDocument, currentPage);

            yPosition = currentPage.getMediaBox().getHeight() - topMargin;
            pdfDocument.addPage(currentPage);
            writePageNumber();
        } catch (Exception e) {
            log.error("Une erreur est survenue lors de la création d'une nouvelle page dans le PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de la création d'une nouvelle page dans le PDF " + this.pdfName + ".", e);
        }
    }

    private void writePageNumber() throws ApiException {
        try {
            contentStream.beginText();
            contentStream.setFont(DEFAULT_FONT, DEFAULT_FONT_SIZE);
            contentStream.newLineAtOffset(currentPage.getMediaBox().getWidth() - 40, 20);
            contentStream.showText(String.valueOf(pdfDocument.getNumberOfPages()));
            contentStream.endText();
        } catch (final Exception e) {
            log.error("Une erreur est survenue lors de l'écriture du numéro de page dans le PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de l'écriture du numéro de page dans le PDF " + this.pdfName + ".", e);
        }
    }

    /**
     * Closes the PDF document. And free memory.
     * @throws ApiException if an error occurs
     */
    @Override
    public final void close() throws ApiException {
        try {
            if (this.contentStream != null)
                this.contentStream.close();

            this.pdfDocument.close();
        } catch (final Exception e) {
            log.error("Une erreur est survenue lors de la fermeture du PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de la fermeture du PDF " + this.pdfName + ".", e);
        }
    }

    /**
     * Generates the PDF file.
     * @param folderToStore the folder to store the PDF file
     * @return the PDF file
     * @throws ApiException if an error occurs
     */
    public final File generatePDF(@NonNull final File folderToStore) throws ApiException {
        try {
            if (this.contentStream != null) {
                this.contentStream.close();
            }
        } catch (Exception closeStreamException) {
            log.error("Une erreur est survenue lors de la fermeture du stream PDF {} .", this.pdfName, closeStreamException);
            throw new ApiException("Une erreur est survenue lors de la fermeture du PDF " + this.pdfName + ".", closeStreamException);
        }

        if (!folderToStore.exists()) {
            log.error("Le dossier de destination du PDF {} n'existe pas.", this.pdfName);
            throw new ApiException("Le dossier de destination du PDF " + this.pdfName + " n'existe pas.");
        }

        try {
            final String finalName = this.pdfName + "_" +
                    getTodayDateForPdfFinalName() + "_" +
                    UUID.randomUUID() + (this.pdfName.endsWith(".pdf") ? "" : ".pdf");

            final File pdfFile = new File(folderToStore, finalName);
            pdfDocument.save(pdfFile);
            return pdfFile;
        } catch (final Exception e) {
            log.error("Une erreur est survenue lors de la génération du PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de la génération du PDF " + this.pdfName + ".", e);
        }
    }

    private String getTodayDateForPdfFinalName() {
        return Instant.now().toString().substring(0, 10);
    }
}
