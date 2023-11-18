package com.funixproductions.core.tools.pdf.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PDFCompanyData {

    @Nullable
    private String name;

    @Nullable
    private String address;

    @Nullable
    private String zipCode;

    @Nullable
    private String city;

    @Nullable
    private String phone;

    @Nullable
    private String email;

    @Nullable
    private String website;

    @Nullable
    private String siret;

    @Nullable
    private String tvaCode;

}
