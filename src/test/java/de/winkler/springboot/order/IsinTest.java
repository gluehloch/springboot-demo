package de.winkler.springboot.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IsinTest {

    @Test
    void isinToString() {
        ISIN isin = new ISIN();
        isin.setName("4711");
        assertThat(isin.toString()).isEqualTo("ISIN=[4711]");
    }

}
