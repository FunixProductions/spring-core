package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.lang.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Setter
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
    protected final void newPage() throws ApiException {
        super.newPage();
        this.setupHeader();
        this.setupFooter();
    }

    /**
     * Sets up the header.
     * @throws ApiException on error write pdf
     */
    protected void setupHeader() throws ApiException {
        setHeaderLogoOnNewPage();
        setCompanyInfosHeader();
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
                super.contentStream.drawImage(this.headerLogo, 50, 50);
                super.yPosition += this.headerLogo.getHeight() + super.lineSpacing;
            } catch (final Exception e) {
                log.error(ERROR_SLF4J, super.pdfName, e);
                throw new ApiException(ERROR_API + super.pdfName, e);
            }
        }
    }

    private void setCompanyInfosHeader() throws ApiException {
        if (this.companyData == null) return;

        final float initialMargin = super.margin;
        final float actualMargin;

        if (this.headerLogo != null) {
            actualMargin = super.margin + this.headerLogo.getWidth() + super.lineSpacing;
        } else {
            actualMargin = initialMargin;
        }

        final List<PDFLine> lines = new ArrayList<>();
        if (this.companyData.getName() != null) {
            lines.add(new PDFLine(this.companyData.getName()));
        }
        if (this.companyData.getAddress() != null) {
            lines.add(new PDFLine(this.companyData.getAddress()));
        }
        if (this.companyData.getZipCode() != null && this.companyData.getCity() != null) {
            lines.add(new PDFLine(this.companyData.getZipCode() + " " + this.companyData.getCity()));
        }
        if (this.companyData.getPhone() != null) {
            lines.add(new PDFLine(this.companyData.getPhone()));
        }
        if (this.companyData.getEmail() != null) {
            lines.add(new PDFLine(this.companyData.getEmail()));
        }
        if (this.companyData.getWebsite() != null) {
            lines.add(new PDFLine("Site internet : " + this.companyData.getWebsite()));
        }
        if (this.companyData.getSiret() != null) {
            lines.add(new PDFLine("SIRET : " + this.companyData.getSiret()));
        }
        if (this.companyData.getTvaCode() != null) {
            lines.add(new PDFLine("N°TVA : " + this.companyData.getTvaCode()));
        }

        super.margin = actualMargin;
        super.writePlainText(lines);

        super.margin = initialMargin;
        super.yPosition -= super.lineSpacing * 2;
    }
}
