package de.winkler.springboot.order;

import static java.util.Objects.*;

import jakarta.persistence.Column;
import javax.persistence.Embeddable;
import java.text.MessageFormat;

@Embeddable
public class ISIN {

    @Column(name = "isin", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return MessageFormat.format("ISIN=[{0}]", this.name);
    }

    public static ISIN of(String name) {
        requireNonNull(name, "ISIN name is null.");

        ISIN isin = new ISIN();
        isin.setName(name);
        return isin;
    }

    public static ISIN of(String name, String description) {
        requireNonNull(description, "ISIN description is null.");

        ISIN isin = ISIN.of(name);
        isin.setDescription(description);
        return isin;
    }

}
