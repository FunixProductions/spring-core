package com.funixproductions.core.tools.pdf.entities;

import com.funixproductions.core.exceptions.ApiException;
import com.funixproductions.core.tools.pdf.tools.VATInformation;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
public class InvoiceBillingTotal {

    /**
     * Prix de la facture sans les taxes ni la remise
     */
    private final double totalHtPriceNoDiscountAmount;

    /**
     * Valeur de la remise par rapport au total ht
     */
    private final double discountAmount;

    /**
     * Prix de la facture hors taxes avec la remsie comprise
     */
    private final double totalHtPrice;

    /**
     * Valeur des taxes par rapport au total ht avec la remise
     */
    private final double vatAmount;

    /**
     * Prix final de la facture toutes taxes comprises avec la tva et la remise
     */
    private final double totalTtcPrice;

    public InvoiceBillingTotal(@NonNull final List<InvoiceItem> invoiceItems,
                               @Nullable final Double percentageDiscount,
                               @Nullable final VATInformation taxInformation) throws ApiException {
        if (percentageDiscount != null) {
            if (percentageDiscount < 0) {
                throw new ApiException("Il est impossible d'appliquer une réduction inférieure à 0.");
            } else if (percentageDiscount > 100) {
                throw new ApiException("Il est impossible d'appliquer une réduction supérieure à 100.");
            }
        }

        this.totalHtPriceNoDiscountAmount = calculateTotalHtItems(invoiceItems);
        this.discountAmount = calculateDiscount(percentageDiscount);
        this.totalHtPrice = calculateFinalHt();
        this.vatAmount = calculateVatAmount(taxInformation);
        this.totalTtcPrice = calculateFinalTtcPrice();
    }

    private double calculateTotalHtItems(final List<InvoiceItem> invoiceItems) throws ApiException {
        double total = 0.0;

        for (final InvoiceItem item : invoiceItems) {
            if (item.getInvoiceItemPrice() < 0 || item.getInvoiceItemQuantity() < 1) {
                throw new ApiException("Un item de la facture possède un prix négatif ou une quantité nulle ou négative.");
            }
            total += item.getInvoiceItemPrice() * item.getInvoiceItemQuantity();
        }
        return total;
    }

    private double calculateDiscount(final Double percentageDiscount) {
        if (percentageDiscount == null || percentageDiscount <= 0) {
            return 0;
        }

        return this.totalHtPriceNoDiscountAmount * (percentageDiscount / 100);
    }

    private double calculateFinalHt() {
        if (this.discountAmount <= 0) {
            return this.totalHtPriceNoDiscountAmount;
        }

        return this.totalHtPriceNoDiscountAmount - discountAmount;
    }

    private double calculateVatAmount(@Nullable final VATInformation taxInformation) {
        if (taxInformation == null) {
            return 0;
        }

        return this.totalHtPrice * (taxInformation.getVatRate() / 100);
    }

    private double calculateFinalTtcPrice() {
        if (this.vatAmount <= 0) {
            return this.totalHtPrice;
        }

        return this.totalHtPrice - vatAmount;
    }

}
