package com.funixproductions.core.tools.pdf.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class PDFCompanyData {

    private String name;

    private String address;

    private String zipCode;

    private String city;

    private String phone;

    private String email;

    private String website;

    private String siret;

    private String tvaCode;

}
