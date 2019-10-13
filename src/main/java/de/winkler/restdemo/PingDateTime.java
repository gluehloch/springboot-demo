package de.winkler.restdemo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.joda.time.DateTime;

public class PingDateTime {

    // -- java.util.Date

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private Date dateTimeBerlin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "UTC")
    private Date dateTimeUTC;

    private Date dateTimeMillies;
 
    // -- Joda DateTime

    private DateTime jodaDateTime;

    // -- java.time.LocalDateTime

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "UTC")
    private LocalDateTime localDateTimeUTC;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private LocalDateTime localDateTimeBerlin;

    private LocalDateTime localDateTime;

    public Date getDateTimeUTC() {
        return dateTimeUTC;
    }

    public Date getDateTimeBerlin() {
        return dateTimeBerlin;
    }

    public LocalDateTime getLocalDateTimeUTC() {
        return localDateTimeUTC;
    }

    public LocalDateTime getLocalDateTimeBerlin() {
        return localDateTimeBerlin;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
    
    public DateTime getJodaDateTime() {
        return jodaDateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTimeUTC = dateTime;
        this.dateTimeBerlin = dateTime;
        this.dateTimeMillies = dateTime;
        this.localDateTimeUTC = dateTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        this.localDateTimeBerlin = dateTime.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
        this.localDateTime = dateTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        this.jodaDateTime = new DateTime(dateTime.getTime());
    }

    public Date getDateTimeMillies() {
        return dateTimeMillies;
    }

}
