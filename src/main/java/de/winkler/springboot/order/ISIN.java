package de.winkler.springboot.order;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.text.MessageFormat;

@Embeddable
public class ISIN {

    @Column(name = "isin", length = 50, nullable = false, unique = true)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return MessageFormat.format("ISIN=[{0}]", this.name);
    }
}
