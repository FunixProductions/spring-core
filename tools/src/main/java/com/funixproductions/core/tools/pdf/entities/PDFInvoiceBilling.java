package com.funixproductions.core.tools.pdf.entities;

import lombok.NonNull;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;

public interface PDFInvoiceBilling {
    /**
     * Get the invoice date.
     * @return the invoice date
     */
    @NonNull
    Date getInvoiceDate();

    /**
     * Get the invoice number prefix.
     * @return the invoice number prefix like FR
     */
    @NonNull
    String getInvoiceNumberPrefix();

    /**
     * Get the invoice number.
     * @return the invoice number
     */
    int getInvoiceNumber();

    /**
     * Get the invoice payment method.
     * @return the invoice payment method example PayPal or Credit Card
     */
    @NonNull
    String getInvoicePaymentMethod();

    /**
     * Get the invoice customer name.
     * @return the invoice customer name
     */
    @NonNull
    String getInvoiceCustomerName();

    /**
     * Get the invoice customer email.
     * @return the invoice customer email
     */
    @NonNull
    String getInvoiceCustomerEmailAddress();

    /**
     * Get the invoice customer address.
     * @return the invoice customer country address null if not in europe
     */
    @Nullable
    VATInformation getVATInformation();

    /**
     * Get items of the invoice.
     * @return the invoice items
     */
    @NonNull
    List<InvoiceItem> getInvoiceItems();
}
