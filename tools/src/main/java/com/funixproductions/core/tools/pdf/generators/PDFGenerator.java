package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Abstract class for PDF generators.
 */
@Slf4j(topic = "PDFGenerator")
public abstract class PDFGenerator implements Closeable {

    public static final PDFont DEFAULT_FONT = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    public static final float DEFAULT_FONT_SIZE = 12;

    /**
     * The name of the PDF file.
     */
    private final String pdfName;

    /**
     * The PDF document.
     */
    private final PDDocument pdfDocument = new PDDocument();

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
        newPage();
    }

    /**
     * Writes a text in the PDF document. Used to write paragraphs.
     * @param lines the lines to write
     * @param fontSize the font size
     * @param font the font
     * @throws ApiException if an error occurs
     */
    public final void writePlainText(@NonNull final String[] lines,
                                     final float fontSize,
                                     @NonNull final PDFont font) throws ApiException {
        try {
            contentStream.setFont(font, fontSize);

            for (String line : lines) {
                if (yPosition <= margin) {
                    newPage();
                    contentStream.setFont(font, fontSize);
                }

                final String[] words = line.split(" ");
                StringBuilder lineBuilder = new StringBuilder();
                float pageWidth = currentPage.getMediaBox().getWidth();
                float wordWidth;

                for (String word : words) {
                    wordWidth = font.getStringWidth(lineBuilder + " " + word) / 1000 * fontSize;

                    if (wordWidth < (pageWidth - (margin + 20))) {
                        if (!lineBuilder.isEmpty()) {
                            lineBuilder.append(" ");
                        }
                        lineBuilder.append(word);
                    } else {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, yPosition);
                        contentStream.showText(lineBuilder.toString());
                        contentStream.endText();
                        yPosition -= lineSpacing;
                        lineBuilder = new StringBuilder(word);

                        if (yPosition <= margin) {
                            newPage();
                            contentStream.setFont(font, fontSize);
                        }
                    }
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(lineBuilder.toString());
                contentStream.endText();
                yPosition -= lineSpacing;
            }
        } catch (final Exception e) {
            log.error("Une erreur est survenue lors de l'écriture dans le PDF {} .", this.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de l'écriture dans le PDF " + this.pdfName + ".", e);
        } finally {
            if (contentStream != null) {
                try {
                    contentStream.close();
                } catch (final Exception e) {
                    log.error("Une erreur est survenue lors de la fermeture du PDF {} .", this.pdfName, e);
                }
            }
        }
    }

    /**
     * Creates a new page in the PDF document.
     */
    protected final void newPage() throws ApiException {
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
     * @throws IOException if an error occurs
     */
    @Override
    public void close() throws IOException {
        this.pdfDocument.close();
    }

    /**
     * Generates the PDF file.
     * @param folderToStore the folder to store the PDF file
     * @return the PDF file
     * @throws ApiException if an error occurs
     */
    public final File generatePDF(@NonNull final File folderToStore) throws ApiException {
        if (!folderToStore.exists()) {
            log.error("Le dossier de destination du PDF {} n'existe pas.", this.pdfName);
            throw new ApiException("Le dossier de destination du PDF " + this.pdfName + " n'existe pas.");
        }

        try {
            final String pdfName = this.pdfName + "_" + getTodayDateForPdfFinalName() + "_" + UUID.randomUUID() + (this.pdfName.endsWith(".pdf") ? "" : ".pdf");

            final File pdfFile = new File(folderToStore, pdfName);
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
