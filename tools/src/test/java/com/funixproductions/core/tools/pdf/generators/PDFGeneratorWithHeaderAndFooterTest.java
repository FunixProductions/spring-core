package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.classpath.ImageReaderClasspath;
import com.funixproductions.core.tools.pdf.entities.PDFCompanyData;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class PDFGeneratorWithHeaderAndFooterTest {

    private static final PDFCompanyData PDF_COMPANY_DATA = new PDFCompanyData(
            "FunixProductions",
            "1 rue de la paix",
            "67000",
            "Strasbourg",
            "+33123456789",
            "contact@funixproductions.com",
            "pacifista.fr",
            "SIRET sdkjfsdlfkjhsdlfkj",
            "domidsfj7866"
    );

    @Test
    void testGeneratePDFHeaderFooterNoLogo() {
        try (final PDFGeneratorWithHeaderAndFooter pdfGenerator = new PDFHeaderFooterNoLogo()) {
            generateTestPdf(pdfGenerator);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    @Test
    void testGeneratePDFHeaderFooterWithLogoFile() {
        try (final PDFGeneratorWithHeaderAndFooter pdfGenerator = new PDFHeaderFooterWithLogoFile()) {
            generateTestPdf(pdfGenerator);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    @Test
    void testGeneratePDFHeaderFooterWithLogoFileAndNoCompanyData() {
        try (final PDFGeneratorWithHeaderAndFooter pdfGenerator = new PDFHeaderFooterWithLogoFileAndNoCompanyData()) {
            generateTestPdf(pdfGenerator);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    @Test
    void testGeneratePDFHeaderFooterWithLogoLoadedFromClasspath() {
        try (final PDFGeneratorWithHeaderAndFooter pdfGenerator = new PDFHeaderFooterWithLogoLoadedFromClasspath()) {
            generateTestPdf(pdfGenerator);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    private void generateTestPdf(final PDFGenerator pdfGenerator) throws Exception {
        pdfGenerator.writePlainText(List.of(
                new PDFLine("This is a test Title.", 50, PDFGenerator.DEFAULT_FONT, PDFGenerator.DEFAULT_FONT_COLOR),
                new PDFLine("This is a test pdf."),
                new PDFLine("This is a test pdf with second line."),
                new PDFLine("This is a test pdf with third line."))
        );
        final File file = new File("target","pdfTestOutDirGenHeaderFooter");
        if (!file.exists() && !file.mkdir()) {
            throw new ApiException("Failed to create directory: " + file.getAbsolutePath());
        }
        pdfGenerator.generatePDF(file);
    }

    private static class PDFHeaderFooterNoLogo extends PDFGeneratorWithHeaderAndFooter {
        public PDFHeaderFooterNoLogo() {
            super("testPdf-header-footer-nologo", PDF_COMPANY_DATA);
            super.newPage();
        }
    }

    private static class PDFHeaderFooterWithLogoFile extends PDFGeneratorWithHeaderAndFooter {
        public PDFHeaderFooterWithLogoFile() {
            super("testPdf-header-footer-withlogo", PDF_COMPANY_DATA, new File("target/test-classes/logo-pacifista.png"));
            super.newPage();
        }
    }

    private static class PDFHeaderFooterWithLogoFileAndNoCompanyData extends PDFGeneratorWithHeaderAndFooter {
        public PDFHeaderFooterWithLogoFileAndNoCompanyData() {
            super("testPdf-header-footer-withlogo-nocompanydata", null, new File("target/test-classes/logo-pacifista.png"));
            super.newPage();
        }
    }

    private static class PDFHeaderFooterWithLogoLoadedFromClasspath extends PDFGeneratorWithHeaderAndFooter {
        public PDFHeaderFooterWithLogoLoadedFromClasspath() {
            super("testPdf-header-footer-withlogo-classpath", PDF_COMPANY_DATA, new ImageReaderClasspath(
                    PDFHeaderFooterWithLogoLoadedFromClasspath.class,
                    "logo-pacifista.png",
                    "png"
            ).getBytes(), "logo-pacifista.png");
            super.newPage();
        }
    }

}
