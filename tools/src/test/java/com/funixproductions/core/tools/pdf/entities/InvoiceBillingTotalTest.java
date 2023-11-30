package com.funixproductions.core.tools.pdf.entities;


import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.tools.VATInformation;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class InvoiceBillingTotalTest {

    @Test
    void testBillingNoVatNoDiscountSingleItem() {
        runTest(10, 1, null, null);
    }

    @Test
    void testBillingNoVatNoDiscountMultipleItems() {
        runTest(20, 3, null, null);
    }

    @Test
    void testBillingWithVatNoDiscount() {
        runTest(30, 2, null, VATInformation.FRANCE);
    }

    @Test
    void testBillingWithVatAndDiscount() {
        runTest(40, 2, 50.0, VATInformation.FRANCE);
    }

    @Test
    void testBillingWithNoVatAndDiscount() {
        runTest(50, 2, 50.0, null);
    }

    @Test
    void testBillingItemNegativePrice() {
        assertThrowsExactly(ApiException.class, () -> runTest(-10, 2, null, null));
    }

    @Test
    void testBillingItemNegativeQuantity() {
        assertThrowsExactly(ApiException.class, () -> runTest(10, -2, null, null));
    }

    @Test
    void testBillingPercentageNegative() {
        assertThrowsExactly(ApiException.class, () -> runTest(10, 2, -10.0, null));
    }

    @Test
    void testBillingPercentageSuperiorAt100() {
        assertThrowsExactly(ApiException.class, () -> runTest(10, 2, 130.0, null));
    }

    private void runTest(final double itemPrice,
                         final int quantity,
                         @Nullable Double percentageDiscount,
                         @Nullable VATInformation vatInformation) {
        final List<InvoiceItem> items = List.of(
                new FakeItem(quantity, itemPrice)
        );
        final InvoiceBillingTotal invoiceBillingTotal = new InvoiceBillingTotal(
                items, percentageDiscount, vatInformation
        );

        final double totalHtBeforeDiscount = itemPrice * quantity;
        final double amountDiscount = percentageDiscount == null ? 0 : totalHtBeforeDiscount * (percentageDiscount / 100);
        final double priceHt = totalHtBeforeDiscount - amountDiscount;
        final double vatAmount = vatInformation == null ? 0 : priceHt * (vatInformation.getVatRate() / 100);
        final double totalTtc = priceHt - vatAmount;

        assertEquals(totalHtBeforeDiscount, invoiceBillingTotal.getTotalHtPriceNoDiscountAmount());
        assertEquals(amountDiscount, invoiceBillingTotal.getDiscountAmount());
        assertEquals(priceHt, invoiceBillingTotal.getTotalHtPrice());
        assertEquals(vatAmount, invoiceBillingTotal.getVatAmount());
        assertEquals(totalTtc, invoiceBillingTotal.getTotalTtcPrice());
    }

    @Getter
    private static class FakeItem implements InvoiceItem {
        private final String invoiceItemName;
        private final String invoiceItemDescription;
        private final int invoiceItemQuantity;
        private final double invoiceItemPrice;

        public FakeItem(int quantity, double price) {
            this.invoiceItemName = UUID.randomUUID().toString();
            this.invoiceItemDescription = UUID.randomUUID().toString();
            this.invoiceItemQuantity = quantity;
            this.invoiceItemPrice = price;
        }
    }

}
