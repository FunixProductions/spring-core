package com.funixproductions.core.tools.pdf.entities;

import org.springframework.lang.Nullable;

/**
 * This interface represents the data of a company.
 */
public interface PDFCompanyData {

    /**
     * @return the name of the company
     */
    @Nullable
    String getName();

    /**
     * @return the address of the company
     */
    @Nullable
    String getAddress();

    /**
     * @return the zip code of the company
     */
    @Nullable
    String getZipCode();

    /**
     * @return the city of the company
     */
    @Nullable
    String getCity();

    /**
     * @return the phone number of the company
     */
    @Nullable
    String getPhone();

    /**
     * @return the email of the company
     */
    @Nullable
    String getEmail();

    /**
     * @return the website of the company
     */
    @Nullable
    String getWebsite();

    /**
     * @return the siret of the company
     */
    @Nullable
    String getSiret();

    /**
     * @return the tva code of the company
     */
    @Nullable
    String getTvaCode();

}
