package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.lang.Nullable;

import java.io.File;

@Setter
@Slf4j(topic = "PDFGeneratorHeaderFooter")
public abstract class PDFGeneratorWithHeaderAndFooter extends PDFGenerator {

    private static final String ERROR_SLF4J = "An error occurred while adding the header logo to the pdf {}";
    private static final String ERROR_API = "Une erreur est survenue lors de l'ajout du logo en header sur le pdf ";

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
    protected void newPage() throws ApiException {
        super.newPage();
        setHeaderLogoOnNewPage();
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
}
