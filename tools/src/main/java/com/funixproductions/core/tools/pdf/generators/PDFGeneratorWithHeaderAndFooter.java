package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;

@Setter
@Slf4j(topic = "PDFGeneratorHeaderFooter")
public abstract class PDFGeneratorWithHeaderAndFooter extends PDFGenerator {

    private PDImageXObject headerLogo;

    /**
     * Constructor.
     *
     * @param pdfName the name of the PDF file
     */
    protected PDFGeneratorWithHeaderAndFooter(@NonNull String pdfName) {
        super(pdfName);
    }

    @Override
    protected void newPage() throws ApiException {
        super.newPage();

        if (this.headerLogo != null) {
            try {
                super.contentStream.drawImage(this.headerLogo, 50, 50);
                super.yPosition += this.headerLogo.getHeight() + super.lineSpacing;
            } catch (final Exception e) {
                log.error("An error occurred while adding the header logo to the pdf {}", super.pdfName, e);
                throw new ApiException("Une erreur est survenue lors de l'ajout du logo en header sur le pdf " + super.pdfName, e);
            }
        }
    }

    public void setHeaderLogo(File headerLogoFile) throws ApiException {
        try {
            if (!headerLogoFile.exists()) {
                throw new ApiException("Header logo file does not exist: " + headerLogoFile.getAbsolutePath());
            }
            this.headerLogo = PDImageXObject.createFromFile(headerLogoFile.getPath(), super.pdfDocument);
        } catch (final Exception e) {
            log.error("An error occurred while adding the header logo to the pdf {}", super.pdfName, e);
            throw new ApiException("Une erreur est survenue lors de l'ajout du logo en header sur le pdf " + super.pdfName, e);
        }
    }
}
