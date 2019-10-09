package de.winkler.restdemo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.joda.time.DateTime;

public class PingDateTime {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "Europe/Berlin")
    private Date dateTimeBerlin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm", locale = "de_DE", timezone = "UTC")
    private Date dateTimeUtc;

    private DateTime jodaDateTime;
    
    private Date dateTimeMillies;

    public Date getDateTimeUtc() {
        return dateTimeUtc;
    }

    public Date getDateTimeBerlin() {
        return dateTimeBerlin;
    }
    
    public DateTime getJodaDateTime() {
        return jodaDateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTimeUtc = dateTime;
        this.dateTimeBerlin = dateTime;
        this.dateTimeMillies = dateTime;
        this.jodaDateTime = new DateTime(dateTime.getTime());
    }

    public Date getDateTimeMillies() {
        return dateTimeMillies;
    }

}
