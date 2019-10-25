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

    private Date dateTimeWithoutFormatDefinition;
 
    // -- Joda DateTime

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private DateTime jodaDateTimeBerlin;

    // -- java.time.LocalDateTime

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "UTC")
    private LocalDateTime localDateTimeUTC;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private LocalDateTime localDateTimeBerlin;

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
    
    public DateTime getJodaDateTimeBerlin() {
        return jodaDateTimeBerlin;
    }

    public void setDateTime(Date dateTime) {
        this.dateTimeUTC = dateTime;
        this.dateTimeBerlin = dateTime;
        this.dateTimeWithoutFormatDefinition = dateTime;
        this.localDateTimeUTC = dateTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        this.localDateTimeBerlin = dateTime.toInstant().atZone(ZoneId.of("Europe/Berlin")).toLocalDateTime();
        // this.localDateTime = dateTime.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        // this.jodaDateTimeBerlin = new DateTime(dateTime.getTime());
        this.jodaDateTimeBerlin = DateTime.now();
        /* 
        DateTime.parse(
                "2015-01-01 15:10:00",
                DateTimeFormat.forPattern("yy-MM-dd HH:mm:ss")).withZone(DateTimeZone.forID("Europe/Berlin"));
        */
    }

    public Date getDateTimeWithoutFormatDefinition() {
        return dateTimeWithoutFormatDefinition;
    }

}
