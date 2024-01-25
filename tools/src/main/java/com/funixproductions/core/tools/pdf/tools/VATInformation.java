package com.funixproductions.core.tools.pdf.tools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * VAT information on countries.
 * 0 for non eu vat countries.
 */
@Getter
@RequiredArgsConstructor
public enum VATInformation {
    AUSTRIA(20.0, "AT"),
    BELGIUM(21.0, "BE"),
    BULGARIA(20.0, "BG"),
    CROATIA(25.0, "HR"),
    CYPRUS(19.0, "CY"),
    CZECH_REPUBLIC(21.0, "CZ"),
    DENMARK(25.0, "DK"),
    ESTONIA(20.0, "EE"),
    FINLAND(24.0, "FI"),
    FRANCE(20.0, "FR"),
    GERMANY(19.0, "DE"),
    GREECE(24.0, "EL"),
    HUNGARY(27.0, "HU"),
    IRELAND(23.0, "IE"),
    ITALY(22.0, "IT"),
    LATVIA(21.0, "LV"),
    LITHUANIA(21.0, "LT"),
    LUXEMBOURG(17.0, "LU"),
    MALTA(18.0, "MT"),
    NETHERLANDS(21.0, "NL"),
    POLAND(23.0, "PL"),
    PORTUGAL(23.0, "PT"),
    ROMANIA(19.0, "RO"),
    SLOVAKIA(20.0, "SK"),
    SLOVENIA(22.0, "SI"),
    SPAIN(21.0, "ES"),
    SWEDEN(25.0, "SE");

    private final double vatRate;
    private final String countryCode;

    @Nullable
    public static VATInformation getVATInformation(String countryCode) {
        for (final VATInformation vatInformation : VATInformation.values()) {
            if (vatInformation.getCountryCode().equalsIgnoreCase(countryCode)) {
                return vatInformation;
            }
        }
        return null;
    }

    public static double getVATRate(String countryCode) {
        final VATInformation vatInformation = getVATInformation(countryCode);

        if (vatInformation != null) {
            return vatInformation.getVatRate();
        }
        return 0.0;
    }

    @Nullable
    public static VATInformation isRateValid(double vatRate) {
        for (final VATInformation vatInformation : VATInformation.values()) {
            if (vatInformation.getVatRate() == vatRate) {
                return vatInformation;
            }
        }
        return null;
    }

}
