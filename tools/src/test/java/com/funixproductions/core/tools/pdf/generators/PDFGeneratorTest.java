package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.exceptions.ApiException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.fail;

class PDFGeneratorTest {

    @Test
    void testGenerationWithPdfExtInName() {
        try (final PDFGenerator pdfGenerator = new PDFGeneratorImpl("test1.pdf")) {
            pdfGenerator.writePlainText(
                    new String[] {
                            "This is a test pdf.",
                            "This is a test pdf with second line.",
                            "This is a test pdf with third line."
                    },
                    PDFGenerator.DEFAULT_FONT_SIZE,
                    PDFGenerator.DEFAULT_FONT
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
                    new String[] {
                            "This is a test pdf.",
                            "This is a test pdf with second line.",
                            "This is a test pdf with third line."
                    },
                    PDFGenerator.DEFAULT_FONT_SIZE,
                    PDFGenerator.DEFAULT_FONT
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
                    new String[] {
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf. With a super big line you see ! ahahahahahahaahahhaa this is so funny ! Let's see if it works ! I hope so ! I really hope so ! I really really hope so ! I really really really hope so ! I really really really really hope so ! I really really really really really hope so ! I really really really really really really hope so ! I really really really really really really really hope so ! I really really really really really really really really hope so ! I really really really really really really really really really hope so ! I really really really. OK got it.",
                            "This is a test pdf with second line.",
                            "This is a test pdf with third line."
                    },
                    PDFGenerator.DEFAULT_FONT_SIZE,
                    PDFGenerator.DEFAULT_FONT
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
        }
    }

}
