package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import lombok.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class PDFGeneratorInvoiceTest {

    private static final PDFGeneratorWithHeaderAndFooterTest.CompanyData PDF_COMPANY_DATA = new PDFGeneratorWithHeaderAndFooterTest.CompanyData(
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

    private static final PDFGeneratorWithHeaderAndFooterTest.CompanyData PDF_BUYER_DATA = new PDFGeneratorWithHeaderAndFooterTest.CompanyData(
            "Microsoft",
            "10 rue du fric",
            "10000",
            "New York",
            "+33123456789",
            "contact@microsoft.com",
            "microsoft.com",
            "SIRET MICROSOFT TEST",
            "101010101010"
    );

    @Test
    void testInvoiceCreationSuccess() {
        final List<InvoiceItem> itemsTest = generateRandomItems(10);

        assertDoesNotThrow(() -> {
            try (final PDFInvoiceTest pdf = new PDFInvoiceTest("successInvoicePdf", itemsTest)) {
                pdf.init();
                this.generatePdf(pdf);
            }
        });
    }

    private List<InvoiceItem> generateRandomItems(final int amount) {
        final List<InvoiceItem> items = new ArrayList<>();
        final Random random = new Random();
        int i = 0;

        while (i < amount) {
            items.add(
                    new InvoiceItemTest(
                            "testItem " + i,
                            "Une super description de test. " + UUID.randomUUID(),
                            random.nextInt(100),
                            random.nextDouble(1000)
                    )
            );
            ++i;
        }
        return items;
    }

    private static class PDFInvoiceTest extends PDFGeneratorInvoice {
        public PDFInvoiceTest(final String pdfName, final List<InvoiceItem> invoiceItems) {
            super(pdfName, PDF_COMPANY_DATA, new File("target/test-classes/logo-pacifista.png"), invoiceItems, PDF_BUYER_DATA);
        }
    }

    private void generatePdf(final PDFGeneratorInvoice invoice) throws Exception {
        final File folder = new File("target", "invoicePdfGenTest");

        if (!folder.exists() && !folder.mkdir()) {
            throw new IOException("cant create test folder");
        }

        invoice.generatePDF(folder);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class InvoiceItemTest implements InvoiceItem {
        private String name;
        private String description;
        private Integer quantity;
        private Double price;

        @Override
        public @NonNull String getInvoiceItemName() {
            return name;
        }

        @Override
        public @NonNull String getInvoiceItemDescription() {
            return description;
        }

        @Override
        public int getInvoiceItemQuantity() {
            if (quantity == null || quantity < 0) {
                return 0;
            } else {
                return quantity;
            }
        }

        @Override
        public double getInvoiceItemPrice() {
            if (price == null || price < 0) {
                return 0;
            } else {
                return price;
            }
        }
    }

}
