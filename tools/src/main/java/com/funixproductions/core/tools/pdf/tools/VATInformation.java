package com.funixproductions.core.tools.pdf.tools;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * VAT information on countries.
 * 0 for non vat countries.
 */
@Getter
@RequiredArgsConstructor
public enum VATInformation {
    AUSTRALIA(10.0, "AU"),
    AUSTRIA(20.0, "AT"),
    BELGIUM(21.0, "BE"),
    BULGARIA(20.0, "BG"),
    CROATIA(25.0, "HR"),
    CYPRUS(19.0, "CY"),
    CZECH_REPUBLIC(21.0, "CZ"),
    DENMARK(25.0, "DK"),
    ESTONIA(22.0, "EE"),
    FINLAND(24.0, "FI"),
    FRANCE(20.0, "FR"),
    GERMANY(19.0, "DE"),
    GREECE(24.0, "GR"),
    HUNGARY(27.0, "HU"),
    IRELAND(23.0, "IE"),
    ITALY(22.0, "IT"),
    LATVIA(21.0, "LV"),
    LITHUANIA(21.0, "LT"),
    LUXEMBOURG(17.0, "LU"),
    MALTA(18.0, "MT"),
    NETHERLANDS(21.0, "NL"),
    NORWAY(25.0, "NO"),
    POLAND(23.0, "PL"),
    PORTUGAL(23.0, "PT"),
    ROMANIA(19.0, "RO"),
    SINGAPORE(9.0, "SG"),
    SLOVAKIA(20.0, "SK"),
    SLOVENIA(22.0, "SI"),
    SOUTH_AFRICA(15.0, "ZA"),
    SPAIN(21.0, "ES"),
    SWEDEN(25.0, "SE"),
    SWITZERLAND(8.1, "CH"),
    UNITED_KINGDOM(20.0, "GB");

    private final double vatRate;
    private final String countryCode;

}
