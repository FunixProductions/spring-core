package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.entities.PDFLine;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

class PDFGeneratorTest {

    @Test
    void testGenerationWithPdfExtInName() {
        try (final PDFGenerator pdfGenerator = new PDFGeneratorImpl("test1.pdf")) {
            pdfGenerator.writePlainText(
                    List.of(
                            new PDFLine("This is a test Title.", 50, PDFGenerator.DEFAULT_FONT),
                            new PDFLine("This is a test pdf."),
                            new PDFLine("This is a test pdf with second line."),
                            new PDFLine("This is a test pdf with third line.")
                    )
            );
            final File file = new File("target","pdfTestOutDirGen");
            if (!file.exists() && !file.mkdir()) {
                throw new ApiException("Failed to create directory: " + file.getAbsolutePath());
            }
            pdfGenerator.generatePDF(file);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    @Test
    void testGenerationWithoutPdfExtInName() {
        try (final PDFGenerator pdfGenerator = new PDFGeneratorImpl("test2")) {
            pdfGenerator.writePlainText(
                    List.of(
                            new PDFLine("This is a test pdf."),
                            new PDFLine("This is a test pdf with second line."),
                            new PDFLine("This is a test pdf with third line.")
                    )
            );
            final File file = new File("target","pdfTestOutDirGen");
            if (!file.exists() && !file.mkdir()) {
                throw new ApiException("Failed to create directory: " + file.getAbsolutePath());
            }
            pdfGenerator.generatePDF(file);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    @Test
    void testGenerationWithLongText() {
        try (final PDFGenerator pdfGenerator = new PDFGeneratorImpl("test3-long-text")) {
            pdfGenerator.writePlainText(
                    List.of(
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it."),
                            new PDFLine("This is a test pdf with second line."),
                            new PDFLine("This is a test pdf with third line.")
                    )
            );
            final File file = new File("target","pdfTestOutDirGen");
            if (!file.exists() && !file.mkdir()) {
                throw new ApiException("Failed to create directory: " + file.getAbsolutePath());
            }
            pdfGenerator.generatePDF(file);
        } catch (final Exception e) {
            fail("Exception thrown on create test pdf: " + e.getMessage());
        }
    }

    private static class PDFGeneratorImpl extends PDFGenerator {
        public PDFGeneratorImpl(String pdfName) {
            super(pdfName);
            this.newPage();
        }
    }

}
