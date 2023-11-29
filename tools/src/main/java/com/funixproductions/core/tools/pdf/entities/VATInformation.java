package com.funixproductions.core.tools.pdf.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * VAT information on european countries.
 * 0 for non european countries.
 */
@Getter
@RequiredArgsConstructor
public enum VATInformation {
    AUSTRALIA(10.0),
    AUSTRIA(20.0),
    BELGIUM(21.0),
    BULGARIA(20.0),
    CROATIA(25.0),
    CYPRUS(19.0),
    CZECH_REPUBLIC(21.0),
    DENMARK(25.0),
    ESTONIA(20.0),
    FINLAND(24.0),
    FRANCE(20.0),
    GERMANY(19.0),
    GREECE(24.0),
    HUNGARY(27.0),
    IRELAND(23.0),
    ITALY(22.0),
    LATVIA(21.0),
    LITHUANIA(21.0),
    LUXEMBOURG(16.0),
    MALTA(18.0),
    NETHERLANDS(21.0),
    NORWAY(25.0),
    POLAND(23.0),
    PORTUGAL(23.0),
    ROMANIA(19.0),
    SINGAPORE(8.0),
    SLOVAKIA(20.0),
    SLOVENIA(22.0),
    SOUTH_AFRICA(15.0),
    SPAIN(21.0),
    SWEDEN(25.0),
    SWITZERLAND(7.7),
    UNITED_KINGDOM(20.0);

    private final double vatRate;

}
