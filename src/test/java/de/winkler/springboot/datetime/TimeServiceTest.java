package de.winkler.springboot.datetime;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.junit.jupiter.api.Test;

class TimeServiceTest {

    @Test
    void convertBetweenLocalDateTimeAndDateViaInstant() {
        LocalDateTime timestamp = LocalDateTime.of(2024, 3, 5, 12, 30, 15);

        Date converted = TimeService.convertToDateViaInstant(timestamp);

        assertThat(TimeService.convertToLocalDateTimeViaInstant(converted)).isEqualTo(timestamp);
    }

    @Test
    void convertDateToLocalDateConsistently() {
        LocalDate date = LocalDate.of(2024, 7, 11);

        Date converted = TimeService.convertToDateViaInstant(date);

        assertThat(TimeService.convertToLocalDateViaInstant(converted)).isEqualTo(date);
        assertThat(TimeService.convertToLocalDateViaMilisecond(converted)).isEqualTo(date);
    }
}
