package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.lang.Nullable;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "PDFGeneratorHeaderFooter")
public abstract class PDFGeneratorWithHeaderAndFooter extends PDFGenerator {

    private static final String ERROR_SLF4J = "An error occurred while adding the header logo to the pdf {}";
    private static final String ERROR_API = "Une erreur est survenue lors de l'ajout du logo en header sur le pdf ";

    @Nullable
    private final PDFCompanyData companyData;

    private PDImageXObject headerLogo;

    /**
     * Constructor.
     *
     * @param pdfName the name of the PDF file
     */
    protected PDFGeneratorWithHeaderAndFooter(@NonNull String pdfName, @Nullable PDFCompanyData companyData) {
        super(pdfName);
        this.companyData = companyData;
    }

    /**
     * Constructor.
     * @param pdfName the name of the PDF file
     * @param companyData the company data to add on the header or footer
     * @param headerLogoFile the header logo file
     */
    protected PDFGeneratorWithHeaderAndFooter(@NonNull String pdfName, @Nullable PDFCompanyData companyData, @NonNull File headerLogoFile) {
        super(pdfName);
        this.companyData = companyData;

        try {
            if (!headerLogoFile.exists()) {
                throw new ApiException("Header logo file does not exist: " + headerLogoFile.getAbsolutePath());
            }
            this.headerLogo = PDImageXObject.createFromFile(headerLogoFile.getPath(), super.pdfDocument);
        } catch (final Exception e) {
            log.error(ERROR_SLF4J, super.pdfName, e);
            throw new ApiException(ERROR_API + super.pdfName, e);
        }
    }

    /**
     * Constructor.
     * @param pdfName the name of the PDF file
     * @param companyData the company data to add on the header or footer
     * @param imageByteArray the header logo byte array
     * @param imageName the header logo name
     */
    protected PDFGeneratorWithHeaderAndFooter(@NonNull String pdfName, @Nullable PDFCompanyData companyData, byte[] imageByteArray, @NonNull String imageName) {
        super(pdfName);
        this.companyData = companyData;

        try {
            this.headerLogo = PDImageXObject.createFromByteArray(super.pdfDocument, imageByteArray, imageName);
        } catch (final Exception e) {
            log.error(ERROR_SLF4J, super.pdfName, e);
            throw new ApiException(ERROR_API + super.pdfName, e);
        }
    }

    @Override
    public final void newPage() throws ApiException {
        super.newPage();
        this.setupHeader();
        this.setupFooter();
    }

    /**
     * Sets up the header.
     * @throws ApiException on error write pdf
     */
    protected void setupHeader() throws ApiException {
        setCompanyInfosHeader(companyData);
        setHeaderLogoOnNewPage();
    }

    /**
     * Sets up the footer.
     * @throws ApiException on error write pdf
     */
    protected void setupFooter() throws ApiException {
    }

    private void setHeaderLogoOnNewPage() throws ApiException {
        if (this.headerLogo != null) {
            try {
                final float pageHeight = super.currentPage.getMediaBox().getHeight();
                final float pageWidth = super.currentPage.getMediaBox().getWidth();

                super.contentStream.drawImage(
                        this.headerLogo,
                        pageWidth - (this.headerLogo.getWidth()) - super.margin,
                        pageHeight - (this.headerLogo.getHeight()) - super.margin
                );
            } catch (final Exception e) {
                log.error(ERROR_SLF4J, super.pdfName, e);
                throw new ApiException(ERROR_API + super.pdfName, e);
            }
        }
    }

    protected void setCompanyInfosHeader(@Nullable final PDFCompanyData companyData) throws ApiException {
        if (companyData == null) return;
        final float baseSpacing = super.lineSpacing;
        final PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        super.lineSpacing = 15;

        final List<PDFLine> lines = new ArrayList<>();
        if (companyData.getName() != null) {
            final PDFLine pdfLine = new PDFLine(companyData.getName());
            pdfLine.setFontColor(Color.GRAY);
            pdfLine.setFont(boldFont);

            lines.add(pdfLine);
        }
        if (companyData.getAddress() != null) {
            final PDFLine pdfLine = new PDFLine(companyData.getAddress());
            pdfLine.setFontColor(Color.GRAY);
            pdfLine.setFont(boldFont);

            lines.add(pdfLine);
        }
        if (companyData.getZipCode() != null && companyData.getCity() != null) {
            final PDFLine pdfLine = new PDFLine(companyData.getZipCode() + " " + companyData.getCity());
            pdfLine.setFontColor(Color.GRAY);
            pdfLine.setFont(boldFont);

            lines.add(pdfLine);
        }
        if (companyData.getPhone() != null) {
            final PDFLine pdfLine = new PDFLine(companyData.getPhone());
            pdfLine.setFontColor(Color.GRAY);

            lines.add(pdfLine);
        }
        if (companyData.getEmail() != null) {
            final PDFLine pdfLine = new PDFLine(companyData.getEmail());
            pdfLine.setFontColor(Color.GRAY);

            lines.add(pdfLine);
        }
        if (companyData.getWebsite() != null) {
            final PDFLine pdfLine = new PDFLine("Site internet : " + companyData.getWebsite());
            pdfLine.setFontColor(Color.GRAY);

            lines.add(pdfLine);
        }
        if (companyData.getSiret() != null) {
            final PDFLine pdfLine = new PDFLine("SIRET : " + companyData.getSiret());
            pdfLine.setFontColor(Color.GRAY);

            lines.add(pdfLine);
        }
        if (companyData.getTvaCode() != null) {
            final PDFLine pdfLine = new PDFLine("N° TVA : " + companyData.getTvaCode());
            pdfLine.setFontColor(Color.GRAY);

            lines.add(pdfLine);
        }

        super.writePlainText(lines);
        super.lineSpacing = baseSpacing;
        super.yPosition -= super.lineSpacing * 2;
    }
}
