package com.funixproductions.core.tools.time;

import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TimeUtils {

    private static final ZoneId PARIS_ZONE_ID = ZoneId.of("Europe/Paris");
    private static final Locale PARIS_LOCALE = Locale.FRANCE;

    public static Duration diffBetweenInstants(final Instant start, final Instant end) {
        return Duration.between(start, end);
    }

    public static long diffInMillisBetweenInstants(final Instant start, final Instant end) {
        return ChronoUnit.MILLIS.between(start, end);
    }

    public static long diffInSecondsBetweenInstants(final Instant start, final Instant end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    public static Instant getDateFromPattern(final String pattern) {
        return Instant.parse(pattern);
    }

    public static Instant getDateFromMillis(final long millis) {
        return Instant.ofEpochMilli(millis);
    }

    /**
     * Returns actual date in French format
     * @return Instant date
     */
    public static Instant getTimeFromFrenchZone() {
        final LocalDateTime now = LocalDateTime.now(PARIS_ZONE_ID);
        return now.toInstant(PARIS_ZONE_ID.getRules().getOffset(now));
    }

    /**
     * Returns actual date in French format
     * @param datePattern parsing pattern
     * @param dateString date to parse
     * @return Instant date
     */
    public static Instant getTimeFromFrenchZone(final String datePattern, final String dateString) {
        final LocalDateTime now = LocalDateTime.parse(
                dateString,
                DateTimeFormatter.ofPattern(datePattern, PARIS_LOCALE)
        );

        return now.toInstant(PARIS_ZONE_ID.getRules().getOffset(now));
    }

}
