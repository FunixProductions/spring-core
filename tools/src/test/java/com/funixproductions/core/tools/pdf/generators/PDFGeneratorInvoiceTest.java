package com.funixproductions.core.tools.pdf.generators;

import com.funixproductions.core.tools.pdf.entities.InvoiceItem;
import com.funixproductions.core.tools.pdf.tools.VATInformation;
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
    void testGenerationWithManualInfo() {
        final List<InvoiceItemTest> itemsTest = List.of(
                new InvoiceItemTest("testItem 1", "Une super description de test. Le grade Pacifista, le Pacifista +, va vous permettre d'avoir des avantages exclusifs. " + UUID.randomUUID(), 1, 10.0),
                new InvoiceItemTest("testItem 2", "Une super description de test. Le grade Pacifista, le Pacifista +, va vous permettre d'avoir des avantages exclusifs. " + UUID.randomUUID(), 2, 20.0)
        );

        assertDoesNotThrow(() -> {
            try (final PDFInvoiceTest pdf = new PDFInvoiceTest("manualInvoicePdf", itemsTest)) {
                pdf.setInvoiceNumber("FP-0001224");
                pdf.setCgvUrl("https://www.pacifista.fr/cgv");
                pdf.setPaymentMethod("Paypal");
                pdf.setVatInformation(VATInformation.FRANCE);
                pdf.setInvoiceDescription("Voici un document de test pour la génération de documents de facturation. Vous pouvez y mettre toutes les informations que vous voulez. Super le dev !");
                pdf.init();
                this.generatePdf(pdf);
            }
        });
    }

    @Test
    void testInvoiceCreationSuccess() {
        final List<InvoiceItem> itemsTest = generateRandomItems(30);

        assertDoesNotThrow(() -> {
            try (final PDFInvoiceTest pdf = new PDFInvoiceTest("successInvoicePdf", itemsTest)) {
                pdf.setInvoiceNumber(Integer.toString(new Random().nextInt(1000000 - 1) + 1));
                pdf.setCgvUrl("https://www.pacifista.fr/cgv");
                pdf.setPaymentMethod("Paypal");
                pdf.setVatInformation(VATInformation.FRANCE);
                pdf.setPercentageDiscount(2.0);
                pdf.setInvoiceDescription("Voici un document de test pour la génération de documents de facturation. Vous pouvez y mettre toutes les informations que vous voulez. Super le dev !");
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
                            "Une super description de test. Le grade Pacifista, le Pacifista +, va vous permettre d'avoir des avantages exclusifs. " + UUID.randomUUID() + " randomNumber: " + random.nextInt(),
                            random.nextInt(100 - 1) + 1,
                            random.nextDouble(1000 - 1) + 1
                    )
            );
            ++i;
        }
        return items;
    }

    private static class PDFInvoiceTest extends PDFGeneratorInvoice {
        public PDFInvoiceTest(final String pdfName, final List<? extends InvoiceItem> invoiceItems) {
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
