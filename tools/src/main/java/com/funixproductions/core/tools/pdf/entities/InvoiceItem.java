package com.funixproductions.core.tools.pdf.entities;

import lombok.NonNull;

/**
 * Extends this class to an object to be able to add it to an invoice.
 */
public interface InvoiceItem {

    /**
     * Get the invoice item name.
     * @return the invoice item name
     */
    @NonNull
    String getInvoiceItemName();

    /**
     * Get the invoice item description.
     * @return the invoice item description
     */
    @NonNull
    String getInvoiceItemDescription();

    /**
     * Get the invoice item quantity.
     * @return the invoice item quantity
     */
    int getInvoiceItemQuantity();

    /**
     * Get the invoice item price.
     * @return the invoice item price without tax
     */
    double getInvoiceItemPrice();

}
